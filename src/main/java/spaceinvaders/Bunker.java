package spaceinvaders;
import java.awt.*;
import java.io.Serializable;
/**
 * Klasa reprezentująca bunkier
 */
public class Bunker implements Serializable{
    private int x;
    private int y;
    private int width;
    private int height;
    private int health;
    /**
     * Konstruktor klasy Bunker
     */
    public Bunker(int x, int y) {
        this.x = x+40;
        this.y = y;
        this.width = 60;
        this.height = 40;
        this.health = 15;
    }
    /**
     * Rysuje bunkier na ekranie oraz wyświetla jego wytrzymałość
     */
    public void draw(Graphics g) {
        g.setColor(new Color(192, 192, 192));
        int[] xPoints = { x, x + width / 2, x + width, x + (3 * width) / 4, x + width / 4 };
        int[] yPoints = { y + height / 2, y, y + height / 2, y + height, y + height };
        Polygon shield = new Polygon(xPoints, yPoints, xPoints.length);
        g.fillPolygon(shield);
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.BOLD, 19));
        g.drawString(String.valueOf(health), x + width / 3, y + 30);
    }
    /**
     * Zwraca prostokąt określający granice bunkra
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    /**
     * Obniża wytrzymałość bunkra o 1 przy każdym trafieniu
     */
    public boolean takeDamage() {
        health--;
        return health <= 0;
    }
}
