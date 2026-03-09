package spaceinvaders;
import java.awt.*;
import java.io.Serializable;
/**
 * Klasa reprezentująca gracza
 */
public class Player implements Serializable{
    private int x;
    private int y;
    private int width;
    private int height;
    public static int SPEED = 10;

    /**
     * Konstruktor klasy Player
     */
    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 50;
        this.height = 30;
    }
    /**
     * Rysuje gracza
     */
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int[] xPoints = {
                x,
                x + width / 2,
                x + width,
                x + width - 10,
                x + width / 2,
                x + 10
        };
        int[] yPoints = {
                y + height,
                y,
                y + height,
                y + height - 10,
                y + 10,
                y + height - 10
        };
        Polygon spaceship = new Polygon(xPoints, yPoints, xPoints.length);
        g2d.setColor(Color.GREEN);
        g2d.fillPolygon(spaceship);
        g2d.setColor(Color.BLACK);
        g2d.fillOval(x + width / 2 - 5, y + height / 2 - 5, 10, 10);
    }
    /**
     * Przesuwa gracza w lewo
     */
    public void moveLeft() {
        x -= SPEED;
        if (x < 0) {
            x = 0;
        }
    }
    /**
     * Przesuwa gracza w prawo
     */
    public void moveRight() {
        x += SPEED;
        if (x + width > 800) {
            x = 800 - width;
        }
    }

    /**
     * Zwraca granice gracza
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    /**
     * Zwraca współrzędną X gracza
     */
    public int getX() { return x; }
    /**
     * Zwraca współrzędną Y gracza
     */
    public int getY() { return y; }
    /**
     * Zwraca szerokość gracza
     */
    public int getWidth() { return width; }
}
