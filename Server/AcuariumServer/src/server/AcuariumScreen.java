package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Fish;

/**
 *
 * @author dericop Clase que instancia el servidor cuando se asocia un nuevo
 * cliente
 */
public final class AcuariumScreen implements Runnable {

    private int id = 0;

    private LinkedList<Fish> fishes;
    private AcuariumServer server = null;
    private Thread thread = null;
    private Socket socket = null;
    private PrintWriter theOut = null;
    private BufferedReader theIn = null;
    private String readLine = "";

    private String[] keyAndPort;

    public AcuariumScreen(int id, Socket socket, AcuariumServer server) {
        this.id = id;
        this.socket = socket;
        this.server = server;
        fishes = new LinkedList<>();
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

    void commandError() {
        theOut.println(ResponseConstants.NOT_FOUND_404);
    }

    void changeFishDirection(String dat) {
        String data = dat.substring(7);
        if (!data.trim().equals("")) {
            try {
                int fishId = Integer.parseInt(data.trim());
                if (server.changeFishDirection(fishId)) {
                    writeMessage("{\"status\":201,\"detail\":{\"data\":\"Dirección de pez cambiada exitosamente\"}" + "}");
                } else {
                    writeMessage("{\"status\":201,\"detail\":{\"data\":\"Pez inexistente\"}" + "}");
                }
            } catch (NumberFormatException e) {
            }
        } else {
            commandError();
        }
    }

    void scareFishes(String dat) {
        String data = dat.substring(9);
        String[] positions = data.split(" ");
        if (positions.length == 2) {

            try {
                int x = Integer.parseInt(positions[0]);
                int y = Integer.parseInt(positions[1]);

                System.out.println("X: " + x + " Y: " + y);

                System.out.println("ID: " + this.id);
                server.scareFishesInScreen(x, y, this.id);

            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }

        } else {
            commandError();
        }
    }

    @Override
    public void run() {
        boolean quit = false;

        while (!quit) {

            try {
                if (this.socket!=null && !this.socket.isClosed() && theIn != null && (readLine = theIn.readLine()) != null) {
                    if (this.socket.isConnected() && theIn.ready()) {
                        System.out.println(readLine);

                        if (readLine != null) {
                            if (!readLine.trim().equals(" ")) {
                                if (readLine.equals("CL EXIT")) {
                                    System.out.println(readLine);
                                    server.deleteScreen(this.id);
                                    quit = true;

                                } else if (readLine.startsWith("CL CHD")) {
                                    changeFishDirection(readLine);

                                } else if (readLine.startsWith("CL SCARE")) {
                                    System.out.println("daniel es un espanto");
                                    scareFishes(readLine);
                                }
                            }
                        }
                    } else {

                    }

                } else {
                    //quit = true;
                    //server.deleteScreen(this.id);
                    quit = true;
                }

            } catch (IOException ex) {
                Logger.getLogger(AcuariumScreen.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        close();

    }

    public void setId(int id) {
        this.id = id;
    }

    public LinkedList<Fish> getFishes() {
        return fishes;
    }

    public void setFishes(LinkedList<Fish> fishes) {
        this.fishes = fishes;
    }

    @Override
    public String toString() {
        return this.id + "";
    }

}
