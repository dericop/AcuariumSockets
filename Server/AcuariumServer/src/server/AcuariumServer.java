/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import model.Fish;
import model.FishConstants;
import org.json.JSONArray;

/**
 *
 * @author dericop
 */
public class AcuariumServer implements Runnable {

    private int sessionID = 1;
    private int confSessionID = 1;
    private String name = "";
    private int configurationPort = 0;
    private String configurationToken = "";
    private int screenPort = 0;
    private String screenToken = "XLS345";
    private LinkedList<AcuariumScreen> screenClients = new LinkedList();
    private ConfigurationSERequest configurationClient = null;
    private ServerSocket serverSocket = null;
    private List<Fish> fishes = Collections.synchronizedList(new LinkedList());
    private LinkedList<Integer> fishtypes = new LinkedList();

    private PrintWriter out = null;
    private BufferedReader in = null;

    private final Thread acuariumThreads = new Thread(this);

    public AcuariumServer(String name, int confPort, String confToken, int screenPort, String screenToken) {
        this.name = name;
        this.configurationPort = confPort;
        this.configurationToken = confToken;
        this.screenPort = screenPort;
        this.screenToken = screenToken;

        //inicializar tipos de peces
        fishtypes.add(1);
        fishtypes.add(2);
        fishtypes.add(3);

        fishes.add(new Fish(2));
        fishes.add(new Fish(1));
        fishes.add(new Fish(3));

    }

    boolean isTokenVisualizationValid(String token) {
        return AcuariumConstants.visualizationToken.equals(token);
    }

    boolean isTokenConfigurationValid(String token) {
        return token.equals(AcuariumConstants.configurationToken);
    }

    void commandError() {
        out.println(ResponseConstants.NOT_FOUND_404);
    }

    public boolean changeFishDirection(int fishId) {

        ListIterator<Fish> it = fishes.listIterator();

        Fish fish;
        while (it.hasNext()) {
            fish = it.next();
            if (fish.getId() == fishId) {
                fish.changeDirection();
                return true;
            }
        }

        return false;
    }

