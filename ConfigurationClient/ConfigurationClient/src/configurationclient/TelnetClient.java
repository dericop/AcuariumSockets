/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package configurationclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Camilo
 */
public class TelnetClient implements Runnable {

    private JTextArea textArea = null;
    private JTextArea textArea1 = null;
    private boolean isConnected;
    private int port;
    private String hostname;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Thread thread;
    private String passwordTelnet;
    private String passwordClients;
    private final JTextField status;
    private final JTable table;

    public TelnetClient(String hostname, int port, JTextArea textArea, JTextArea textArea1, String passwordTelnet, String passwordClients, JTextField status, JTable table) {
        this.port = port;
        this.hostname = hostname;
        this.textArea = textArea;
        this.textArea1 = textArea1;
        this.isConnected = false;
        this.status = status;
        this.table = table;
        this.open();
        this.thread = new Thread(this);
        this.thread.start();
    }

    public void open() {
        try {
            this.setSocket(new Socket(this.getHostname(), this.getPort()));
            this.setOut(new PrintWriter(this.getSocket().getOutputStream(), true));
            this.setIn(new BufferedReader(new InputStreamReader(getSocket().getInputStream())));
        } catch (IOException ex) {
            Logger.getLogger(TelnetClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void close() {
        try {
            this.getSocket().close();
            this.getOut().close();
            this.getIn().close();
        } catch (IOException ex) {
            Logger.getLogger(TelnetClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeMessage(String message) {
        this.getOut().println(message);
    }

    public void receiveMessage(String message) {
        try {
            System.out.println(message);
            JSONObject jsonObj = new JSONObject(message);
            String status = jsonObj.get("status").toString();
            if (status.equals("100")) {
                this.setIsConnected(true);
                DefaultTableModel model = (DefaultTableModel) this.table.getModel();
                model.addRow(new Object[]{"1", "CMD SCREEN-LIST", "Muestra el listado de pantallas"});
                model.addRow(new Object[]{"2", "CMD CHANGE <SCREEN1> <SCREEN2>", "Intercambia un par de pantallas"});
                model.addRow(new Object[]{"3", "CMD DEL SCREEN <ID>", "Intercambia un par de pantallas"});
                model.addRow(new Object[]{"4", "CMD FISH-LIST", "Muestra el listado de peces"});
                model.addRow(new Object[]{"5", "CMD DEL FISH <ID>", " Elimina un pex con identificador ID"});
                model.addRow(new Object[]{"6", "CMD ADD FISH <TYPE> <SCREEN>", "Agrega un pez a una pantalla"});
                model.addRow(new Object[]{"7", "CMD FISHTYPES-LIST", "Lista los tipos de peces"});
            }
            this.status.setText(status);
            this.textArea.append(message+"\n");
        } catch (JSONException ex) {
            Logger.getLogger(TelnetClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        String readLine = "";
        boolean quit = false;
        while (!quit) {
            try {
                readLine = this.getIn().readLine();
//                writeMessage(readLine);
                this.receiveMessage(readLine);
            } catch (IOException ex) {
                Logger.getLogger(TelnetClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        close();
    }

    /**
     * @return the textArea
     */
    public JTextArea getTextArea() {
        return textArea;
    }

    /**
     * @param aTextArea the textArea to set
     */
    public void setTextArea(JTextArea aTextArea) {
        textArea = aTextArea;
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
     * @return the hostname
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * @param hostname the hostname to set
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
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

    /**
     * @return the out
     */
    public PrintWriter getOut() {
        return out;
    }

    /**
     * @param out the out to set
     */
    public void setOut(PrintWriter out) {
        this.out = out;
    }

    /**
     * @return the in
     */
    public BufferedReader getIn() {
        return in;
    }

    /**
     * @param in the in to set
     */
    public void setIn(BufferedReader in) {
        this.in = in;
    }

    /**
     * @return the thread
     */
    public Thread getThread() {
        return thread;
    }

    /**
     * @param thread the thread to set
     */
    public void setThread(Thread thread) {
        this.thread = thread;
    }

    /**
     * @return the isConnected
     */
    public boolean isIsConnected() {
        return isConnected;
    }

    /**
     * @param isConnected the isConnected to set
     */
    public void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

}
