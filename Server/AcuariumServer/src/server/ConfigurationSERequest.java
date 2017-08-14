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
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;

/**
 *
 * @author dericop
 */
public final class ConfigurationSERequest implements Runnable {

    private int id = 0;
    private AcuariumServer server = null;
    private Thread thread = null;
    private Socket socket = null;
    private PrintWriter theOut = null;
    private BufferedReader theIn = null;
    private String readLine = "";

    public ConfigurationSERequest(int id, Socket socket, AcuariumServer server) {
        this.id = id;
        this.socket = socket;
        this.server = server;
        open();
    }

    public void open() {
        try {
            theOut = new PrintWriter(socket.getOutputStream(), true);
            theIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        } catch (IOException ex) {
            Logger.getLogger(AcuariumScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
        thread = new Thread(this);
        thread.start();
    }

    public void close() {
        try {
            theOut.close();
            theIn.close();
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(AcuariumScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeMessage(String message) {
        theOut.println(message);
    }

    private void sendScreenList() {
        writeMessage("{\"status\":200,\"detail\":{\"data\": {\"Cantidad de pantallas\": "
                + "\"" + this.server.getScreenClients().size() + "\", \"Resolución\": \"" + AcuariumConstants.resolution + "\"}" + "}}");
    }

    private void screenChangeSuccessfull() {
        writeMessage("{\"status\":200,\"detail\":{\"data\":\" Pantallas cambiadas con éxito ;) \"}}");
    }

    private void changeScreens(int pos1, int pos2) {
        LinkedList<AcuariumScreen> screens = this.server.getScreenClients();
        pos1--;
        pos2--;
        if ((pos1 >= 0 && pos2 >= 0)
                && (pos1 < screens.size() && pos2 < screens.size())) {

            server.clearClientFishList();

            System.out.println(pos1 + " p2 " + pos2 + "tam " + this.server.getScreenClients().size());

            this.server.getScreenClients().get(pos1).setId(pos2 + 1);
            this.server.getScreenClients().get(pos2).setId(pos1 + 1);

            AcuariumScreen acTemp = this.server.getScreenClients().get(pos2);

            this.server.getScreenClients().set(pos2, screens.get(pos1));
            this.server.getScreenClients().set(pos1, acTemp);

            updateScreen(this.server.getScreenClients().get(pos1));
            updateScreen(this.server.getScreenClients().get(pos2));

            screenChangeSuccessfull();

        } else {
            unknownScreens();
        }
    }

    private void updateScreen(AcuariumScreen screen) {
        JSONArray data = new JSONArray(screen.getFishes());
        screen.writeMessage("{\"status\":200,\"detail\":{\"data\":" + data.toString() + "}" + "}");
    }

    private void sendFishList() {
        JSONArray data = new JSONArray(this.server.getFishes());
        writeMessage("{\"status\":200,\"detail\":{\"data\":" + data.toString() + "}" + "}");
    }

    private synchronized void deleteFish(int id) {
        boolean resp = this.server.deleteFish(id);
        if (resp) {
            writeMessage("{\"status\":200,\"detail\":{\"data\":\"Pez borrado con éxito \"}}");
        } else {
            writeMessage("{\"status\":200,\"detail\":{\"data\":\"Pez inexistente \"}}");
        }
    }

    private synchronized void sendFishToScreen(int type, int screen) {
        int realScreen = screen - 1;
        int position = realScreen * AcuariumConstants.resolution;
        if (this.server.addFish(type, position)) {
            writeMessage("{\"status\":200,\"detail\":{\"data\": \"Pez agregado con éxito en la pantalla " + screen + "\"}" + "}");
        } else {
            writeMessage("{\"status\":200,\"detail\":{\"data\":\" Revise los parámetros, tipo o pantalla inexistente " + screen + "\"}" + "}");
        }
    }

    private void sendFishTypesList() {
        String base = "{\"status\":200,\"detail\":{\"data\":";
        if (server.getFishtypes().size() > 0) {
            base += "[";
            for (int i = 0; i < server.getFishtypes().size(); i++) {
                switch (i) {
                    case 1:
                        base += "{ \"Tipo\":1 , \"Descripción\": \"Pez que nada en lo alto\" }";
                        break;
                    case 2:
                        base += "{ \"Tipo\":2 , \"Descripción\": \"Pez que nada en zona media\" }";
                        break;
                    case 3:
                        base += "{ \"Tipo\":3 , \"Descripción\": \"Pez que nada en zona baja\" }";
                        break;
                    default:
                        base += "{ \"Tipo\":" + i + ", \"Descripción\": \"Pez que nada en lo alto\" }";
                        break;
                }
                if (i < server.getFishtypes().size() - 1) {
                    base += ",";
                }
            }

            base += "]}}";
        } else {
            base += "\"No se dispone de ningún tipo de pez.\"}}";
        }

        writeMessage(base);

    }

    void deleteScreen(int screen) {
        System.out.println("La pantallaaa esss:" + screen);
        if (server.deleteScreen(screen)) {
            writeMessage("{\"status\":200,\"detail\":{\"data\": \"Pantalla eliminada con éxito \"}}");
        } else {
            unknownScreens();
        }
    }

    void commandError() {
        theOut.println(ResponseConstants.NOT_FOUND_404);
    }

    void unknownScreens() {
        theOut.println("{\"status\":200,\"detail\":{\"data\": \" La(s) pantallas ingresadas son inexistentes \"}}");
    }

    void changeScreens(String dat) {
        String[] screensData = dat.substring(11).split(" ");
        if (screensData.length == 2) {
            try {
                changeScreens(Integer.parseInt(screensData[0]), Integer.parseInt(screensData[1]));
            } catch (NumberFormatException e) {
                commandError();
            }

        } else {
            commandError();
        }
    }

    synchronized void deleteFishes(String dat) {
        String fishData = dat.substring(12).trim();
        if (!fishData.equals("")) {
            try {
                int fishId = Integer.parseInt(fishData);
                deleteFish(fishId);
            } catch (NumberFormatException e) {
                commandError();
            }

        } else {
            commandError();
        }

    }

    synchronized void addFish(String dat) {
        String[] screensData = dat.substring(13).split(" ");
        if (screensData.length == 2) {
            try {

                int type = Integer.parseInt(screensData[0]);
                int screen = Integer.parseInt(screensData[1]);
                sendFishToScreen(type, screen);

            } catch (NumberFormatException e) {
                commandError();
            }

        }
    }

    synchronized void deleteScreen(String dat) {
        //Eliminar pantalla CMD DEL SCREEN 1
        String screenData = dat.substring(15).trim();
        if (!screenData.equals("")) {
            try {
                int screen = Integer.parseInt(screenData);
                screen--;

                System.out.println("Screeeen:" + screen);

                //deleteScreen(screen);
                if (screen >= 0 && screen < server.getScreenClients().size()) {
                    server.getScreenClients().get(screen).writeMessage("{\"status\":300,\"detail\":{\"data\": \" Cerrar cliente \"}}");
//                    try {
                    //Thread.sleep(300);
                    System.out.println("pantalla numero: " + screen);
                    server.getScreenClients().get(screen).close();
                    server.getScreenClients().remove(screen);
//                    } catch (InterruptedException ex) {
//                        Logger.getLogger(ConfigurationSERequest.class.getName()).log(Level.SEVERE, null, ex);
//                    }

                }

            } catch (NumberFormatException e) {
                commandError();
            }
        } else {
            commandError();
        }
    }

    @Override
    public void run() {

        while (!readLine.trim().equalsIgnoreCase("QUIT")) {

            try {

                theOut = new PrintWriter(socket.getOutputStream(), true);
                theIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

                readLine = theIn.readLine();

                if (readLine.startsWith("CMD SCREEN-LIST")) {
                    sendScreenList();

                } else if (readLine.startsWith("CMD DEL SCREEN")) {
                    System.out.println(readLine);
                    deleteScreen(readLine);

                } else if (readLine.startsWith("CMD CHANGE")) { //CMD CHANGE P1 P2
                    changeScreens(readLine);

                } else if (readLine.startsWith("CMD FISH-LIST")) {
                    sendFishList();

                } else if (readLine.startsWith("CMD DEL FISH")) {//CMD DEL FISH ID
                    deleteFishes(readLine);

                } else if (readLine.startsWith("CMD ADD FISH")) {//CMD ADD FISH TYPE SCREEN
                    addFish(readLine);

                } else if (readLine.startsWith("CMD FISHTYPES-LIST")) {
                    sendFishTypesList();

                } else if (!readLine.equals("QUIT")) {
                    writeMessage("Error: Invalid command");
                }

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        close();

    }

}