    private boolean isValidNumber(String digit) {
        for (int i = 0; i < digit.length(); i++) {
            if (!Character.isDigit(digit.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private void updateScreenIds() {
        this.sessionID = 1;
        for (AcuariumScreen ac : screenClients) {
            ac.setId(sessionID);
            ac.writeMessage(ResponseConstants.SUCCESS_100(sessionID + ""));
            this.sessionID++;
        }
    }

    public boolean deleteScreen(int screen) {
        System.out.println("Quiero borrar: " + screen);
        screen = screen - 1;
        if (screen >= 0 && screen < this.screenClients.size()) {
            this.screenClients.remove(screen);
            System.out.println("Borrado: " + this.screenClients);
            updateScreenIds();

            return true;
        } else {
            return false;
        }
    }

    public boolean addFish(int type, int position) {
        if (this.fishtypes.contains(type) && position <= this.screenClients.size() * AcuariumConstants.resolution) {
            Fish newFish = new Fish(type);
            newFish.setX(position);
            this.fishes.add(newFish);
            return true;
        }
        return false;
    }

    void registerScreen(Socket socket) {

        this.screenClients.addLast(new AcuariumScreen(sessionID, socket, this));
        System.out.println("out:" + out);
        System.out.println("Tamaño:" + this.screenClients.size());

        out.println(ResponseConstants.SUCCESS_100(sessionID + ""));
    }

    void forbiddenError() {
        out.println(ResponseConstants.FORBIDDEN_403);
    }

    void initScreenConnection(String token, Socket socket) {
        System.out.println("conexion");

        if (isTokenVisualizationValid(token.trim())) {
            registerScreen(socket);

        } else {
            forbiddenError();
        }
    }

    void initConfigureConnection(String tokens, Socket socket) {
        String[] toks = tokens.split(" ");
        System.out.println(toks.length);
        System.out.println(Arrays.asList(toks));

        if (toks.length == 2) {
            String confToken = toks[0];
            String usrToken = toks[1];

            if (AcuariumConstants.configurationToken.equals(confToken)) {
                AcuariumConstants.visualizationToken = usrToken;
                configurationClient = new ConfigurationSERequest(confSessionID, socket, this);
                confSessionID++;
                out.println(ResponseConstants.SUCCESS_100("{\"id\":" + sessionID + "}"));

            } else {
                commandError();
            }

        } else {
            commandError();
        }
    }

    public void start() {
        Socket socket;
        ConfigurationSERequest confClient;

        boolean quit = false;
        String readLine;

        //hilo de ubicaciones de peces
        acuariumThreads.start();

        try {
            serverSocket = new ServerSocket(screenPort);

            while (!quit) {
                try {
                    System.out.println("ACUARIO - Esperando Usuarios...");
                    socket = serverSocket.accept();

                    out = new PrintWriter(socket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

                    readLine = in.readLine();
                    System.out.println(readLine);

                    if (!readLine.trim().equals(" ")) {
                        //CL START 5521
                        if (readLine.startsWith("CL START")) {
                            //USUARIO DE PANTALLA
                            System.out.println(readLine);
                            initScreenConnection(readLine.substring(8), socket);
                            sessionID++;

                        } else if (readLine.startsWith("START CONF")) {
                            //USUARIO DE CONFIGURACIÓN
                            initConfigureConnection(readLine.substring(11), socket);
                        }
                    }

                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }

            }
            serverSocket.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    synchronized LinkedList<AcuariumScreen> getScreenClients() {
        return this.screenClients;
    }

    public void clearClientFishList() {
        for (AcuariumScreen acuariumScreen : screenClients) {
            acuariumScreen.getFishes().clear();
        }
    }

    private void updateFishInScreen(Fish fish, int posx, int screen) {
        try {
            Fish fishCopy = (Fish) fish.clone();
            fishCopy.setX(posx);
            screenClients.get(screen).getFishes().push(fishCopy);

            JSONArray data = new JSONArray(screenClients.get(screen).getFishes());
            screenClients.get(screen).writeMessage("{\"status\":200,\"detail\":{\"data\":" + data.toString() + "}" + "}");

        } catch (CloneNotSupportedException e) {
            System.out.println(e.getMessage());
        }

    }

    public synchronized void scareFishesInScreen(int x, int y, int screen) {
        //ORGANIZAR
        int realScreen = screen - 1;
        int realx = (x + (realScreen * AcuariumConstants.resolution));
        System.out.println(this.screenClients.size());
        if (realScreen >= 0 && realScreen < this.screenClients.size()) {

            for (Fish f : fishes) {

                System.out.println("Real x: " + realx + " X: " + f.getX() + "Y: " + f.getY() + "");

                if (realx <= f.getX() + 500 /*Derecha*/ && realx >= f.getX() - 500 /*Derecha*/
                        && y >= f.getY() - 500 && y <= f.getY() + 500) {
                    System.out.println("Espantadooooo");
                    f.setScareTime(200);
                }
            }
        }

    }

    private void updateFishPositions(Fish fish) {
        int endRef = -1;
        int initRef = 0;

        endRef = AcuariumConstants.resolution * this.screenClients.size();

        if (fish.getX() + (int) fish.getWidth() / 2 >= endRef && fish.getDirection() == 1) {
            fish.setDirection(-1);
        } else if (fish.getX() - (int) fish.getWidth() / 2 <= 0 && fish.getDirection() == -1) {
            fish.setDirection(1);
        }

        if (fish.getDirection() == -1) {

            fish.setX(fish.getX() - fish.getVelocity());

        } else {
            fish.setX(fish.getX() + fish.getVelocity());
        }

    }

    public LinkedList<Integer> getFishtypes() {
        return fishtypes;
    }

    private void delay(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void run() {
        boolean quit = false;
        int screen1 = -1;
        int xpos1 = -1;
        int screen2 = -1;
        int xpos2 = -1;

        while (!quit) {
            delay(10);
            //System.out.println(this.screenClients);
            if (!screenClients.isEmpty()) {

                ListIterator<Fish> it = fishes.listIterator();

                clearClientFishList();
                while (it.hasNext()) {
                    synchronized (fishes) {
                        Fish fish = it.next();

                        updateFishPositions(fish);
                        int radius = (int) fish.getWidth() / 2;

                        screen1 = (int) (fish.getX() - radius) / AcuariumConstants.resolution;

                        //if ((int) fish.getWidth() / 2 <= fish.getX()) {
                        xpos1 = (int) (((fish.getX() - radius) % AcuariumConstants.resolution) + radius);
                        //}

                        screen2 = (int) (fish.getX() + radius) / AcuariumConstants.resolution;

                        xpos2 = (int) (((fish.getX() + radius) % AcuariumConstants.resolution) - radius);

                        //System.out.println("Pos1: " + fish.getX() + " Pos2:" + xpos2 + " Screen1: " + screen1 + " Screen2: " + screen2);
                        if (screen2 < screenClients.size()) {

                            updateFishInScreen(fish, xpos1, screen1);

                            if (screen1 != screen2) {
                                //Enviar a cliente 1 y 2
                                updateFishInScreen(fish, xpos2, screen2);

                            }

                        }
                    }

                }
                for (AcuariumScreen ac : this.screenClients) {
                    JSONArray data = new JSONArray(ac.getFishes());
                    ac.writeMessage("{\"status\":200,\"detail\":{\"data\":" + data.toString() + "}" + "}");
                }
            }

        }
    }

    public synchronized boolean deleteFish(int id) {
        ListIterator<Fish> it = fishes.listIterator();

        while (it.hasNext()) {
            Fish f = it.next();
            if (f.getId() == id) {
                it.remove();
                return true;
            }
        }

        return false;
    }

    public List<Fish> getFishes() {
        return fishes;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AcuariumServer server = new AcuariumServer("Mi server", 5521, "", 5521, "");
        server.start();
    }

}
