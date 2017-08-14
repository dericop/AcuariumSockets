/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visualizationclient;

import java.awt.Point;

/**
 *
 * @author Camilo
 */
public class Fish {
    private Point position;
    private int id;
    private float distance;
    private float velocity;

    /**
     * @return the position
     */
    public Point getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(Point position) {
        this.position = position;
    }
    private enum area {TOP,MID,BOT};
    private enum type {GRANDE,MEDIANO,PEQUENO};

    public Fish(Point position, int id, float distance, float velocity) {
        this.position = position;
        this.id = id;
        this.distance = distance;
        this.velocity = velocity;
    }

}
