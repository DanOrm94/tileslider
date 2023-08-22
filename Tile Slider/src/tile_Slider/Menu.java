package tile_Slider;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Menu extends JFrame {

    public static final int WIDTH = 500;
    public static final int HEIGHT = WIDTH / 12 * 9;
    public static final int SCALE = 2;
    public final String TITLE = "TILE SLIDER";
    public static JFrame startMenu;
    static JButton playButton;
    public static BufferedImage[] imageArray;
    private static String customImagePath = "";
    private static JLabel selectedImageLabel = new JLabel("default image selected");
    private static Game gPanel;
    private static JTextField player1NameField;
    private static JTextField player2NameField;
    private static Game player1Game;
    private static Game player2Game;
    private static boolean twoPlayersMode = false;
    private static String player1Name = "";
    private static String player2Name = "";


    public static String playerName = ""; // Initialize playerName with an empty string

    private static JFrame gameScreen;

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        new Menu();
//        LoginFrame loginfieldFrame = new LoginFrame();
    }

    public Menu() {
        render();
    }

    public static void render() {

        startMenu = new JFrame("start menu");
        startMenu.pack();
        startMenu.setDefaultCloseOperation(EXIT_ON_CLOSE);
        startMenu.setResizable(false);
        startMenu.setLocationRelativeTo(null);
        startMenu.setVisible(true);
        startMenu.setBounds(0, 0, 600, 600);
        startMenu.setLayout(null);
        populateFrame();

    }

    public static void populateFrame() {
        File selectedFile = null;
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10)); // 3 rows, 2 columns, with 10px padding
        JLabel rowLabel = new JLabel("Rows (2-10):");
        JTextField rowField = new JTextField();
        JLabel colLabel = new JLabel("Columns (2-10):");
        JTextField colField = new JTextField();

        // Add the input components to the input panel
        inputPanel.add(rowLabel);
        inputPanel.add(rowField);
        inputPanel.add(colLabel);
        inputPanel.add(colField);

        // Add the "Select image" button
        JPanel selectImagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton selectImageButton = new JButton("Select Image");
        selectImagePanel.add(selectImageButton);
        selectImagePanel.add(selectImageButton);
        selectImagePanel.add(selectedImageLabel);

        JPanel buttonPanel = new JPanel();
        playButton = new JButton("Play");
        buttonPanel.add(playButton);
        playButton.setPreferredSize(new Dimension(100, 100));

        JPanel playerModePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton onePlayerButton = new JButton("1 Player");
        JButton twoPlayersButton = new JButton("2 Players");
        playerModePanel.add(onePlayerButton);
        playerModePanel.add(twoPlayersButton);

        JPanel playerNamePanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JLabel player1NameLabel = new JLabel("Player 1 Name:");
        player1NameField = new JTextField();
        JLabel player2NameLabel = new JLabel("Player 2 Name:");
        player2NameField = new JTextField();
        playerNamePanel.add(player1NameLabel);
        playerNamePanel.add(player1NameField);
        playerNamePanel.add(player2NameLabel);
        playerNamePanel.add(player2NameField);

        // Add the input and button panels to the start menu
        startMenu.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1;
        startMenu.add(inputPanel, gbc);

        gbc.gridy++;
        startMenu.add(playerModePanel, gbc);

        gbc.gridy++;
        startMenu.add(playerNamePanel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 10, 0); // Add some spacing between components
        startMenu.add(selectImagePanel, gbc);

        gbc.gridy++;
        gbc.weighty = 0;
        startMenu.add(buttonPanel, gbc);


        startMenu.pack();
        startMenu.setDefaultCloseOperation(EXIT_ON_CLOSE);
        startMenu.setResizable(false);
        startMenu.setLocationRelativeTo(null);
        startMenu.setVisible(true);
        startMenu.setBounds(0, 0, 600, 600);

        selectImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // When the "Select image" button is clicked, show the file chooser dialog
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                int result = chooser.showOpenDialog(startMenu);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = chooser.getSelectedFile();
                    try {
                        BufferedImage image = ImageIO.read(selectedFile);
                        int width = image.getWidth();
                        int height = image.getHeight();

                        if (width > 1000 || height > 1000) {
                            JOptionPane.showMessageDialog(startMenu, "File too big! Pick another file");
                        } else {
                            customImagePath = selectedFile.getAbsolutePath();
                            selectedImageLabel.setText("custom image selected");

                        }

                    } catch (Exception e1) {

                        e1.printStackTrace();
                    }

                }
            }
        });

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int numRows = Integer.parseInt(rowField.getText());
                int numCols = Integer.parseInt(colField.getText());

                if (numRows < 2 || numRows > 10 || numCols < 2 || numCols > 10) {
                    JOptionPane.showMessageDialog(startMenu,
                            "Please enter values between 2 and 10 for both rows and columns.", "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    if (twoPlayersMode) {
                        startGameAfterUserValuesChecked(numRows, numCols, customImagePath, playerName, twoPlayersMode);
                    } else {
                        startGameAfterUserValuesChecked(numRows, numCols, customImagePath, playerName, twoPlayersMode);
                    }

                }
            }
        });

        onePlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // When the "1 Player" button is clicked, switch to one player mode
                twoPlayersMode = false;
                player1NameField.setEnabled(true);
                player2NameField.setEnabled(false);
            }
        });

        twoPlayersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // When the "2 Players" button is clicked, switch to two players mode
                twoPlayersMode = true;
                player1NameField.setEnabled(true);
                player2NameField.setEnabled(true);
            }
        });

    }

    public static void startGameAfterUserValuesChecked(int numRows, int numCols, String imagePath, String playerName,
                                                       boolean twoPlayersMode) {

        startMenu.setVisible(false);

        if (imagePath != "") {
            imagePath = customImagePath;
        } else {
            Path filePath = Paths.get("Resources");
            Path fullFilePath = filePath.toAbsolutePath();
            imagePath = (fullFilePath.toString() + "\\defaultimage.jpeg");
        }

        BufferedImage myPicture = null;
        try (FileInputStream inputStream = new FileInputStream(imagePath)) {
            myPicture = ImageIO.read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (myPicture == null) {
            System.err.println("Error: Failed to read image from file.");
            return;
        }

        JLabel picLabel = new JLabel(new ImageIcon(myPicture));

        if (twoPlayersMode) {

            String[] playersName = new String[2];
            playersName[0] = player1NameField.getText();
            playersName[1] = player2NameField.getText();
            createGamePanel(numRows, numCols, picLabel, myPicture, playersName);

        } else {

            String[] playersName = {player1NameField.getText()};
            createGamePanel(numRows, numCols, picLabel, myPicture, playersName);

        }

    }

    public static void createGamePanel(int rows, int columns, JLabel picLabel, BufferedImage image,
                                       String[] playersName) {
        // Creates the game panel (all the setup is in the Game class)
        player1Game = new Game(rows, columns, image, picLabel, playersName, twoPlayersMode);

    }

    public static JFrame getGameScreen() {

        return gameScreen;
    }

}