/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visualizationclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Camilo
 */
public final class VisualizationClient implements Runnable {

    private int port;
    private final String hostname;
    private String password;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final Thread thread;
    private int id;
    private JSONArray fishes;

    public VisualizationClient(String hostname, int port, String password) {
        this.hostname = hostname;
        this.port = port;
        this.password = password;
        this.open();
        this.thread = new Thread((Runnable) this);
        this.thread.start();
        this.id = 0;
        this.fishes = new JSONArray();
    }

    public void open() {
        try {
            System.out.println("---------------------");
            this.setSocket(new Socket(hostname, getPort()));
            System.out.println(socket);
            this.out = new PrintWriter(this.getSocket().getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(this.getSocket().getInputStream()));
            writeMessage("CL START " + this.password);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void close() {
        try {
            if (this.getSocket() != null && this.getSocket().isConnected()) {
                this.getSocket().close();
                this.out.close();
                this.in.close();
            }
            this.thread.interrupt();
        } catch (Exception e) {
        }
    }

    public void writeMessage(String message) {
        this.out.println(message);
    }

    @Override
    public void run() {
        String readline = "";
        boolean quit = false;
        while (!quit) {
            try {
                if (this.getSocket() != null && this.getSocket().isConnected()) {
                    readline = this.in.readLine();
                    System.out.println(readline);
                    if (readline != null) {
                        JSONObject jsonObj;
                        try {
                            jsonObj = new JSONObject(readline);
                            String status = jsonObj.get("status").toString();
                            switch (status) {
                                case "100":
                                    setId(Integer.parseInt(((JSONObject) (jsonObj.get("detail"))).get("data").toString()));
                                    VisualizationFrame.clientFrame.setVisible(true);
                                    break;
                                case "200":
                                    this.setFishes((JSONArray) (((JSONObject) jsonObj.get("detail")).get("data")));
                                    break;
                                case "300":
                                    System.out.println("aca esta tu 300");
                                    writeMessage("CL EXIT");
                                    this.close();
                                    VisualizationFrame.close();
                                    Thread.interrupted();
                                    break;
                                case "403":
                                    close();
                                    VisualizationFrame.close();
                                    Thread.interrupted();
                                    break;
                            }
                        } catch (JSONException ex) {
                            Logger.getLogger(VisualizationClient.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                } else {
                    quit = true;
                }
            } catch (IOException e) {
            }
        }
        System.out.println("kasjdhaksdhak");
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the fishes
     */
    public JSONArray getFishes() {
        return fishes;
    }

    /**
     * @param fishes the fishes to set
     */
    public void setFishes(JSONArray fishes) {
        this.fishes = fishes;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * @param socket the socket to set
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

}
