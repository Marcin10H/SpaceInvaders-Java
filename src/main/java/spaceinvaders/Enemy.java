package spaceinvaders;
import java.awt.*;
import java.io.Serializable;
/**
 * Klasa reprezentująca przeciwnika
 */
public class Enemy implements Serializable{
    private int x;
    private int y;
    private int width;
    private int height;
    /**
     * Konstruktor klasy Enemy
     */
    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 30;
        this.height = 20;
    }
    /**
     * Rysuje przeciwnika na ekranie w formie wielokąta przypominającego kosmitę
     */
    public void draw(Graphics g) {
        g.setColor(Color.ORANGE);
        int[] xPoints = {
                x,
                x + width / 4,
                x + width / 2,
                x + 3 * width / 4,
                x + width,
                x + 3 * width / 4,
                x + width / 2,
                x + width / 4
        };
        int[] yPoints = {
                y + height / 2,
                y,
                y,
                y,
                y + height / 2,
                y + height,
                y + height,
                y + height
        };

        Polygon alienShape = new Polygon(xPoints, yPoints, xPoints.length);
        g.fillPolygon(alienShape);
        g.setColor(Color.BLACK);
        int eyeWidth = 3;
        int eyeHeight = 3;
        g.fillOval(x + width / 3 - eyeWidth / 2, y + height / 3 - eyeHeight / 2, eyeWidth, eyeHeight);
        g.fillOval(x + 2 * width / 3 - eyeWidth / 2, y + height / 3 - eyeHeight / 2, eyeWidth, eyeHeight);
    }
    /**
     * Zwraca współrzędną X przeciwnika
     */
    public int getX() {
        return x;
    }
    /**
     * Zwraca współrzędną Y przeciwnika
     */
    public int getY() {
        return y;
    }
    /**
     * Zwraca szerokość przeciwnika
     */
    public int getWidth() {
        return width;
    }
    /**
     * Zwraca wysokość przeciwnika
     */
    public int getHeight() {
        return height;
    }
    /**
     * Ustawia nową wartość X dla przeciwnika
     */
    public void setX(int x) {
        this.x = x;
    }
    /**
     * Ustawia nową wartość Y dla przeciwnika
     */
    public void setY(int y) {
        this.y = y;
    }
}
