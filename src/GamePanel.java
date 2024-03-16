import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.awt.Color;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 75;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;
    JButton restartButton;
    int highestScore = 0;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        restartButton = new JButton("Try again?");
        restartButton.setBackground(Color.BLACK);
        restartButton.setForeground(Color.GREEN);
        restartButton.setFont(new Font("Ink Free", Font.BOLD, 30));
        restartButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });
        restartButton.setVisible(false);

        restartButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                Font font = restartButton.getFont();
                restartButton.setFont(new Font(font.getName(), Font.BOLD, font.getSize() + 6)); // Increase font size by 2
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                Font font = restartButton.getFont();
                restartButton.setFont(new Font(font.getName(), Font.BOLD, font.getSize() - 6)); // Decrease font size by 2
            }
        });

        this.add(restartButton);

        startGame();
    }


    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void restartGame() {
        running = true;
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        restartButton.setVisible(false); // Hide the button when restarting
        newApple();
        resetSnakePosition();
        timer.restart();
        repaint();
    }

    public void resetSnakePosition() {
        // Reset the snake's initial position
        x[0] = SCREEN_WIDTH / 2;
        y[0] = SCREEN_HEIGHT / 2;

        // Reset the positions of the body parts
        for (int i = 1; i < bodyParts; i++) {
            x[i] = x[0] - i * UNIT_SIZE;
            y[i] = y[0];
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor((new Color(45, 180, 0)));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.white);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("SCORE: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("SCORE: " + applesEaten)) / 2, g.getFont().getSize());
        } else {
            // Draw the "Game Over" text only if the game is over
            gameOver(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        //checks if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        //checks if head touches left border
        if (x[0] < 0) {
            running = false;
        }
        //checks if head touches right border
        if (x[0] > SCREEN_WIDTH) {
            running = false;
        }
        //checks if head touches top border
        if (y[0] < 0) {
            running = false;
        }
        //checks if head touches bottom border
        if (y[0] > SCREEN_HEIGHT) {
            running = false;
        }
        if (!running) {
            if (applesEaten > highestScore) {
                highestScore = applesEaten; // Update the highest score if necessary
            }
            timer.stop();
            restartButton.setVisible(true); // Show the button when the game is over
        }
    }

    public void gameOver(Graphics g) {
        //Score
        g.setColor(Color.white);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metricsScore = getFontMetrics(g.getFont());
        g.drawString("SCORE: " + applesEaten, (SCREEN_WIDTH - metricsScore.stringWidth("SCORE: " + applesEaten)) / 2, g.getFont().getSize());
        // Highest Score
        g.setColor(Color.white);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metricsHighestScore = getFontMetrics(g.getFont());
        g.drawString("HIGHEST SCORE: " + highestScore, (SCREEN_WIDTH - metricsHighestScore.stringWidth("HIGHEST SCORE: " + highestScore)) / 2, g.getFont().getSize() + 50);
        //Game Over text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metricsGameOver = getFontMetrics(g.getFont());
        g.drawString("GAME OVER!", (SCREEN_WIDTH - metricsGameOver.stringWidth("GAME OVER!")) / 2, SCREEN_HEIGHT / 2);

        // Positioning the restart button below the "Game Over" text
        restartButton.setBounds((SCREEN_WIDTH - restartButton.getPreferredSize().width) / 2, SCREEN_HEIGHT / 2 + 100, restartButton.getPreferredSize().width, restartButton.getPreferredSize().height);
        restartButton.revalidate();
        restartButton.repaint();

        // Add the restart button to the panel
        add(restartButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}
