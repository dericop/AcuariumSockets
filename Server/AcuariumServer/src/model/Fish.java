package model;

/**
 *
 * @author dericop Modelo de un pez
 */
public class Fish implements Cloneable {

    /**
     * Identificar del pez en el servidor
     */
    private int id;
    /**
     * Tipo de pez, enumeración TOP 1, MEDIUM 2, BOTTOM 3
     */

    private int type;
    /**
     * Posición en x & y del pez, se enviará al cliente de visualización
     */

    private int x;

    private int y;

    private int direction;

    /**
     * Distancia que puede recorrer un pez al ser asustado
     */
    private float distance;
    /**
     * Velocidad del pez
     */
    private int velocity;
    /**
     * Dimensión en ancho
     */
    private float width;
    /**
     * Dimensión en alto
     */
    private float height;
    /**
     * Velocidad con la que nada un pez asustado
     */
    private int scareVelocity;

    private int scareTime;

    /**
     *
     * @param type El tipo de pez que se quiere crear. Enumeración FishType
     */
    public Fish(int type) {
        this.id = FishConstants.currentid; //Asignar el siguiente id disponible
        FishConstants.currentid++;
        this.type = type;
        this.direction = 1;
        this.scareTime = -1;
        configure();
    }

    public void changeDirection() {

        if (this.direction == 1) {
            this.direction = -1;
        } else {
            this.direction = 1;
        }
    }

    /**
     * Configura el objeto dependiendo del tipo de pez creado
     */
    private void configure() {
        switch (type) {
            case 1://TOP
                setTopDefaults();
                break;
            case 2://MEDIUM
                setMedDefaults();
                break;
            case 3://BOTTOM
                setBottomDefaults();
                break;
        }
    }

    /**
     * Configuración para el pez tipo TOP
     */
    private void setTopDefaults() {
        this.x = 0;
        this.y = FishConstants.TOP_DEFAULT_Y;
        this.distance = FishConstants.TOP_DEFAULT_DISTANCE;
        this.velocity = FishConstants.TOP_DEFAULT_VELOCITY;
        this.scareVelocity = FishConstants.TOP_DEFAULT_SCARE_VELOCITY;
        this.width = FishConstants.TOP_DEFAULT_W;
        this.height = FishConstants.TOP_DEFAULT_H;
    }

    /**
     * Configuración para el pez tipo MEDIUM
     */
    private void setMedDefaults() {
        this.x = 0;
        this.y = FishConstants.MED_DEFAULT_Y;
        this.distance = FishConstants.MED_DEFAULT_DISTANCE;
        this.velocity = FishConstants.MED_DEFAULT_VELOCITY;
        this.scareVelocity = FishConstants.MED_DEFAULT_SCARE_VELOCITY;
        this.width = FishConstants.MED_DEFAULT_W;
        this.height = FishConstants.MED_DEFAULT_H;
    }

    /**
     * Configuración para el pez tipo BOTTOM
     */
    private void setBottomDefaults() {
        this.x = 0;
        this.y = FishConstants.BOTTOM_DEFAULT_Y;
        this.distance = FishConstants.BOTTOM_DEFAULT_DISTANCE;
        this.velocity = FishConstants.BOTTOM_DEFAULT_VELOCITY;
        this.scareVelocity = FishConstants.BOTTOM_DEFAULT_SCARE_VELOCITY;
        this.width = FishConstants.BOTTOM_DEFAULT_W;
        this.height = FishConstants.BOTTOM_DEFAULT_H;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    //-------------------- GETTERS & SETTERS ------------------------------------
    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int getVelocity() {
        if (this.scareTime != -1) {
            this.scareTime--;
            return this.scareVelocity;
        }
        
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getScareVelocity() {
        return scareVelocity;
    }

    public void setScareVelocity(int scareVelocity) {
        this.scareVelocity = scareVelocity;
    }

    public void setScareTime(int time) {
        this.scareTime = time;
    }

    public int getScareTime() {
        return this.scareTime;
    }

}
