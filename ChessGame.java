// ChessGame.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChessGame extends JFrame {
    private ChessBoard board;
    private JPanel controlPanel;
    private JButton newGameButton;
    private JComboBox<String> gameModeComboBox;
    private JLabel statusLabel;
    private boolean playerVsPlayer = true;

    public ChessGame() {
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("Java Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        board = new ChessBoard(this);
        add(board, BorderLayout.CENTER);

        controlPanel = new JPanel();
        newGameButton = new JButton("New Game");
        gameModeComboBox = new JComboBox<>(new String[]{"Player vs Player", "Player vs AI"});
        statusLabel = new JLabel("White's turn");

        controlPanel.add(newGameButton);
        controlPanel.add(gameModeComboBox);
        controlPanel.add(statusLabel);

        add(controlPanel, BorderLayout.SOUTH);

        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.newGame();
                updateStatus("White's turn");
            }
        });

        gameModeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerVsPlayer = gameModeComboBox.getSelectedIndex() == 0;
                board.newGame();
                updateStatus("White's turn");
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void updateStatus(String status) {
        statusLabel.setText(status);
    }

    public boolean isPlayerVsPlayer() {
        return playerVsPlayer;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ChessGame();
            }
        });
    }
}