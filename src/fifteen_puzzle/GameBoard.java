package fifteen_puzzle;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class GameBoard extends JPanel{
    //jakiś komentarz
    //board
    final private int boardX = 0;
    final private int boardY = 100;
    final private int boardSize = 300;
    final private int margin = 25;

    //title label
    final private int titleX = 0;
    final private int titleY = 0;
    final private int titleWidth = 600;
    final private int titleHeight = 50;

    //time label
    final private int timerX = 400;
    final private int timerY = 100;
    final private int timerWidth = 200;
    final private int timerHeight = 50;

    //moves label
    final private int movesX = 400;
    final private int movesY = 200;
    final private int movesWidth = 200;
    final private int movesHeight = 50;

    //pause button
    final private int pauseX = 400;
    final private int pauseY = 300;
    final private int pauseWidth = 200;
    final private int pauseHeight = 50;

    //reset button
    final private int resetX = 400;
    final private int resetY = 400;
    final private int resetWidth = 200;
    final private int resetHeight = 50;

    private Font titleFont = new Font("SansSerif", Font.BOLD, 50);
    private Font tileFont = new Font("SansSerif", Font.BOLD, 20);
    private Font pauseFont = new Font("SansSerif", Font.PLAIN, 40);
    private Font informationFont = new Font("SansSerif", Font.BOLD, 20);
    private Font victoryFont = new Font("SansSerif", Font.BOLD, 30);

    //tiles
    private int[] tiles;
    private int dimension = 4;
    private int tileSize;
    private int blankPos;
    private int swapPos = -1;

    //time
    private String time;
    private int minutes;
    private int seconds;

    private int moves;
    private int move = 5;

    //private BufferedImage frame;

    private enum Direction{
        LEFT, RIGHT, UP, DOWN;
    }
    private Direction direction;
    private boolean isMoving = false;

    private boolean gameOver;
    private boolean isPaused;

    private final Random RANDOM = new Random();
    private javax.swing.Timer animation;
    private java.util.Timer timer;

    GameBoard(){
        setPreferredSize(new Dimension(600,600));
        setFocusable(true);

        setAnimation();
        setTimer();
        reset();
        addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                if(x >= resetX && x <= (resetX + resetWidth) && y >= resetY && y <= (resetY + resetHeight)){
                    reset();
                }
                else if(!gameOver){
                    if(x >= pauseX && x <= (pauseX + pauseWidth) && y >= pauseY && y <= (pauseY + pauseHeight)){
                        isPaused = !isPaused;
                        repaint();
                    }
                    else if(!isMoving && !isPaused){
                        x = e.getX() - boardX - margin;
                        y = e.getY() - boardY - margin;
                        if(x >= 0 && x <= dimension*tileSize && y >= 0 && y <= dimension*tileSize){
                            swapPos = (y/tileSize) * dimension + (x/tileSize);
                            if((swapPos/dimension == blankPos/dimension && Math.abs(swapPos - blankPos) == 1) || Math.abs(swapPos - blankPos) == dimension){
                                if(swapPos == blankPos - 1) direction = Direction.RIGHT;
                                if(swapPos == blankPos + 1) direction = Direction.LEFT;
                                if(swapPos == blankPos - dimension) direction = Direction.DOWN;
                                if(swapPos == blankPos + dimension) direction = Direction.UP;
                                moves++;
                                animation.start();
                            }
                        }
                    }
                }
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(!isMoving && !isPaused && !gameOver){
                    switch(e.getKeyCode()){
                        case KeyEvent.VK_LEFT:
                            if(blankPos % dimension < dimension - 1){
                                swapPos = blankPos + 1;
                                direction = Direction.LEFT;
                                moves++;
                                animation.start();
                            }
                            break;
                        case KeyEvent.VK_RIGHT:
                            if(blankPos % dimension > 0){
                                swapPos = blankPos - 1;
                                direction = Direction.RIGHT;
                                moves++;
                                animation.start();
                            }
                            break;
                        case KeyEvent.VK_UP:
                            if(blankPos/dimension < dimension - 1){
                                swapPos = blankPos + dimension;
                                direction = Direction.UP;
                                moves++;
                                animation.start();
                            }
                            break;
                        case KeyEvent.VK_DOWN:
                            if(blankPos/dimension > 0){
                                swapPos = blankPos - dimension;
                                direction = Direction.DOWN;
                                moves++;
                                animation.start();
                            }
                            break;
                    }
                }
            }
        });
    }

    private void reset(){
        //board update
        tileSize = boardSize / dimension;
        blankPos = dimension*dimension - 1;
        tiles = new int[dimension*dimension];
        for (int i = 0; i < tiles.length-1; i++) {
            tiles[i] = i+1;
        }
        shuffle();

        //timer update
        timer.cancel();
        setTimer();

        //moves counter update
        moves = 0;
        repaint();

        gameOver = false;
        isPaused = false;
    }

    private void setAnimation(){
        move = 0;
        animation = new javax.swing.Timer(10, (e) ->{
            isMoving = true;
            move += 5;
            repaint();
            if(move == tileSize){
                animation.stop();
                move = 0;
                int temp = tiles[swapPos];
                tiles[swapPos] = tiles[blankPos];
                tiles[blankPos] = temp;
                blankPos = swapPos;
                swapPos = -1;
                isMoving = false;
                gameOver = isGameOver();
            }
        });
    }

    private void setTimer(){
        //time = "czas: 00:00";
        seconds = 0;
        minutes = 0;
        timer = new java.util.Timer();
        timer.schedule(new TimerTask(){
            @Override
            public void run() {
                if(!isPaused && !gameOver){
                    time = "czas: ";
                    if(seconds == 60){
                        seconds = 0;
                        minutes++;
                    }
                    if(minutes < 10){
                        time += "0";
                    }
                    time += String.valueOf(minutes) + ":";
                    if(seconds < 10){
                        time += "0";
                    }
                    time += String.valueOf(seconds);
                    seconds++;
                    repaint();
                }
            }
        }, 0, 1000);
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawTitle(g2D);
        drawFrame(g2D);
        drawTiles(g2D);
        drawTimer(g2D);
        drawMoves(g2D);
        drawPause(g2D);
        drawReset(g2D);
        if(isPaused) drawPausedScreen(g2D);
        if(isGameOver()) drawVictoryScreen(g2D);
    }

    private void drawTitle(Graphics2D g2D){
        g2D.setColor(Color.DARK_GRAY);
        g2D.setFont(titleFont);
        drawCenteredString(g2D,"FIFTEEN PUZZLE", titleX, titleY, titleWidth, titleHeight);
    }

    private void drawPausedScreen(Graphics2D g2D){
        g2D.setColor(new Color(0f,0f,0f,.8f ));
        g2D.fillRoundRect(0, 100, dimension*tileSize + 2*margin, dimension*tileSize + 2*margin, 20, 20);
        g2D.setColor(Color.WHITE);
        g2D.setFont(pauseFont);
        drawCenteredString(g2D,"P A U Z A", 0, 100, dimension*tileSize + 2*margin, dimension*tileSize + 2*margin);
    }

    private void drawVictoryScreen(Graphics2D g2D){
        g2D.setColor(new Color(0f,0f,0f,.8f ));
        g2D.fillRoundRect(0, 100, dimension*tileSize + 2*margin, dimension*tileSize + 2*margin, 20, 20);
        g2D.setColor(Color.YELLOW);
        g2D.setFont(victoryFont);
        drawCenteredString(g2D,"Z W Y C I Ę S T W O", 0, 100, dimension*tileSize + 2*margin, dimension*tileSize + 2*margin);
    }

    private void drawTimer(Graphics2D g2D){
        g2D.setColor(Color.ORANGE);
        g2D.fillRoundRect(timerX, timerY, timerWidth, timerHeight, 20, 20);
        g2D.setColor(Color.BLACK);
        g2D.setFont(informationFont);
        drawCenteredString(g2D, time, timerX, timerY, timerWidth, timerHeight);
    }

    private void drawMoves(Graphics2D g2D){
        g2D.setColor(Color.ORANGE);
        g2D.fillRoundRect(movesX, movesY, movesWidth, movesHeight, 20, 20);
        g2D.setColor(Color.BLACK);
        g2D.setFont(informationFont);
        drawCenteredString(g2D, "liczba ruchów: " + String.valueOf(moves), movesX, movesY, movesWidth, movesHeight);
    }

    private void drawReset(Graphics2D g2D){
        g2D.setColor(new Color(150, 150, 255));
        g2D.fillRoundRect(resetX, resetY, resetWidth, resetHeight, 20, 20);
        g2D.setColor(Color.BLACK);
        g2D.setFont(informationFont);
        drawCenteredString(g2D, "reset", resetX, resetY, resetWidth, resetHeight);
    }

    private void drawPause(Graphics2D g2D){
        g2D.setColor(new Color(150, 150, 255));
        g2D.fillRoundRect(pauseX, pauseY, pauseWidth, pauseHeight, 20, 20);
        g2D.setColor(Color.BLACK);
        g2D.setFont(informationFont);
        drawCenteredString(g2D, "pauza", pauseX, pauseY, pauseWidth, pauseHeight);
    }

    private void drawFrame(Graphics2D g2D){
        g2D.setColor(Color.DARK_GRAY);
        g2D.fillRoundRect(0, 100, dimension*tileSize + 2*margin, dimension*tileSize + 2*margin, 20, 20);
        g2D.setColor(Color.BLACK);
        g2D.drawRoundRect(0, 100, dimension*tileSize + 2*margin, dimension*tileSize + 2*margin, 20, 20);
        //g2D.drawImage(frame,0, 0, null);

        g2D.setColor(Color.LIGHT_GRAY);
        g2D.fillRoundRect(margin, 100 + margin, dimension*tileSize, dimension*tileSize, 20, 20);
        g2D.setColor(Color.BLACK);
        g2D.drawRoundRect(margin, 100 + margin, dimension*tileSize, dimension*tileSize, 20, 20);
    }

    private void drawTiles(Graphics2D g2D){
        g2D.setFont(tileFont);
        int x, y, index;
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                index = dimension*i + j;
                if(tiles[index] == 0)
                    continue;
                else{
                    x = margin + j*tileSize;
                    y = margin + i*tileSize;
                    if((index == swapPos)) {
                        switch (direction) {
                            case UP:
                                y -= move;
                                break;
                            case DOWN:
                                y += move;
                                break;
                            case RIGHT:
                                x += move;
                                break;
                            case LEFT:
                                x -= move;
                                break;
                        }
                    }
                }
                drawTile(g2D, index, x, 100 + y);
            }
        }
    }

    private void drawTile(Graphics2D g2D, int index, int x, int y){
        g2D.setColor(Color.CYAN);
        g2D.fillRoundRect(x, y, tileSize, tileSize, 20, 20);
        g2D.setColor(Color.BLACK);
        g2D.drawRoundRect(x, y, tileSize, tileSize, 20, 20);
        g2D.setColor(Color.BLACK);
        drawCenteredString(g2D, String.valueOf(tiles[index]), x, y, tileSize, tileSize);
    }

    private void drawCenteredString(Graphics2D g2D, String s, int x, int y, int width, int height){
        FontMetrics fm = g2D.getFontMetrics();
        int asc = fm.getAscent();
        int desc = fm.getDescent();
        int stringWidth = fm.stringWidth(s);
        g2D.drawString(s, x + (width-stringWidth)/2, y + asc + (height - (asc+desc)) / 2);
    }

    private int getInversionsNumber(){
        int invCount = 0;
        for (int i = 0; i < dimension*dimension - 2; i++) {
            for (int j = i+1; j < dimension*dimension - 1; j++) {
                if(tiles[i] > tiles[j]){
                    invCount++;
                }
            }
        }
        return invCount;
    }

    private boolean isSolvable(){
        int invCount = getInversionsNumber();
        if(invCount % 2 == 0){
            return true;
        }
        else return false;
    }

    private void shuffle(){
        for (int i = tiles.length - 2; i > 0; i--) {
            int r = RANDOM.nextInt(i);
            int temp = tiles[i];
            tiles[i] = tiles[r];
            tiles[r] = temp;
        }
        tiles[tiles.length-1] = 0;
        if(!isSolvable() || isGameOver()) shuffle();
    }

    private boolean isGameOver(){
        if(tiles[tiles.length-1] != 0)
            return false;
        else{
            for (int i = 0; i < tiles.length-1; i++) {
                if(tiles[i] != i+1)
                    return false;
            }
        }
        return true;
    }
}

