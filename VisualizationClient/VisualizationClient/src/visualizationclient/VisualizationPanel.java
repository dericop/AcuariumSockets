/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visualizationclient;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Camilo
 */
public class VisualizationPanel extends javax.swing.JPanel {

    private VisualizationClient client;

    public VisualizationPanel() {
        initComponents();
        String hostname = JOptionPane.showInputDialog(null, "Ingresa el hostname");
        while ("".equals(hostname) || !VisualizationPanel.validate(hostname)) {
            hostname = JOptionPane.showInputDialog(null, "Ingresa una ip valida");
        }
        String port = JOptionPane.showInputDialog(null, "port");
        while ("".equals(port) || !isInteger(port)) {
            port = JOptionPane.showInputDialog(null, "Ingresa un pueto valido");
        }
        String password = JOptionPane.showInputDialog(null, "password");
        this.client = new VisualizationClient(hostname, Integer.parseInt(port), password);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                setPositionOrScareClickedFishes(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }
        });
    }

    public static boolean isInteger(String s) {
        return isInteger(s, 10);
    }

    public static boolean isInteger(String s, int radix) {
        if (s.isEmpty()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) {
                    return false;
                } else {
                    continue;
                }
            }
            if (Character.digit(s.charAt(i), radix) < 0) {
                return false;
            }
        }
        return true;
    }
    private static final Pattern PATTERN = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public static boolean validate(final String ip) {
        return PATTERN.matcher(ip).matches();
    }

    private void setPositionOrScareClickedFishes(int mouseX, int mouseY) {
        for (int i = 0; i < this.getClient().getFishes().length(); i++) {
            try {
                JSONObject fish = this.getClient().getFishes().getJSONObject(i);
                int x = Integer.parseInt(fish.get("x").toString());
                int y = Integer.parseInt(fish.get("y").toString());
                int midWidth = (int) (Integer.parseInt(fish.get("width").toString())) / 2;
                int midHeight = (int) (Integer.parseInt(fish.get("height").toString())) / 2;
                if (mouseX <= x + midHeight && mouseX >= x - midHeight && mouseY <= y + (midHeight * 2) && mouseY >= y) {
                    int id = Integer.parseInt(fish.get("id").toString());
                    getClient().writeMessage("CL CHD " + id);
                } else {
                    getClient().writeMessage("CL SCARE " + mouseX + " " + mouseY);
                }
            } catch (JSONException ex) {
                Logger.getLogger(VisualizationPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void drawFishes(Graphics grphcs) {
        System.out.println(this.getClient().getFishes());
        for (int i = 0; i < this.getClient().getFishes().length(); i++) {
            try {
                if (this.getClient() != null && this.getClient().getFishes() != null && this.getClient().getFishes().getJSONObject(i) != null) {
                    System.out.println(this.getClient().getFishes());

                    if (this.getClient().getFishes().getJSONObject(i) != null) {
                        JSONObject fish = this.getClient().getFishes().getJSONObject(i);
                        System.out.println(fish.toString());
                        int type = Integer.parseInt(fish.get("type").toString());
                        int x = Integer.parseInt(fish.get("x").toString());
                        int y = Integer.parseInt(fish.get("y").toString());
                        int direction = Integer.parseInt(fish.get("direction").toString());
                        int width = Integer.parseInt(fish.get("width").toString());
                        ImageIcon fishImage = new ImageIcon(getClass().getResource("../static/image/fish" + type + ".gif"));
                        if (direction == 1) {
                            grphcs.drawImage(fishImage.getImage(), x - (width / 2), y, fishImage.getImage().getWidth(this), fishImage.getImage().getHeight(this), this);

                        } else {
                            grphcs.drawImage(fishImage.getImage(), x + (width / 2), y, -fishImage.getImage().getWidth(this), fishImage.getImage().getHeight(this), this);
                        }
                    }

                }
            } catch (JSONException ex) {
                Logger.getLogger(VisualizationPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void closeSession() {
        try {
            //getClient().writeMessage("CL EXIT");
            Thread.sleep(300);
            getClient().close();
        } catch (InterruptedException ex) {
            Logger.getLogger(VisualizationPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void paint(Graphics grphcs) {
        super.paint(grphcs); //To change body of generated methods, choose Tools | Templates.
        Dimension height = getSize();
        ImageIcon image;
        if (this.getClient().getId() % 2 != 0) {
            image = new ImageIcon(getClass().getResource("../static/image/background.jpg"));
        } else {
            image = new ImageIcon(getClass().getResource("../static/image/background1.jpg"));
        }
        grphcs.drawImage(image.getImage(), 0, 0, height.width, height.height, this);
        drawFishes(grphcs);
        repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @return the client
     */
    public VisualizationClient getClient() {
        return client;
    }

    /**
     * @param client the client to set
     */
    public void setClient(VisualizationClient client) {
        this.client = client;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
