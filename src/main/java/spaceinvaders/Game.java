package spaceinvaders;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import javax.sound.sampled.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;

/**
 * Klasa reprezentująca główną logikę gry
 */
public class Game extends JPanel implements KeyListener{
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private final int SHOOT_COOLDOWN = 500;
    private List<Enemy> enemies;
    private List<Bullet> bullets;
    private List<EnemyBullet> enemyBullets;
    private List<Bunker> bunkers;
    private Player player;
    private int score;
    private int highScore;
    public boolean isGameOver;
    private boolean isPaused = false;
    private boolean isMusicOn = true;
    private int enemyDirection = 1; // 1 - ruch w prawo, -1 - ruch w lewo
    private int lives = 3;
    private long lastShotTime = 0;
    private Clip backgroundMusic;
    private Timer enemyTimer;
    private Timer bulletTimer;
    private Timer repaintTimer;
    private Timer enemyShootTimer;
    private Timer enemyBulletMoveTimer;
    private Image backgroundImage;
    /**
     * Konstruktor inicjalizujący grę
     */
    public Game() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        try {
            backgroundImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream("background_image.png"));
        } catch (IOException e) {
            System.err.println("Failed to load background image: " + e.getMessage());
            backgroundImage = null;
        }

        player = new Player(WIDTH / 2 - 25, HEIGHT - 50);
        score = 0;
        isGameOver = false;
        enemyBullets = new java.util.ArrayList<>();
        loadHighScore();
        playBackgroundMusic();
        initializeEnemies();
        initializeBullets();
        initializeBunkers();
        runGameLoop();
    }
    /**
     * Wczytuje najwyższy wynik z pliku
     */
    private void loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader("highscore.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                highScore = Integer.parseInt(line);
            } else {
                highScore = 0;
            }
        } catch (IOException e) {
            highScore = 0;
        }
    }
    /**
     * Zapisuje najwyższy wynik do pliku
     */
    private void saveHighScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("highscore.txt"))) {
            writer.write(String.valueOf(highScore));
        } catch (IOException e) {
            System.err.println("Could not save high score: " + e.getMessage());
        }
    }
    /**
     * Inicjalizuje przeciwników
     */
    public void initializeEnemies() {
        enemies = new java.util.ArrayList<>();
        int rows = 4;
        int cols = 10;
        for (int row = 0; row < rows; row++){
            for (int i = 0; i < cols; i++) {
                enemies.add(new Enemy(50 + i * 40, 50 + row * 40));
            }
        }
    }
    /**
     * Inicjalizuje listę pocisków
     */
    public void initializeBullets() {
        bullets = new java.util.ArrayList<>();
    }
    /**
     * Inicjalizuje bunkry
     */
    private void initializeBunkers() {
        bunkers = new java.util.ArrayList<>();
        int bunkerY = HEIGHT - 150;
        int bunkerSpacing = 150;
        for (int i = 0; i < 4; i++) {
            bunkers.add(new Bunker(100 + i * bunkerSpacing, bunkerY));
        }
    }
    /**
     * Włącza lub wyłącza pauzę w grze
     */
    public void togglePause() {
        if (isGameOver) return;
        isPaused = !isPaused;
        if (isPaused) {
            enemyTimer.stop();
            bulletTimer.stop();
            repaintTimer.stop();
            enemyShootTimer.stop();
            enemyBulletMoveTimer.stop();
        } else {
            enemyTimer.start();
            bulletTimer.start();
            repaintTimer.start();
            enemyShootTimer.start();
            enemyBulletMoveTimer.start();
        }
    }
    /**
     * Włącza lub wyłącza muzykę
     */
    private void toggleMusic() {
        if (backgroundMusic != null) {
            if (isMusicOn) {
                backgroundMusic.stop();
            } else {
                backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            }
            isMusicOn = !isMusicOn;
        }
    }
    /**
     * Uruchamia główną pętlę gry i inicjalizuje timery.
     */
    private void runGameLoop() {
        enemyTimer = new Timer(400, e -> {
            if (!isGameOver) {
                moveEnemies();
                checkCollisions();
            } else {
                ((Timer) e.getSource()).stop();
            }
        });

        bulletTimer = new Timer(30, e -> {
            if (!isGameOver) {
                moveBullets();
                checkCollisions();
                checkBulletBunkerCollisions();
            }
        });
        repaintTimer = new Timer(10, e -> {
            if (!isGameOver) {
                repaint();
            }
        });
        enemyShootTimer = new Timer(400, e -> {
            if (!isGameOver) {
                enemyShoot();
            }
        });
        enemyBulletMoveTimer = new Timer(30, e -> {
            if (!isGameOver) {
                moveEnemyBullets();
                checkPlayerHit();
            }
        });
        enemyTimer.start();
        bulletTimer.start();
        repaintTimer.start();
        enemyShootTimer.start();
        enemyBulletMoveTimer.start();
    }
    /**
     * Przemieszcza przeciwników oraz zmienia kierunek ich ruchu, gdy dotrą do krawędzi ekranu
     */
    public void moveEnemies() {
        boolean changeDirection = false;
        for (Enemy enemy : enemies) {
            enemy.setX(enemy.getX() + enemyDirection * 20);
            if (enemy.getX() <= 0 || enemy.getX() + enemy.getWidth() >= WIDTH) {
                changeDirection = true;
            }
            int bunkerY = HEIGHT - 150;
            if (enemy.getY() + enemy.getHeight() >= bunkerY) {
                isGameOver = true;
                break;
            }
        }
        if (changeDirection) {
            enemyDirection *= -1;
            for (Enemy enemy : enemies) {
                enemy.setY(enemy.getY() + 20);

                if (enemy.getY() + enemy.getHeight() > player.getY()) {
                    isGameOver = true;
                }
            }
        }
    }
    /**
     * Tworzy nowy pocisk i dodaje go do listy jeśli minął odpowiedni czas od ostatniego strzału
     */
    public void shoot() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime >= SHOOT_COOLDOWN) {
            int bulletX = player.getX() + player.getWidth() / 2 - 2;
            int bulletY = player.getY();

            bullets.add(new Bullet(bulletX, bulletY));
            lastShotTime = currentTime;
        }
    }
    /**
     * Przesuwa pociski gracza i usuwa te które opuszczą ekran lub trafią przeciwnika
     */
    private void moveBullets() {
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            bullet.move();
            if (!bullet.isActive()) {
                bullets.remove(i);
                i--;
            }
        }
    }
    /**
     * Sprawia, że losowi przeciwnicy strzelają w stronę gracza.
     */
    SecureRandom random = new SecureRandom();
    private void enemyShoot() {
        for (Enemy enemy : enemies) {
            if (random.nextDouble() < 0.01) { // 1% chance to shoot
                int bulletX = enemy.getX() + enemy.getWidth() / 2 - 2;
                int bulletY = enemy.getY() + enemy.getHeight();
                enemyBullets.add(new EnemyBullet(bulletX, bulletY));
            }
        }
    }
    /**
     * Przesuwa pociski przeciwników
     */
    private void moveEnemyBullets() {
        for (int i = 0; i < enemyBullets.size(); i++) {
            EnemyBullet bullet = enemyBullets.get(i);
            bullet.move();
            if (!bullet.isActive()) {
                enemyBullets.remove(i);
                i--;
            }
        }
    }
    /**
     * Sprawdza kolizje pocisków z przeciwnikami
     */
    public void checkCollisions() {
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            for (int j = 0; j < enemies.size(); j++) {
                Enemy enemy = enemies.get(j);
                Rectangle enemyRect = new Rectangle(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());
                if (bullet.getBounds().intersects(enemyRect)) {
                    bullets.remove(i);
                    enemies.remove(j);
                    score += 10;
                    i--;
                    break;
                }
            }
        }
        if (enemies.isEmpty()) {
            nextWave();
        }
    }
    /**
     * Sprawdza kolizje pocisków z bunkrami
     */
    private void checkBulletBunkerCollisions() {
        java.util.List<Bunker> bunkersToRemove = new java.util.ArrayList<>();
        for (Bullet bullet : bullets) {
            for (Bunker bunker : bunkers) {
                if (bunker.getBounds().intersects(bullet.getBounds())) {
                    bullet.setActive(false);
                    if (bunker.takeDamage()) {
                        bunkersToRemove.add(bunker);
                    }
                    break;
                }
            }
        }
        for (EnemyBullet bullet : enemyBullets) {
            for (Bunker bunker : bunkers) {
                if (bunker.getBounds().intersects(bullet.getBounds())) {
                    bullet.setActive(false);
                    if (bunker.takeDamage()) {
                        bunkersToRemove.add(bunker);
                    }
                    break;
                }
            }
        }
        bunkers.removeAll(bunkersToRemove);
    }
    /**
     * Sprawdza, czy gracz został trafiony przez pocisk przeciwnika.
     */
    public void checkPlayerHit() {
        for (EnemyBullet bullet : enemyBullets) {
            if (bullet.getBounds().intersects(player.getBounds())) {
                lives--;
                bullet.setActive(false);
                if (lives <= 0) {
                    isGameOver = true;
                    if (score > highScore) {
                        highScore = score;
                        saveHighScore();
                    }
                }
                break;
            }
        }
    }
    /**
     * Przechodzi do następnej fali przeciwników resetuje ich pozycje i odradza bunkry
     */
    public void nextWave() {
        initializeEnemies();
        initializeBullets();
        initializeBunkers();
        enemyBullets.clear();
        enemyDirection = 1;
    }
    /**
     * Resetuje stan gry i rozpoczyna nową rundę.
     */
    public void resetGame() {
        if (enemyTimer != null) enemyTimer.stop();
        if (bulletTimer != null) bulletTimer.stop();
        if (repaintTimer != null) repaintTimer.stop();
        if (enemyShootTimer != null) enemyShootTimer.stop();
        if (enemyBulletMoveTimer != null) enemyBulletMoveTimer.stop();
        player = new Player(WIDTH / 2 - 25, HEIGHT - 50);
        score = 0;
        lives = 3;
        isGameOver = false;
        enemyBullets = new java.util.ArrayList<>();
        initializeEnemies();
        initializeBullets();
        initializeBunkers();
        enemyDirection = 1;
        runGameLoop();
    }
    /**
     * Odtwarza muzykę w tle w pętli
     */
    private void playBackgroundMusic() {
        try {
            InputStream audioResource = getClass().getResourceAsStream("/background_music.wav");
            if (audioResource == null) {
                System.err.println("Background music file not found");
                return;
            }
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioResource);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioInputStream);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }catch(Exception e){
            System.err.println("Could not play background music: " + e.getMessage());
        }
    }
    /**
     * Rysuje komponenty gry na ekranie
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT, this);
        }
        if (isGameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("GAME OVER", WIDTH / 2 - 150, HEIGHT / 2);

            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Press 'R' to Restart", WIDTH / 2 - 100, HEIGHT / 2 + 50);

            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString("High Score: " + highScore, WIDTH / 2 - 100, HEIGHT / 2 + 100);
            return;
        }

        player.draw(g);
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }
        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }
        for (EnemyBullet bullet : enemyBullets) {
            bullet.draw(g);
        }
        for (Bunker bunker : bunkers) {
            bunker.draw(g);
        }
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Lives: " + lives, WIDTH - 100, 20);
    }
    /**
     * Obsługuje zdarzenia naciśnięcia klawiszy
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            player.moveLeft();
        } else if (key == KeyEvent.VK_RIGHT) {
            player.moveRight();
        } else if (key == KeyEvent.VK_SPACE) {
            shoot();
        } else if (key == KeyEvent.VK_R && isGameOver) {
            resetGame();
        } else if (key == KeyEvent.VK_P) {
            togglePause();
        } else if (key == KeyEvent.VK_M) {
            toggleMusic();
        }
        repaint();
    }
    /**
     * Obsługuje zdarzenia spuszczenia klawisza
     */
    @Override
    public void keyReleased(KeyEvent e) {
    }
    /**
     * Obsługuje zdarzenia wpisania znaku
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }
    /**
     * Metoda główna uruchamiająca grę, tworzy okno i inicjalizuje instancję gry
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Space Invaders");
            Game game = new Game();
            frame.add(game);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
    /**
     * Zwraca listę przeciwników obecnych w grze
     */
    public List<Enemy> getEnemies() {
        return enemies;
    }
    /**
     * Zwraca listę pocisków wystrzelonych przez gracza
     */
    public List<Bullet> getBullets() {
        return bullets;
    }
    /**
     * Aktualną listę żyć gracza
     */
    public int getLives() {
        return lives;
    }
    /**
     * Zwraca aktualny wynik
     */
    public int getScore() {
        return score;
    }
    /**
     * Zwraca obiekt reprezentujący gracza
     */
    public Player getPlayer() {
        return player;
    }
    /**
     * Sprawdza, czy gra się zakończyła
     */
    public boolean isGameOver() {
        return isGameOver;
    }
    /**
     * Zwraca listę pocisków wystrzelonych przez przeciwników
     */
    public List<EnemyBullet> getEnemyBullets() {
        return enemyBullets;
    }
    /**
     * Sprawdza czy aktywna jest pauza
     */
    public boolean isPaused() {
        return isPaused;
    }
}