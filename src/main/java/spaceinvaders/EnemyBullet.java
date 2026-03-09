package spaceinvaders;
import java.awt.*;
import java.io.Serializable;
/**
 * Klasa reprezentująca pocisk wystrzelony przez przeciwnika
 */
public class EnemyBullet implements Serializable{
    private int x, y;
    private final int WIDTH = 5;
    private final int HEIGHT = 10;
    private final int SPEED = 5;
    private boolean active = true;
    /**
     * Konstruktor klasy EnemyBullet
     */
    public EnemyBullet(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }
    /**
     * Przesuwa pocisk w dół o ustaloną prędkość
     */
    public void move() {
        y += SPEED;
        if (y > 600) {
            active = false;
        }
    }
    /**
     * Rysuje pocisk na ekranie
     */
    public void draw(Graphics g) {
        g.setColor(Color.RED);
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
    public void setActive(boolean active) {
        this.active = active;
    }
    /**
     * Zwraca prostokątne granice pocisku wykorzystywane do detekcji kolizji
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }
}