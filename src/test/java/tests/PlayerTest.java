package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spaceinvaders.Player;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerTest {
    private Player player;
    /**
     * Inicjalizacja gracza
     */
    @BeforeEach
    public void setUp() {
        player = new Player(100, 500);
    }
    /**
     * Testuje, czy gracz jest poprawnie inicjalizowany.
     */
    @Test
    public void testInitialization() {
        assertEquals(100, player.getX(), "Initial X position should be 100");
        assertEquals(500, player.getY(), "Initial Y position should be 500");
        assertEquals(50, player.getWidth(), "Player width should be 50");
    }
    /**
     * Testuje ruch gracza w lewo.
     */
    @Test
    public void testMoveLeft() {
        player.moveLeft();
        assertEquals(90, player.getX(), "Player should move 10 pixels to the left");
        player.moveLeft();
        player.moveLeft();
        player.moveLeft();
        player.moveLeft();
        player.moveLeft();
        player.moveLeft();
        player.moveLeft();
        player.moveLeft();
        player.moveLeft();
        player.moveLeft();
        assertEquals(0, player.getX(), "Player should not move beyond the left edge of the screen");
    }

    /**
     * Testuje ruch gracza w prawo
     */
    @Test
    public void testMoveRight() {
        player.moveRight();
        assertEquals(110, player.getX(), "Player should move 10 pixels to the right");
        for (int i = 0; i < 70; i++) {
            player.moveRight();
        }
        assertEquals(750, player.getX(), "Player should not move beyond the right edge of the screen");
    }

    /**
     * Testuje czy granice gracza są poprawnie obliczane
     */
    @Test
    public void testGetBounds() {
        Rectangle bounds = player.getBounds();
        assertEquals(100, bounds.x, "Bounds X should match player X position");
        assertEquals(500, bounds.y, "Bounds Y should match player Y position");
        assertEquals(50, bounds.width, "Bounds width should match player width");
        assertEquals(30, bounds.height, "Bounds height should match player height");
    }

    /**
     * Testuje, czy współrzędne i wymiary gracza są poprawnie zwracane.
     */
    @Test
    public void testGetters() {
        assertEquals(100, player.getX(), "getX should return the correct X position");
        assertEquals(500, player.getY(), "getY should return the correct Y position");
        assertEquals(50, player.getWidth(), "getWidth should return the correct width");
    }
}