package spaceinvaders;
import java.awt.*;
import java.io.Serializable;
/**
 * Klasa reprezentująca pocisk wystrzeliwany przez gracza
 */
public class Bullet implements Serializable{
    private int x, y;
    private final int WIDTH = 5;
    private final int HEIGHT = 10;
    private final int SPEED = 10;
    private boolean active = true;
    /**
     * Konstruktor klasy Bullet
     */
    public Bullet(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }
    /**
     * Przesuwa pocisk do góry
     */
    public void move() {
        y -= SPEED;
        if (y < 0) {
            active = false;
        }
    }
    /**
     * Rysuje pocisk na ekranie
     */
    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, WIDTH, HEIGHT);
    }
    /**
     * Sprawdza, czy pocisk jest aktywny
     */
    public boolean isActive() {
        return active;
    }
    /**
     * Ustawia stan aktywności pocisku
     */
    public void setActive(boolean active){
        this.active = active;
    }
    /**
     * Zwraca prostokąt określający granice pocisku
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }
}