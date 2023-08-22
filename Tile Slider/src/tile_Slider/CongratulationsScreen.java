package tile_Slider;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CongratulationsScreen extends JFrame {

    private JLabel congratulationsLabel;
    private JButton tryAgainButton;
    private JButton exitButton;
    JLabel imgLabel;

    public CongratulationsScreen(int rows, int columns, Tile[][] tempTile) {
        imgLabel = new JLabel();
        imgLabel.setBackground(Color.BLACK);

        setTitle("Congratulations");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setVisible(true);

        initializeComponents();
        setupLayout();
        setupListeners(imgLabel, rows, columns, tempTile);

    }

    private void initializeComponents() {
        congratulationsLabel = new JLabel("Congratulations! You have won the game.");

        tryAgainButton = new JButton("Try Again");
        exitButton = new JButton("Exit");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.add(congratulationsLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(tryAgainButton);
        buttonPanel.add(exitButton);

        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
    }

    private void setupListeners(JLabel imgLabel, int rows, int columns, Tile[][] tempTiles) {
        tryAgainButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the congratulations screen

                Game.gFrame.setVisible(false);

                new Menu(); // Create a new instance of the Menu class
            }
        });

        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Handle "Exit" button click event here
                System.exit(0); // Exit the application
            }
        });
    }
}