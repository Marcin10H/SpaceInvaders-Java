package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import spaceinvaders.Game;
import spaceinvaders.Player;
import spaceinvaders.Enemy;
import spaceinvaders.Bullet;
import spaceinvaders.EnemyBullet;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;

public class GameTest {

    private Game game;
    /**
     * Ustawia środowisko testowe przed każdym testem
     * Tworzy nową instancję gry która będzie używana w testach
     */
    @BeforeEach
    public void setUp() {
        game = new Game();
    }
    /**
     * Testuje inicjalizację gry
     */
    @Test
    public void testInitialization() {
        assertNotNull(game, "Game should not be null");
        assertEquals(3, game.getLives(), "Initial lives should be 3");
        assertEquals(0, game.getScore(), "Initial score should be 0");
    }
    /**
     * Testuje ruch wrogów
     */
    @Test
    public void testMoveEnemies() {
        List<Enemy> enemiesBeforeMove = game.getEnemies();
        int initialX = enemiesBeforeMove.get(0).getX();
        game.moveEnemies();
        assertNotEquals(initialX, enemiesBeforeMove.get(0).getX(), "Enemies should move horizontally");
    }
    /**
     * Testuje ruch gracza w lewo i w prawo
     */
    @Test
    void testPlayerMovement() {
        int initialX = game.getPlayer().getX();
        game.getPlayer().moveLeft();
        assertEquals(initialX - Player.SPEED, game.getPlayer().getX());

        game.getPlayer().moveRight();
        assertEquals(initialX, game.getPlayer().getX());
    }
    /**
     * Testuje kolizje pocisków gracza z przeciwnikami
     */
    @Test
    public void testCheckCollisions() {
        Enemy enemy = game.getEnemies().get(0);
        Bullet bullet = new Bullet(enemy.getX(), enemy.getY());
        game.getBullets().add(bullet);
        int initialEnemyCount = game.getEnemies().size();
        game.checkCollisions();
        assertEquals(initialEnemyCount - 1, game.getEnemies().size(), "Enemy should be removed when hit by a bullet");
        assertEquals(10, game.getScore(), "Score should increase when enemy is hit");
    }
    /**
     * Testuje strzelanie gracza
     */
    @Test
    public void testShoot() {
        int initialBulletCount = game.getBullets().size();
        game.shoot();
        assertEquals(initialBulletCount + 1, game.getBullets().size(), "Bullet should be added to the list after shooting");
    }

    /**
     * Testuje czy gra kończy się kiedy graczowi skończą się życia
     */
    @Test
    public void testGameOverWhenLivesDepleted() {
        int initialLives = game.getLives();
        for (int i = 0; i < initialLives; i++) {
            EnemyBullet bullet = new EnemyBullet(game.getPlayer().getX(), game.getPlayer().getY());
            game.getEnemyBullets().add(bullet);
            game.checkPlayerHit();
        }
        assertTrue(game.isGameOver(), "Game should be over when player has no lives left");
    }

    /**
     * Testuje, czy pocisk trafia w wroga
     */
    @Test
    public void testBulletHitsEnemy() {
        Enemy enemy = game.getEnemies().get(0);
        Bullet bullet = new Bullet(enemy.getX(), enemy.getY());
        game.getBullets().add(bullet);
        int initialEnemyCount = game.getEnemies().size();
        game.checkCollisions();
        assertEquals(initialEnemyCount - 1, game.getEnemies().size(), "Enemy should be removed when hit by a bullet");
        assertEquals(10, game.getScore(), "Score should increase when enemy is hit");
    }

    /**
     * Testuje, czy gracz traci życie po trafieniu przez pocisk wroga
     */
    @Test
    public void testPlayerHitByEnemyBullet() {
        EnemyBullet bullet = new EnemyBullet(game.getPlayer().getX(), game.getPlayer().getY());
        game.getEnemyBullets().add(bullet);
        int initialLives = game.getLives();
        game.checkPlayerHit();
        assertEquals(initialLives - 1, game.getLives(), "Player should lose a life when hit by an enemy bullet");
    }
    /**
     * Testuje resetowanie gry
     */
    @Test
    public void testGameReset() {
        game.shoot();
        game.moveEnemies();
        game.getPlayer();
        game.resetGame();
        assertEquals(3, game.getLives(), "Lives should reset to 3");
        assertEquals(0, game.getScore(), "Score should reset to 0");
        assertFalse(game.isGameOver(), "Game should not be over after reset");
    }
    /**
     * Testuje pauzowanie gry
     */
    @Test
    public void testPauseGame() {
        game.togglePause();
        assertTrue(game.isPaused(), "Game should be paused after toggling pause");
        game.togglePause();
        assertFalse(game.isPaused(), "Game should resume after toggling pause again");
    }
    /**
     * Testuje przejście do kolejnej fali przeciwników
     */
    @Test
    public void testNextWave() {
        game.getEnemies().clear();
        game.nextWave();
        assertEquals(40, game.getEnemies().size(), "Next wave should initialize 40 enemies");
        assertEquals(0, game.getBullets().size(), "Bullets should be cleared after next wave");
    }
    /**
     * Testuje inicjalizację przeciwników
     */
    @Test
    public void testInitializeEnemies() {
        game.initializeEnemies();
        assertEquals(40, game.getEnemies().size(), "Enemies should be initialized correctly");
    }
    /**
     * Testuje inicjalizację pocisków
     */
    @Test
    public void testInitializeBullets() {
        game.initializeBullets();
        assertEquals(0, game.getBullets().size(), "Bullets should be initialized correctly");
    }
    /**
     * Testuje obsługę zdarzeń klawiatury
     */
    @Test
    public void testKeyHandling() {
        int initialX = game.getPlayer().getX();
        game.keyPressed(new KeyEvent(game, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_LEFT, ' '));
        assertTrue(game.getPlayer().getX() < initialX);

        initialX = game.getPlayer().getX();
        game.keyPressed(new KeyEvent(game, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_RIGHT, ' '));
        assertTrue(game.getPlayer().getX() > initialX);

        int initialBullets = game.getBullets().size();
        game.keyPressed(new KeyEvent(game, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_SPACE, ' '));
        assertEquals(initialBullets + 1, game.getBullets().size());
    }
    /**
     * Testuje reakcję na naciśnięcie klawisza 'r' w celu restartu gry
     */
    @Test
    public void testRestartKey() {
        game.getEnemyBullets().add(new EnemyBullet(0, 0));
        game.keyPressed(new KeyEvent(game, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_R, ' '));
        assertFalse(game.isGameOver());
    }
    /**
     * Testuje renderowanie ekranu końca gry (GAME OVER)
     */
    @Test
    public void testGameOverRendering() {
        game.isGameOver = true;
        Graphics g = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB).getGraphics();
        assertDoesNotThrow(() -> game.paintComponent(g));
    }
    /**
     * Testuje renderowanie gry podczas normalnej rozgrywki
     */
    @Test
    public void testInGameRendering() {
        Graphics g = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB).getGraphics();
        assertDoesNotThrow(() -> game.paintComponent(g));
    }
}
