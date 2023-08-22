package tile_Slider;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class Game extends JPanel {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public int columns;
    public int rows;
    public BufferedImage image;
    public static JFrame gFrame;
    public static BufferedImage[] imageArray;
    public static JPanel tempPanel;
    private Tile[][] tempTiles;
    private Tile[][] tempTiles2;
    private static int blankTileR;
    private static int blankTileC;
    private static int swapTileR;
    private static int swapTileC;
    private boolean player1Turn = true; // Initialize to true since the game starts with player 1's turn

    private Tile[][] originalOrder;
    private Tile[][] originalOrder2;

    int count = 0;
    int moveCount = 1;

    private String playerName1;
    private String playerName2;
    boolean gameWon;

    private boolean whosTurn = true; // true is player 1 false is player 2

    private boolean twoPlayersMode;

    int picturewidth = 0;
    int pictureheight = 0;

    public int getMoveCount() {
        return moveCount;
    }

    public static JLabel imageLabel;

    @SuppressWarnings("serial")

    public Game(int rows, int columns, BufferedImage image, JLabel picLabel, String[] playersName,
                boolean twoPlayersMode) {

        this.twoPlayersMode = twoPlayersMode;

        gFrame = new JFrame("Game");

        gFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        picturewidth = image.getWidth();
        pictureheight = image.getHeight();

        this.setFocusable(true);
        this.image = image;
        this.columns = columns;
        this.rows = rows;

        this.setBounds(0, 0, picturewidth, pictureheight);
        this.setSize(picturewidth, pictureheight);
        this.add(picLabel);
        this.setVisible(true);

        tempTiles = setImgToGrid(gFrame, rows, columns, image, false);
        if (twoPlayersMode) {
            tempTiles2 = setImgToGrid(gFrame, rows, columns, image, true);
            pictureheight = (image.getHeight() * 2) + 10;
            gFrame.setBounds(0, 0, picturewidth, pictureheight);
            gFrame.setSize(picturewidth, pictureheight);

            playerName2 = playersName[1];

        }

        // must set the player 1 name as it will be defualt regardless of two player or
        // not

        playerName1 = playersName[0];

        this.revalidate();
        this.repaint();

        InputMap inputMap = tempPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = tempPanel.getActionMap();
        inputMap.put(KeyStroke.getKeyStroke("LEFT"), "leftAction");
        inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "rightAction");
        inputMap.put(KeyStroke.getKeyStroke("UP"), "upAction");
        inputMap.put(KeyStroke.getKeyStroke("DOWN"), "downAction");
        inputMap.put(KeyStroke.getKeyStroke(MouseEvent.BUTTON1, 0), "leftClickAction");

        // adding the mouse listener to the main game panel

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                tempTiles[i][j].panel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (count != (rows * columns) - 1) {

                            Tile clickedTile = getClickedTile(tempTiles);
                            moveClickedTile(clickedTile);

                        }

                    }

                });
                count = count + 1;

            }
        }

        // setting the second game panel up if in 2 player mode

        if (twoPlayersMode) {
            for (int i = rows; i < 2 * rows; i++) {
                for (int j = 0; j < columns; j++) {
                    tempTiles2[i][j].panel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if (count != (rows * columns) - 1) {

                                Tile clickedTile = getClickedTile(tempTiles2);
                                moveClickedTile(clickedTile);

                            }

                        }

                    });
                    count = count + 1;

                }
            }
        }

        // Key board functions
        actionMap.put("leftAction", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Left");

                if (whosTurn) {
                    tempTiles = moveLeft(tempTiles, rows, columns);
                } else {
                    tempTiles2 = moveLeft(tempTiles2, rows, columns);
                }

                moveCount++;
                whosTurn = !whosTurn; // Switch turns

            }
        });
        actionMap.put("rightAction", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("right");

                if (whosTurn) {
                    tempTiles = moveRight(tempTiles, rows, columns);
                } else {
                    tempTiles2 = moveRight(tempTiles2, rows, columns);
                }

                moveCount++;
                whosTurn = !whosTurn; // Switch turns

            }
        });

        actionMap.put("upAction", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("up");

                if (whosTurn) {
                    tempTiles = moveUp(tempTiles, rows, columns);
                } else {
                    tempTiles2 = moveUp(tempTiles2, rows, columns);
                }

                moveCount++;
                whosTurn = !whosTurn; // Switch turns

            }
        });

        actionMap.put("downAction", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("down");
                swapTileR = blankTileR - 1;
                swapTileC = blankTileC;

                if (whosTurn) {
                    tempTiles = moveDown(tempTiles, rows, columns);
                } else {
                    tempTiles2 = moveDown(tempTiles2, rows, columns);
                }

                whosTurn = !whosTurn;
                moveCount++;
            }
        });

        // Initialize the original order of the tiles
        originalOrder = new Tile[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                originalOrder[i][j] = tempTiles[i][j];
            }
        }

        if (twoPlayersMode) {
            originalOrder2 = new Tile[2 * rows][columns];
            for (int i = rows; i < 2 * rows; i++) {
                for (int j = 0; j < columns; j++) {
                    originalOrder2[i - rows][j] = tempTiles2[i][j];
                }
            }
        }
    }


    private void moveClickedTile(Tile clickedTile) {
        if (clickedTile != null) {

            // player 2 turn with mouse click
            if (twoPlayersMode && !whosTurn) {
                System.out.print("Two player");

                int direction = availableSpace(tempTiles2, clickedTile);

                switch (direction) {
                    case 1:

                        tempTiles2 = moveUp(tempTiles2, rows, columns);

                        whosTurn = true;

                        break;
                    case 2:

                        tempTiles2 = moveDown(tempTiles2, rows, columns);

                        whosTurn = true;

                        break;
                    case 3:

                        tempTiles2 = moveLeft(tempTiles2, rows, columns);
                        whosTurn = true;

                        break;
                    case 4:

                        tempTiles2 = moveRight(tempTiles2, rows, columns);
                        whosTurn = true;

                        break;
                    default:
                        // No available space for the clicked tile
                        break;
                }

            }

            // this is player 1 turn
            else if (whosTurn) {
                System.out.print("one player");
                int direction = availableSpace(tempTiles, clickedTile);

                switch (direction) {
                    case 1:
                        tempTiles = moveUp(tempTiles, rows, columns);
                        whosTurn = false;

                        break;
                    case 2:
                        tempTiles = moveDown(tempTiles, rows, columns);
                        whosTurn = false;

                        break;
                    case 3:
                        tempTiles = moveLeft(tempTiles, rows, columns);
                        whosTurn = false;

                        break;
                    case 4:
                        tempTiles = moveRight(tempTiles, rows, columns);
                        whosTurn = false;

                        break;
                    default:
                        // No available space for the clicked tile
                        break;
                }

            }

            System.out.print("Click");
            moveCount++;
            gFrame.revalidate();
            gFrame.repaint();

            if (checkWinCondition(tempTiles,true)){
                gameWon= true;
            }
            // Check if the game is won after the move
            if (!whosTurn&&twoPlayersMode) {

                if (checkWinCondition(tempTiles2,false)) {
                    gameWon = true;
                }

            }
        }
    }

    private Tile getClickedTile(Tile[][] tiles) {
        if (tiles[0][0] != null) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    Tile tile = tiles[i][j];
                    if (tile.hasImage && tile.panel.getMousePosition() != null) {
                        return tile;
                    }
                }
            }
        } else {
            for (int i = rows; i < 2 * rows; i++) {
                for (int j = 0; j < columns; j++) {
                    Tile tile = tiles[i][j];
                    if (tile.hasImage && tile.panel.getMousePosition() != null) {
                        return tile;
                    }
                }
            }
        }
        return null;
    }

    public static Tile[][] setImgToGrid(JFrame frame, int rows, int columns, BufferedImage image, boolean twoplayer) {
        int width = 487, height = 463;
        imageArray = new BufferedImage[rows * columns];

        splitImageIntoArray(rows, columns, width / columns, height / rows, image);

        Tile[][] tiles = new Tile[rows * 3][columns * 3];
        Integer count = 0;
        Integer count2 = 0;

        if (!twoplayer) {
            for (int i = 0; i < rows; i++) {

                for (int j = 0; j < columns; j++) {

                    tempPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 1, 1));
                    tempPanel.setBorder(BorderFactory.createLineBorder(Color.black));

                    if (count != (rows * columns) - 1) {
                        JLabel imageLabel = new JLabel(new ImageIcon(imageArray[count]));
                        tempPanel.add(imageLabel);
                        tiles[i][j] = new Tile();
                        tiles[i][j].tileorder = count;
                        tiles[i][j].panel = tempPanel;
                        tiles[i][j].hasImage = true;
                    } else {

                        JPanel blankPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 1, 1));
                        blankPanel.setBorder(BorderFactory.createLineBorder(Color.black));
                        blankPanel.setBackground(Color.BLACK);
                        blankPanel.setBounds(j * width / columns, i * height / rows, width / columns, height / rows);

                        tiles[i][j] = new Tile();
                        tiles[i][j].tileorder = count;
                        tiles[i][j].panel = blankPanel;
                        tiles[i][j].image = imageArray[count];

                        System.out.println(blankPanel.getWidth() + " " + blankPanel.getHeight() + " "
                                + blankPanel.getLocation().toString());
                        frame.add(blankPanel);

                    }

                    tempPanel.setBackground(Color.WHITE);
                    frame.add(tempPanel);
                    tempPanel.setBounds(j * width / columns, i * height / rows, width / columns, height / rows);

                    count++;

                }

            }

        } else {
            for (int i = rows; i < 2 * rows; i++) {
                for (int j = 0; j < columns; j++) {
                    // ...

                    tempPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 1, 1));
                    tempPanel.setBorder(BorderFactory.createLineBorder(Color.black));

                    if (count2 != (rows * columns) - 1) {
                        JLabel imageLabel = new JLabel(new ImageIcon(imageArray[count2]));
                        tempPanel.add(imageLabel);
                        tiles[i][j] = new Tile();
                        tiles[i][j].panel = tempPanel;
                        tiles[i][j].hasImage = true;
                    } else {

                        JPanel blankPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 1, 1));
                        blankPanel.setBorder(BorderFactory.createLineBorder(Color.black));
                        blankPanel.setBackground(Color.BLACK);
                        blankPanel.setBounds(j * width / columns, (i * height / rows) + 50, width / columns,
                                height / rows);

                        tiles[i][j] = new Tile();

                        tiles[i][j].panel = blankPanel;
                        tiles[i][j].image = imageArray[count2];

                        System.out.println(blankPanel.getWidth() + " " + blankPanel.getHeight() + " "
                                + blankPanel.getLocation().toString());
                        frame.add(blankPanel);

                    }

                    tempPanel.setBackground(Color.WHITE);
                    frame.add(tempPanel);
                    tempPanel.setBounds(j * width / columns, (i * height / rows) + 50, width / columns, height / rows);

                    count2++;

                }

            }

        }

        frame.setVisible(true);
        frame.setSize(image.getWidth(), image.getHeight());
        return tiles;
    }

    public static void splitImageIntoArray(int rows, int columns, int smallWidth, int smallHeight, BufferedImage img) {
        int count = 0;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {

                imageArray[count] = img.getSubimage(x * smallWidth, y * smallHeight, smallWidth, smallHeight);

                count++;
            }

        }
    }

    public Tile[][] moveUp(Tile[][] tiles, int rows, int col) {
        boolean up = false;

        if (twoPlayersMode && !whosTurn) {
            for (int i = rows; i < 2 * rows - 1; i++) {
                for (int j = 0; j < col; j++) {
                    if (!tiles[i][j].hasImage && tiles[i + 1][j].hasImage && !up) {
                        Point tempPoint = tiles[i + 1][j].panel.getLocation();
                        tiles[i + 1][j].panel.setLocation(tiles[i][j].panel.getLocation());
                        tiles[i][j].panel.setLocation(tempPoint);

                        Tile tempTile = tiles[i + 1][j];
                        tiles[i + 1][j] = tiles[i][j];
                        tiles[i][j] = tempTile;
                        up = true;

                        if (checkWinCondition(tiles,false)) {

                            JLabel imageLabel = new JLabel(new ImageIcon(imageArray[imageArray.length - 1]));
                            tempTiles[rows - 1][columns - 1].panel.add(imageLabel);

                        }

                        break;
                    }
                }

            }
        } else {
            for (int i = 0; i < rows - 1; i++) {
                for (int j = 0; j < col; j++) {
                    if (!tiles[i][j].hasImage && tiles[i + 1][j].hasImage && !up) {
                        Point tempPoint = tiles[i + 1][j].panel.getLocation();
                        tiles[i + 1][j].panel.setLocation(tiles[i][j].panel.getLocation());
                        tiles[i][j].panel.setLocation(tempPoint);

                        Tile tempTile = tiles[i + 1][j];
                        tiles[i + 1][j] = tiles[i][j];
                        tiles[i][j] = tempTile;
                        up = true;

                        if (checkWinCondition(tiles,true   )) {

                            JLabel imageLabel = new JLabel(new ImageIcon(imageArray[imageArray.length - 1]));
                            tempTiles[rows - 1][columns - 1].panel.add(imageLabel);

                        }

                        break;
                    }
                }

            }
        }
        return tiles;
    }

    public Tile[][] moveDown(Tile[][] tiles, int rows, int col) {

        if (twoPlayersMode && !whosTurn) {
            for (int i = rows; i < 2 * rows; i++) {
                for (int j = 0; j < col; j++) {
                    if (i > 0) {
                        if (!tiles[i][j].hasImage && tiles[i - 1][j].hasImage) {
                            // Move the tile
                            Point tempPoint = tiles[i - 1][j].panel.getLocation();
                            tiles[i - 1][j].panel.setLocation(tiles[i][j].panel.getLocation());
                            tiles[i][j].panel.setLocation(tempPoint);

                            Tile tempTile = tiles[i - 1][j];
                            tiles[i - 1][j] = tiles[i][j];
                            tiles[i][j] = tempTile;

                            // Check if the game is finished
                            if (checkWinCondition(tiles,false)) {

                                JLabel imageLabel = new JLabel(new ImageIcon(imageArray[imageArray.length - 1]));
                                tempTiles[rows - 1][columns - 1].panel.add(imageLabel);

                            }

                            break;
                        }
                    }
                }

            }
        } else {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < col; j++) {
                    if (i > 0) {
                        if (!tiles[i][j].hasImage && tiles[i - 1][j].hasImage) {
                            // Move the tile
                            Point tempPoint = tiles[i - 1][j].panel.getLocation();
                            tiles[i - 1][j].panel.setLocation(tiles[i][j].panel.getLocation());
                            tiles[i][j].panel.setLocation(tempPoint);

                            Tile tempTile = tiles[i - 1][j];
                            tiles[i - 1][j] = tiles[i][j];
                            tiles[i][j] = tempTile;

                            // Check if the game is finished
                            if (checkWinCondition(tiles,true    )) {

                                JLabel imageLabel = new JLabel(new ImageIcon(imageArray[imageArray.length - 1]));
                                tempTiles[rows - 1][columns - 1].panel.add(imageLabel);

                            }

                            break;
                        }
                    }
                }

            }
        }

        return tiles;
    }

    public Tile[][] moveLeft(Tile[][] tiles, int rows, int col) {

        if (twoPlayersMode && !whosTurn) {
            for (int i = rows; i < 2 * rows; i++) {
                for (int j = 0; j < col - 1; j++) {

                    if (!tiles[i][j].hasImage && tiles[i][j + 1].hasImage) {
                        Point tempPoint = tiles[i][j + 1].panel.getLocation();
                        tiles[i][j + 1].panel.setLocation(tiles[i][j].panel.getLocation());
                        tiles[i][j].panel.setLocation(tempPoint);

                        Tile tempTile = tiles[i][j + 1];
                        tiles[i][j + 1] = tiles[i][j];
                        tiles[i][j] = tempTile;

                        if (checkWinCondition(tiles,false   )) {
                            // Fill in the blank tile at the bottom right

                            JLabel imageLabel = new JLabel(new ImageIcon(imageArray[imageArray.length - 1]));
                            tempTiles[rows - 1][columns - 1].panel.add(imageLabel);

                        }

                        break;
                    }

                }

            }
        } else {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < col - 1; j++) {

                    if (!tiles[i][j].hasImage && tiles[i][j + 1].hasImage) {
                        Point tempPoint = tiles[i][j + 1].panel.getLocation();
                        tiles[i][j + 1].panel.setLocation(tiles[i][j].panel.getLocation());
                        tiles[i][j].panel.setLocation(tempPoint);

                        Tile tempTile = tiles[i][j + 1];
                        tiles[i][j + 1] = tiles[i][j];
                        tiles[i][j] = tempTile;

                        if (checkWinCondition(tiles,true    )) {
                            // Fill in the blank tile at the bottom right

                            JLabel imageLabel = new JLabel(new ImageIcon(imageArray[imageArray.length - 1]));
                            tempTiles[rows - 1][columns - 1].panel.add(imageLabel);

                        }

                        break;
                    }

                }

            }
        }
        return tiles;
    }

    public Tile[][] moveRight(Tile[][] tiles, int rows, int col) {

        if (twoPlayersMode && !whosTurn) {
            for (int i = rows; i < 2 * rows; i++) {
                for (int j = 0; j < col - 1; j++) {

                    if (tiles[i][j].hasImage && !tiles[i][j + 1].hasImage) {
                        Point tempPoint = tiles[i][j + 1].panel.getLocation();
                        tiles[i][j + 1].panel.setLocation(tiles[i][j].panel.getLocation());
                        tiles[i][j].panel.setLocation(tempPoint);

                        Tile tempTile = tiles[i][j + 1];
                        tiles[i][j + 1] = tiles[i][j];
                        tiles[i][j] = tempTile;

                        if (checkWinCondition(tiles,false   )) {

                            JLabel imageLabel = new JLabel(new ImageIcon(imageArray[imageArray.length - 1]));
                            tempTiles[rows - 1][columns - 1].panel.add(imageLabel);

                        }

                        break;
                    }

                }

            }
        } else {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < col - 1; j++) {

                    if (tiles[i][j].hasImage && !tiles[i][j + 1].hasImage) {
                        Point tempPoint = tiles[i][j + 1].panel.getLocation();
                        tiles[i][j + 1].panel.setLocation(tiles[i][j].panel.getLocation());
                        tiles[i][j].panel.setLocation(tempPoint);

                        Tile tempTile = tiles[i][j + 1];
                        tiles[i][j + 1] = tiles[i][j];
                        tiles[i][j] = tempTile;

                        if (checkWinCondition(tiles,true    )) {

                            JLabel imageLabel = new JLabel(new ImageIcon(imageArray[imageArray.length - 1]));
                            tempTiles[rows - 1][columns - 1].panel.add(imageLabel);

                        }

                        break;
                    }

                }

            }
        }
        return tiles;
    }

    public int availableSpace(Tile[][] tiles, Tile tile) {
        int row = -1;
        int col = -1;

        // Find the position of the given tile in the 2D array
        if (twoPlayersMode && !whosTurn) {
            for (int i = rows; i < 2 * rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (tiles[i][j] == tile) {
                        row = i;
                        col = j;
                        break;
                    }
                }
            }

            if (row - 1 >= 0 && !tiles[row - 1][col].hasImage) {
                // Tile can move up
                return 1;
            } else if (row + 1 < 2 * rows && !tiles[row + 1][col].hasImage) {
                // Tile can move down
                return 2;
            } else if (col - 1 >= 0 && !tiles[row][col - 1].hasImage) {
                // Tile can move left
                return 3;
            } else if (col + 1 < columns && !tiles[row][col + 1].hasImage) {
                // Tile can move right
                return 4;
            }
        } else {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (tiles[i][j] == tile) {
                        row = i;
                        col = j;
                        break;
                    }
                }
            }

            if (row - 1 >= 0 && !tiles[row - 1][col].hasImage) {
                // Tile can move up
                return 1;
            } else if (row + 1 < rows && !tiles[row + 1][col].hasImage) {
                // Tile can move down
                return 2;
            } else if (col - 1 >= 0 && !tiles[row][col - 1].hasImage) {
                // Tile can move left
                return 3;
            } else if (col + 1 < columns && !tiles[row][col + 1].hasImage) {
                // Tile can move right
                return 4;
            }
        }

        // Check if the tile can move in different directions

        // Tile cannot move in any direction
        return 0;
    }

    public class TileMouseListener extends MouseAdapter {
        private Tile tile;

        public TileMouseListener(Tile tile) {
            this.tile = tile;
        }

    }

    public boolean checkWinCondition(Tile[][] tiles, boolean tilebool) {


        // Check if each tile is in the correct position
        if (tilebool) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (tiles[i][j] != originalOrder[i][j]) {
                        return false; // Tiles are not in the correct order
                    }

                }
            }
        } else {
            for (int i = rows; i < 2 * rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (tiles[i][j] != originalOrder2[i][j]) {
                        return false; // Tiles are not in the correct order
                    }

                }
            }

        }


        // Disable further tile movements
        tempPanel.getActionMap().clear();

        // Add the final tile
        imageLabel = new JLabel(new ImageIcon(imageArray[imageArray.length - 1]));
        tempTiles[rows - 1][columns - 1].panel.add(imageLabel);
        tempTiles[rows - 1][columns - 1].panel.revalidate();
        tempTiles[rows - 1][columns - 1].panel.repaint();

        System.out.println("Total Moves: " + moveCount);

        System.out.println("you have won");

        JLabel imageLabel = new JLabel(new ImageIcon(imageArray[imageArray.length - 1]));
        tempTiles[rows - 1][columns - 1].panel.add(imageLabel);
        //tempTiles2[rows - 1][columns - 1].panel.add(imageLabel);

        new CongratulationsScreen(rows, columns, tempTiles);

        return true; // All tiles are in the correct order

    }

    public void setPlayerNames(String playerName1, String playerName2) {
        this.playerName1 = playerName1;
        this.playerName2 = playerName2;
    }

    public void saveCountToFile(String playerName, int moveCount) {
        String projectPath = System.getProperty("user.dir"); // Get the current project directory
        String filePath = projectPath + "/resources/" + playerName + ".txt"; // File path for the player

        try {
            FileWriter writer = new FileWriter(filePath);
            String message = "Player " + playerName + " has won in " + moveCount + " moves";
            writer.write(message);
            writer.close();
            System.out.println("Move count for " + playerName + " saved to file: " + filePath);
        } catch (IOException e) {
            System.out.println("Error saving move count to file: " + e.getMessage());
        }
    }

}