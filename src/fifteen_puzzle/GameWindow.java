package fifteen_puzzle;
import javax.swing.*;
import java.awt.*;

class GameWindow extends JFrame {
    private GameBoard gameBoard;

    GameWindow() {
        setTitle("Fifteen Puzzle");
        setLayout(new FlowLayout());
        setSize(700, 550);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gameBoard = new GameBoard();
        add(gameBoard);

        setVisible(true);
    }
}
