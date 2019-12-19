import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.Timer;
import javax.sound.sampled.FloatControl; //used for mute/unmute button
import javax.swing.*;
import java.io.File;
import javax.sound.sampled.AudioInputStream; //used to play sounds and intro music
import javax.sound.sampled.AudioSystem; //used to play sounds and intro music
import javax.sound.sampled.Clip; //used to play sounds and intro music

/**
 *  Description: This class contains the methods that create the GUI and the grid for the Battleship game.
 *  There are MANY methods, some dictating the placement and orientation of ships and other handling the win
 *  conditions.
 *
 *  Author: Karn Bansal
 *
 *  Date: June 19, 2018
 */


public class BattleshipGUI extends JFrame implements ActionListener

{

    //Declare components of JFrame
    private JPanel mainMenuPanel, gameBoardPanel, gridPanel;
    private JButton onePlayer, twoPlayer, numWins, rotateShip, mainInstructions,
            shipInstructions, gameplayInstructions, ready, exitButton;
    private JButton[][] grid = new JButton[10][10]; //10x10 grid for gameplay
    private JRadioButton[] shipRadioButton = new JRadioButton[3];
    private ButtonGroup allShips;
    private JTextArea moveBox;
    private JLabel titlePicture, turnDisplayer, mute, unmute, settings, backgrd;
    private JScrollPane scrollPane;

    //Boolean variables to store modes/turns
    private boolean isSinglePlayer; //Stores if player clicked one or two player mode
    private boolean isPlay; //Stores whether or not both players have placed their ship
    private boolean isPlayerOneTurn; //Stores whose turn it is (either to place or to hit)

    //Non-array ImageIcons
    private final ImageIcon GOODHIT;
    private final ImageIcon MISS;
    private final ImageIcon WATER;
    private final ImageIcon HEADER;

    //Final ImageIcon objects store images that are going to be needed
    //1st array dimension for each orientation (0=west, 1=north, 2=east, 3=south)
    //2nd dimension for each piece of the ship
    private final ImageIcon[][] CRUISER = new ImageIcon[4][2];
    private final ImageIcon[][] DESTROYER = new ImageIcon[4][3];
    private final ImageIcon[][] BATTLESHIP = new ImageIcon[4][4];
    private final ImageIcon MUTE;
    private final ImageIcon UNMUTE;

    //Instance variables associated with sounds
    private static Clip clip, clip2;
    private boolean flag2 = true;

    //Constant String objects store instructions
    private final String MMINSTRUCTIONS, SHIPINSTRUCTIONS, GAMEINSTRUCTIONS;

    //Stores which colours/fonts are used in this program
    private final Color[] colours;
    private final Font[] fonts;

    //Declare 2 players
    private Player playerOne, playerTwo;
    //Declare a rand object for single player
    private Random rand;

    //Stores which ship was last placed (used for rotate last ship)
    private Player lastPlayer = null;
    private Ship lastShipPlaced = null;
    private int lastRow = -1;
    private int lastCol = -1;

    //main method calls BattleshipGUI constructor to set up game GUI
    public static void main(String[] args)

    {

        new BattleshipGUI();

    }

    //BattleshipGUI constructor method sets up game GUI
    public BattleshipGUI()

    {



        //Set up JFrame
        super( "Battleship - Karn Bansal"); //Title

        try

        {

            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

        }

        catch (Exception e)

        {

            e.printStackTrace();

        }

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Closes when X clicked
        setSize(535, 720); //Size of JFrame
        setLocationRelativeTo(null); //Centers JFrame on screen
        setResizable(false);

        //Initialize non-GUI variables
        isSinglePlayer = false; //Default is two-player mode
        isPlay = false; //ensures that menu and ship placing occurs prior to playing game
        isPlayerOneTurn = true; //ensures that Player 1 goes first every game


        //setting ImageIcons to ships
        for (int i = 0; i < CRUISER[0].length; i++) //Left 1x2
            CRUISER[0][i] = new ImageIcon("Images/red.JPG");
        for (int i = 0; i < CRUISER[1].length; i++) //Up 1x2
            CRUISER[1][i] = new ImageIcon("Images/red.JPG");
        for (int i = 0; i < CRUISER[2].length; i++) //Right 1x2
            CRUISER[2][i] = new ImageIcon("Images/red.JPG");
        for (int i = 0; i < CRUISER[3].length; i++) //Down 1x2
            CRUISER[3][i] = new ImageIcon("Images/red.JPG");

        for (int i = 0; i < DESTROYER[0].length; i++) //Left 1x3
            DESTROYER[0][i] = new ImageIcon("Images/red.JPG");
        for (int i = 0; i < DESTROYER[1].length; i++) //Up 1x3
            DESTROYER[1][i] = new ImageIcon("Images/red.JPG");
        for (int i = 0; i < DESTROYER[2].length; i++) //Right 1x3
            DESTROYER[2][i] = new ImageIcon("Images/red.JPG");
        for (int i = 0; i < DESTROYER[3].length; i++) //Down 1x3
            DESTROYER[3][i] = new ImageIcon("Images/red.JPG");

        for (int i = 0; i < BATTLESHIP[0].length; i++) //Left 1x4
            BATTLESHIP[0][i] = new ImageIcon("Images/red.JPG");
        for (int i = 0; i < BATTLESHIP[1].length; i++) //Up 1x4
            BATTLESHIP[1][i] = new ImageIcon("Images/red.JPG");
        for (int i = 0; i < BATTLESHIP[2].length; i++) //Right 1x4
            BATTLESHIP[2][i] = new ImageIcon("Images/red.JPG");
        for (int i = 0; i < BATTLESHIP[3].length; i++) //Down 1x4
            BATTLESHIP[3][i] = new ImageIcon("Images/red.JPG");


        //intializing ImageIcons for non-ship actions (i.e. missing ship, hitting ship, water for grid and header)
        GOODHIT = new ImageIcon("Images/x.png");
        MISS = new ImageIcon("Images/whitecircle.png");
        WATER = new ImageIcon("Images/watercropped.jpeg");
        HEADER = new ImageIcon("Images/Karn's Battle Boats.jpg");

        //setting final Strings to text for instructions
        MMINSTRUCTIONS =
                "Select single-player or two-player modes to get started. To check stats (# of wins) "
                        + "of any player, click on the \"Number of Wins\" button, and enter the player's username." +
                        "\nTo mute the title song, click on the mute button located on the bottom right." +
                        "\nTo check the developer information, click on the settings wheel located on the " +
                        "bottom left.";
        SHIPINSTRUCTIONS =
                "You must now place your ships! To do so, click on any of the squares on the grid to place a ship. "
                        + "\nYou must place a total of 6 ships (2 of each type); you can change the type of ship "
                        + "you are placing by changing the selected button on the bottom left corner. \n\nHowever, "
                        + "you cannot move your ship once you have placed it, but you can rotate it " +
                        "\"Rotate Last Ship\" button. Click the \"Ready\" button when you are done with placing your ships.";
        GAMEINSTRUCTIONS =
                "Try to guess the location of your opponent's ship by clicking on a square on "
                        + "the grid. If you get a hit, you get to try again! If you miss, it is your opponent's turn to "
                        + "try to find your ships. \"Hit\" spaces will be shown as red X's, while "
                        + "\"Missed\" spaces will be shown as a white circle with" +
                        "a black background. \nWhen all 6 ships of any player are sunk, "
                        + "the game will end and your game will be recorded in your statistics."
                        + "\n\n Note: Look at the box on the bottom; it will tell you if have completely" +
                        "sunk a full ship. ";

        //Colours/fonts used throughout this program
        colours = new Color[]

        {

            new Color(152, 252, 152), //Background (Pale green)
            new Color(0, 153, 255), //Foreground (Blue)
            new Color(255, 69, 0) //Foreground (Orange-red)

        };

        fonts = new Font[]

        {

            new Font("Calibri Light", Font.PLAIN, 16),
            new Font("Calibri Heading", Font.ITALIC, 20),
            new Font("Cambria", Font.PLAIN, 20),
            new Font("Arial", Font.PLAIN, 32),

        };


        /*  Set up mainMenuPanel and components on it  */
        mainMenuPanel = new JPanel();
        mainMenuPanel.setLayout(null); //Don't use layout for this panel so that
        //child components can use .setBounds()
        mainMenuPanel.setBackground(colours[0]);

        //One-player button
        onePlayer = new JButton("Single Player");
        onePlayer.addActionListener(this);
        onePlayer.setBounds(90, 400, 170, 75);
        onePlayer.setBackground(colours[2]);
        onePlayer.setFont(fonts[2]);

        //Two-player button
        twoPlayer = new JButton("Two Player");
        twoPlayer.addActionListener(this);
        twoPlayer.setBounds(270, 400, 170, 75);
        twoPlayer.setBackground(colours[2]);
        twoPlayer.setFont(fonts[2]);

        //Instructions button
        mainInstructions = new JButton("Instructions");
        mainInstructions.addActionListener(this);
        mainInstructions.setBounds(180, 500, 170, 50);
        mainInstructions.setBackground(colours[2]);
        mainInstructions.setFont(fonts[0]);

        //Check stats button
        numWins = new JButton("# Wins");
        numWins.addActionListener(this);
        numWins.setBounds(180, 560, 170, 50);
        numWins.setBackground(colours[2]);
        numWins.setFont(fonts[0]);

        //Menu Picture (under title)
        titlePicture = new JLabel(HEADER);
        titlePicture.setBounds(30, 80, 470, 250);

        //background picture (main menu)
        backgrd = new JLabel();
        backgrd.setBounds(-200, -225, 1024, 1200);
        backgrd.setIcon(new ImageIcon("Images/battleshipbg.jpg"));

        //settings wheel label (opens developer information)
        settings = new JLabel();
        settings.setBounds(20, 595, 100, 100);
        settings.setIcon(new ImageIcon("Images/settings.png"));
        settings.addMouseListener(new MouseListener()

        {

            //methods below relate to the MouseListener on settings
            @Override
            public void mouseClicked(MouseEvent e) //if settings wheel is clicked, then the following text is shown

            {

                JOptionPane.showMessageDialog(mainMenuPanel, "This program was created by Karn Bansal." +
                        "\nDate: June 19, 2018." +
                        "\nVersion: 1.1");

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }

        });

        //adding mute and unmute labels to control intro music
        MUTE = new ImageIcon("Images/Mute Button.png");
        UNMUTE = new ImageIcon("Images/Unmute Button.png");

        mute = new JLabel();
        unmute = new JLabel();

        mute.setIcon(MUTE);
        unmute.setIcon(UNMUTE);

        mute.setBounds(435, 595, 100, 100);
        unmute.setBounds(435, 595, 100, 100);

        mute.setVisible(false);
        unmute.setVisible(true);


        mute.addMouseListener(new MouseListener()

        {

            @Override
            //when volume button clicked, unmute
            public void mouseClicked(MouseEvent e)

            {

                mute.setVisible(false);
                unmute.setVisible(true);

                setVolume1(1);
                setVolume2(1);

                mainMenuPanel.repaint();
                mainMenuPanel.revalidate();

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        unmute.addMouseListener(new MouseListener()

        {

            @Override
            //when volume button clicked, mute
            public void mouseClicked(MouseEvent e)

            {

                mute.setVisible(true);
                unmute.setVisible(false);

                setVolume1(0);
                setVolume2(0);

                mainMenuPanel.repaint();
                mainMenuPanel.revalidate();

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        //adding various components to panel
        mainMenuPanel.add(onePlayer);
        mainMenuPanel.add(twoPlayer);
        mainMenuPanel.add(mainInstructions);
        mainMenuPanel.add(numWins);
        mainMenuPanel.add(titlePicture);
        mainMenuPanel.add(settings);
        mainMenuPanel.add(unmute);
        mainMenuPanel.add(mute);
        mainMenuPanel.add(backgrd);


        //gridPanel will contain the main 10 x 10 grid for the game
        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(10, 10, 1, 1));
        gridPanel.setBounds(15, 60, 500, 500);

        //Buttons on grid (only part of gridPanel)
        for (int i = 0; i < grid.length; i++)

        {

            for (int j = 0; j < grid[i].length; j++)

            {

                grid[i][j] = new JButton();
                grid[i][j].addActionListener(this);
                grid[i][j].setActionCommand("pressed"); //all have same actionCommand
                gridPanel.add(grid[i][j]);

            }

        }

        //gameBoardPanel will hold the gridPanel and moveBox
        gameBoardPanel = new JPanel();
        gameBoardPanel.setLayout(null); //null layout so that the moveBox and gridPanel can easily fit
        gameBoardPanel.setBackground(colours[0]);

        //Turn displayer (will display who's turn it is when playing the game)
        turnDisplayer = new JLabel();
        turnDisplayer.setBounds(50, 10, 415, 50);
        turnDisplayer.setFont(fonts[1]);

        //Radio button group to choose which type of ship to place
        allShips = new ButtonGroup();
        for (int i = 0; i < shipRadioButton.length; i++)

        {

            shipRadioButton[i] = new JRadioButton();
            shipRadioButton[i].setBounds(20, 570 + (i*20), 250, 20);
            shipRadioButton[i].setFont(fonts[0]);
            shipRadioButton[i].setBackground(colours[0]);
            allShips.add(shipRadioButton[i]);

        }

        shipRadioButton[0].setSelected(true); //first option is default selection
        shipRadioButton[0].setText("1x2 Destroyer (2 Total)");
        shipRadioButton[1].setText("1x3 Cruiser: (2 Total)");
        shipRadioButton[2].setText("1x4 Battleship: (2 Total)");

        //moveBox indicates which moves have been made to the user during play
        moveBox = new JTextArea();
        moveBox.setBounds(20, 570, 400, 100);
        moveBox.setEditable(false);
        moveBox.setLineWrap(true);
        //ScrollPane allows for automatic scrolling when moveBox is filled
        scrollPane = new JScrollPane(moveBox, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBounds(20, 570, 400, 100);

        //Instructions button for placing ships
        shipInstructions = new JButton();
        shipInstructions.setBounds(320, 587, 40, 40);
        shipInstructions.addActionListener(this);
        shipInstructions.setBackground(colours[2]);
        shipInstructions.setFont(fonts[2]);
        shipInstructions.setText("?");
        shipInstructions.setBorder(null);

        //Instructions button for gameplay
        gameplayInstructions = new JButton();
        gameplayInstructions.setBounds(465, 600, 40, 40);
        gameplayInstructions.addActionListener(this);
        gameplayInstructions.setBackground(colours[2]);
        gameplayInstructions.setFont(fonts[2]);
        gameplayInstructions.setText("?");
        gameplayInstructions.setBorder(null);

        //Rotate ship button
        rotateShip = new JButton();
        rotateShip.setBounds(190, 640, 160, 40);
        rotateShip.addActionListener(this);
        rotateShip.setBackground(colours[2]);
        rotateShip.setFont(fonts[0]);
        rotateShip.setText("Rotate Last Ship");
        rotateShip.setBorder(null);

        //Ready button
        ready = new JButton();
        ready.setBounds(375, 572, 137, 65);
        ready.addActionListener(this);
        ready.setBackground(colours[2]);
        ready.setFont(fonts[3]);
        ready.setText("Ready");

        //Exit button
        exitButton = new JButton();
        exitButton.setBounds(455, 10, 75, 30);
        exitButton.addActionListener(this);
        exitButton.setBackground(colours[2]);
        exitButton.setFont(fonts[1]);
        exitButton.setText("Exit");

        //Add components to gameBoardPanel
        gameBoardPanel.add(gridPanel);
        gameBoardPanel.add(turnDisplayer);

        //for loop is used to add each radio button
        for (int i = 0; i < shipRadioButton.length; i++)
            gameBoardPanel.add(shipRadioButton[i]);

        gameBoardPanel.add(shipInstructions);
        gameBoardPanel.add(rotateShip);
        gameBoardPanel.add(ready);
        gameBoardPanel.add(exitButton);


        this.add(mainMenuPanel);
        this.setVisible(true);
        this.setBackground(colours[2]);
        mainMenuPanel.setVisible(true); //Only panel that's visible at start
        //playSound2 ensures that the intro music plays open opening the application
        playSound2(flag2);
        //message displays as soon as the game is opened
        JOptionPane.showMessageDialog(this, "You are now playing BATTLESHIP! " +
                "\nTurn up the volume to listen to some intense music!" +
                "\nNote: you can mute the song using the button on the bottom right of the screen.");
        //gameBoard is hidden at first (so that only the main menu panel is shown)
        gameBoardPanel.setVisible(false);

   }

    //actionPerformed method takes care of all button pressing actions
    @Override
    public void actionPerformed(ActionEvent e)

    {

        //Displays main instructions when clicked
        if (e.getSource() == mainInstructions)

        {

            JOptionPane.showMessageDialog(
                    this, "<html><p style='width: 300px;'>" + MMINSTRUCTIONS);

        }

        //Displays instructions for placing ships when clicked
        else if (e.getSource() == shipInstructions)

        {

            JOptionPane.showMessageDialog(
                    this, "<html><p style='width: 300px;'>" + SHIPINSTRUCTIONS);

        }

        //Displays instructions for playing game when clicked
        else if (e.getSource() == gameplayInstructions)

        {

            JOptionPane.showMessageDialog(
                    this, "<html><p style='width: 300px;'>" + GAMEINSTRUCTIONS);

        }

        //Shows the user number of stats when clicked
        else if (e.getSource() == numWins)

        {

            String name = JOptionPane.showInputDialog(this, "Please enter a username: ");

            //if user does not enter a name into the option box
            if (name == null || name == "")

            {

                JOptionPane.showMessageDialog(this, "No name entered...");

            }

            else //if a name IS entered

                {

                    try

                    {

                        name = name.toUpperCase();
                        //retrieve score based upon username
                        int[] temp = IO.getScore(name);
                        int wins = temp[0];
                        int totalGames = temp[1];
                        JOptionPane.showMessageDialog(this, name + " has " + wins + " out of " + totalGames + " wins.");

                    }

                catch (IOException ex)

                {

                    System.out.println("Error while acquiring stats...");

                }

            }

        }

        //If single-player button clicked, start game
        else if (e.getSource() == onePlayer)

        {

            isSinglePlayer = true; //isSinglePlayer is set to true because single player is clicked
            stopSound2(); //intro music is stopped
            startPerform();

        }

        //If two-player button clicked, start game
        else if (e.getSource() == twoPlayer)

        {

            isSinglePlayer = false; //isSinglePlayer is set to false because two player is clicked
            flag2 = false;
            stopSound2(); //intro music is stopped
            startPerform();

        }

        //If ready button clicked (after placing ships) then the readyPerform method is called
        else if (e.getSource() == ready)

        {

            readyPerform();

        }

        //else if statement deals with if rotate ship is clicked
        else if (e.getSource() == rotateShip)

        {

            //If current turn matches lastPlayer
            Player playerTurn = isPlayerOneTurn ? playerOne : playerTwo;
            if (lastPlayer != null && playerTurn.getName().equals(lastPlayer.getName()))

            {

                //ship will rotate clockwise around point of placement until exception is thrown or
                //it reaches its original orientation
                if (lastShipPlaced.getOrientation() == 'l')

                {

                    //reset ship orientation
                    for (int i = 0; i < lastShipPlaced.getLength(); i++)
                        playerTurn.setShips(lastRow, lastCol - i, false);

                    //try to rotate ship (until ArrayOutOfBoundsException is not met)
                    try

                    {

                        placeShipUp(lastPlayer, lastShipPlaced, lastRow, lastCol);

                    }

                    catch (ArrayIndexOutOfBoundsException e1)

                    {

                        try

                        {

                            placeShipRight(lastPlayer, lastShipPlaced, lastRow, lastCol);

                        }

                        catch (ArrayIndexOutOfBoundsException f)

                        {

                            try

                            {

                                placeShipDown(lastPlayer, lastShipPlaced, lastRow, lastCol);

                            }

                            catch (ArrayIndexOutOfBoundsException g)

                            {

                                placeShipLeft(lastPlayer, lastShipPlaced, lastRow, lastCol);
                                JOptionPane.showMessageDialog(this, "This ship cannot be rotated.");

                            }

                        }

                    } //end catch

                } //end if orientation is west

                else if (lastShipPlaced.getOrientation() == 'u')

                {

                    for (int i = 0; i < lastShipPlaced.getLength(); i++)
                        playerTurn.setShips(lastRow - i, lastCol, false);

                    try

                    {

                        placeShipRight(lastPlayer, lastShipPlaced, lastRow, lastCol);

                    }

                    catch (ArrayIndexOutOfBoundsException e1)

                    {

                        try

                        {

                            placeShipDown(lastPlayer, lastShipPlaced, lastRow, lastCol);

                        }

                        catch (ArrayIndexOutOfBoundsException f)

                        {

                            try

                            {

                                placeShipLeft(lastPlayer, lastShipPlaced, lastRow, lastCol);

                            }

                            catch (ArrayIndexOutOfBoundsException g)

                            {

                                placeShipUp(lastPlayer, lastShipPlaced, lastRow, lastCol);
                                JOptionPane.showMessageDialog(this, "This ship cannot be rotated.");

                            }

                        }

                    } //end catch

                } //end if orientation is north

                else if (lastShipPlaced.getOrientation() == 'r')

                {

                    for (int i = 0; i < lastShipPlaced.getLength(); i++)
                        playerTurn.setShips(lastRow, lastCol + i, false);

                    try

                    {

                        placeShipDown(lastPlayer, lastShipPlaced, lastRow, lastCol);

                    }
                    catch (ArrayIndexOutOfBoundsException e1)

                    {
                        try

                        {

                            placeShipLeft(lastPlayer, lastShipPlaced, lastRow, lastCol);

                        }

                        catch (ArrayIndexOutOfBoundsException f)

                        {

                            try

                            {

                                placeShipUp(lastPlayer, lastShipPlaced, lastRow, lastCol);

                            }

                            catch (ArrayIndexOutOfBoundsException g)

                            {

                                placeShipRight(lastPlayer, lastShipPlaced, lastRow, lastCol);
                                JOptionPane.showMessageDialog(this, "This ship cannot be rotated.");

                            }

                        }

                    } //end catch

                } //end if orientation is east

                else if (lastShipPlaced.getOrientation() == 'd')

                {

                    for (int i = 0; i < lastShipPlaced.getLength(); i++)
                        playerTurn.setShips(lastRow + i, lastCol, false);

                    try

                    {

                        placeShipLeft(lastPlayer, lastShipPlaced, lastRow, lastCol);

                    }

                    catch (ArrayIndexOutOfBoundsException e1)

                    {

                        try

                        {

                            placeShipUp(lastPlayer, lastShipPlaced, lastRow, lastCol);

                        }

                        catch (ArrayIndexOutOfBoundsException e2)

                        {

                            try

                            {

                                placeShipRight(lastPlayer, lastShipPlaced, lastRow, lastCol);

                            }

                            catch (ArrayIndexOutOfBoundsException e3)

                            {

                                placeShipDown(lastPlayer, lastShipPlaced, lastRow, lastCol);
                                JOptionPane.showMessageDialog(this, "This ship cannot be rotated.");

                            }

                        }

                    } //end catch

                } //end if orientation is south

            }

            else //Not the same turn as lastPlayer

            {

                JOptionPane.showMessageDialog(this, "There is nothing to rotate...");

            }

            changeGrid(); //change GUI after ship is rotated

        }

        //program will close if player presses exit button
        else if (e.getSource()== exitButton)

        {

            JOptionPane.showMessageDialog(this, "Thanks for playing!");
            System.exit(0);

        }

        //If grid is clicked
        else if (e.getActionCommand().equals("pressed"))

        {

            //Find the location of the button clicked
            int row = 0;
            int col = 0;
            findButtonLocation:
            for (int i = 0; i < grid.length; i++)

            {

                for (int j = 0; j < grid[i].length; j++)

                    if (e.getSource().equals(grid[i][j]))

                    {

                        row = i;
                        col = j;
                        break findButtonLocation; //Breaks out of both loops when location found

                    }

            }

            //If a player is PLACING a ship, redirect to placeShipPerform method
            if (!isPlay && ((JButton)e.getSource()).getIcon().equals(WATER))

            {

                placeShipPerform(row, col);

            }

            //If a player is HITTING a ship, redirect to hitSpacePerform method
            else if (isPlay && ((JButton)e.getSource()).getIcon().equals(WATER))

            {

                hitSpacePerform(row, col);
                playSound1();

            }

            changeGrid(); //change GUI after a button is clicked

        }

    }

    //method takes care of placing ships, using the row and col #
    private void placeShipPerform(int row, int col)

    {

        Player playerTurn = isPlayerOneTurn? playerOne:playerTwo; //Copy the attributes of current player
        Ship shipBeingPlaced = null; //Copy the attributes of ship being placed

        //Find the ship being placed
        if (shipRadioButton[0].isSelected())//cruiser

        {

            if (!playerTurn.destroyer1.getIsPlaced())
                shipBeingPlaced = playerTurn.destroyer1;

            else if (!playerTurn.destroyer2.getIsPlaced())
                shipBeingPlaced = playerTurn.destroyer2;

            else

            {

                JOptionPane.showMessageDialog(this, "There are no more destroyers to place.");
                return; //No ship to place, so exit this method

            }

        }

        else if (shipRadioButton[1].isSelected()) //destroyer

        {

            if (!playerTurn.cruiser1.getIsPlaced())
                shipBeingPlaced = playerTurn.cruiser1;

            else if (!playerTurn.cruiser2.getIsPlaced())
                shipBeingPlaced = playerTurn.cruiser2;

            else

            {

                JOptionPane.showMessageDialog(this, "There are no more cruisers to place.");
                return; //No ship to place, so exit this method

            }

        }

        else if (shipRadioButton[2].isSelected()) //battleship

        {

            if (!playerTurn.battleship1.getIsPlaced())
                shipBeingPlaced = playerTurn.battleship1;

            else if (!playerTurn.battleship2.getIsPlaced())
                shipBeingPlaced = playerTurn.battleship2;

            else

            {

                JOptionPane.showMessageDialog(this, "There are no more battleships to place.");
                return; //No ship to place, so exit this method

            }

        }

        //place ship (three nested try catches used to accomplish this)
        try

        {

            //If placing ship with left orientation doesn't work, rotate up
            placeShipLeft(playerTurn, shipBeingPlaced, row, col);

        }

        catch (ArrayIndexOutOfBoundsException e1)

        {

            try

            {

                //If placing up doesn't work, rotate right
                placeShipUp(playerTurn, shipBeingPlaced, row, col);

            }

            catch (ArrayIndexOutOfBoundsException e2)

            {

                try

                {

                    //If placing right doesn't work, rotate down
                    placeShipRight(playerTurn, shipBeingPlaced, row, col);

                }

                catch (ArrayIndexOutOfBoundsException e3)

                {

                    try

                    {

                        //If placing down doesn't work, it is an invalid move
                        placeShipDown(playerTurn, shipBeingPlaced, row, col);

                    }

                    catch (ArrayIndexOutOfBoundsException e4)

                    {

                        //If computer made invalid move, click again
                        if (isSinglePlayer && !isPlayerOneTurn)
                            placeShipPerform(rand.nextInt(10), rand.nextInt(10));
                        else //If player made invalid move, inform player
                            JOptionPane.showMessageDialog(this, "Cannot place ship like this. Please try again.");

                    }

                }

            }

        } //end catch e1

    } //end placeShipPerform()

    //method takes care of placing ship facing west (left) using row and col (int) as parameters
    private void placeShipLeft(Player player, Ship ship, int row, int col) throws ArrayIndexOutOfBoundsException

    {

        int[][] shipLocation = new int[ship.getLength()][2];
        for (int i = 0; i < ship.getLength(); i++)

        {

            if (player.getShips(row, col - i)) //If there is already a ship at where one is being set
                throw new ArrayIndexOutOfBoundsException(); //Throw an exception to rotate it
            shipLocation[i][0] = row;
            shipLocation[i][1] = col - i; //Set row and col for 1 block of the ship

        }

        for (int i = 0; i < ship.getLength(); i++) //If the ship was successfully set, update player grid
            player.setShips(row, col - i, true);

        //Update "last" variables
        lastPlayer = player;
        lastShipPlaced = ship;
        lastRow = row;
        lastCol = col;

        //Update ship attributes
        ship.setLocation(shipLocation);
        ship.setOrientation('l');
        ship.setIsPlaced(true);

    }

    //method takes care of placing ship facing up (north) with row and col (int) as parameters
    private void placeShipUp(Player player, Ship ship, int row, int col)

    {

        int[][] shipLocation = new int[ship.getLength()][2];
        for (int i = 0; i < ship.getLength(); i++)

        {

            if (player.getShips(row - i, col)) //If there is already a ship at where one is being set
                throw new ArrayIndexOutOfBoundsException(); //Throw an exception to rotate it
            shipLocation[i][0] = row - i;
            shipLocation[i][1] = col;

        }

        for (int i = 0; i < ship.getLength(); i++) //If the ship was successfully set, update player grid
            player.setShips(row - i, col, true);

        //Update "last" variables
        lastPlayer = player;
        lastShipPlaced = ship;
        lastRow = row;
        lastCol = col;

        //Update ship attributes
        ship.setLocation(shipLocation);
        ship.setOrientation('u');
        ship.setIsPlaced(true);

    }

    //method takes care of placing ship facing east (right) with row and col (int) as parameters
    private void placeShipRight(Player player, Ship ship, int row, int col)

    {

        int[][] shipLocation = new int[ship.getLength()][2];
        for (int i = 0; i < ship.getLength(); i++){
            if (player.getShips(row, col + i)) //If there is already a ship at where one is being set
                throw new ArrayIndexOutOfBoundsException(); //Throw an exception to rotate it
            shipLocation[i][0] = row;
            shipLocation[i][1] = col + i;

        }

        for (int i = 0; i < ship.getLength(); i++) //If the ship was successfully set, update player grid
            player.setShips(row, col + i, true);

        //Update "last" variables
        lastPlayer = player;
        lastShipPlaced = ship;
        lastRow = row;
        lastCol = col;

        //Update ship attributes
        ship.setLocation(shipLocation);
        ship.setOrientation('r');
        ship.setIsPlaced(true);

    }

    //method takes care of placing ship facing down (south) with row and col (int) as parameters
    private void placeShipDown(Player player, Ship ship, int row, int col)

    {

        int[][] shipLocation = new int[ship.getLength()][2];
        for (int i = 0; i < ship.getLength(); i++)

        {

            if (player.getShips(row + i, col)) //If there is already a ship at where one is being set
                throw new ArrayIndexOutOfBoundsException(); //Throw an exception to rotate it
            shipLocation[i][0] = row + i;
            shipLocation[i][1] = col;

        }

        for (int i = 0; i < ship.getLength(); i++) //If the ship was successfully set, update player grid
            player.setShips(row + i, col, true);

        //Update "last" variables
        lastPlayer = player;
        lastShipPlaced = ship;
        lastRow = row;
        lastCol = col;

        //Update ship attributes
        ship.setLocation(shipLocation);
        ship.setOrientation('d');
        ship.setIsPlaced(true);

    }

    //method takes care of hitting an empty space with row and col (int) as parameters
    private void hitSpacePerform(int row, int col)

    {

        //copy attributes of the Player whose turn it is, and is not
        Player playerTurn = isPlayerOneTurn ? playerOne : playerTwo;
        Player notPlayerTurn = isPlayerOneTurn ? playerTwo : playerOne;

        if (isPlayerOneTurn) //Update appropriate player's hits
            playerOne.setHits(row, col, true);
        else
            playerTwo.setHits(row, col, true);

        //If player hit an opponent ship
        if (notPlayerTurn.getShips(row, col))

        {

            //Player can try again
            moveBox.append("\nHit! " + playerTurn.getName() + " gets to try again!"
                    + " (Row: " + row + " Column: " + col + ")"); //Tell player they can reclick

            //If all pieces of the ship is hit
            if (fullShipIsHit(row, col))

            {

                notPlayerTurn = isPlayerOneTurn ? playerTwo : playerOne;
                //Check for a win (if all opponents ships are hit)
                if (notPlayerTurn.battleship1.getIsHit() && notPlayerTurn.battleship2.getIsHit() &&
                        notPlayerTurn.cruiser1.getIsHit() && notPlayerTurn.cruiser2.getIsHit() &&
                        notPlayerTurn.destroyer1.getIsHit() && notPlayerTurn.destroyer2.getIsHit())

                {

                    //Call playerWins() with the player's turn's name
                    playerWins(isPlayerOneTurn ? playerOne.getName() : playerTwo.getName());

                }

            }

            //computer will click with a delay if it is its turn
            if (isSinglePlayer && !isPlayerOneTurn)

            {

                //Disables every grid button to avoid clicking before GUI changes turns
                for (int i = 0; i < grid.length; i++)

                {

                    for (int j = 0; j < grid[i].length; j++)
                        grid[i][j].setActionCommand("Disabled");

                }

                Timer timer = new Timer();
                timer.schedule(new TimerTask()  //After 1.65s, enable all buttons and let computer click

                {
                    public void run()

                    {

                        for (int i = 0; i < grid.length; i++)

                        {

                            for (int j = 0; j < grid[i].length; j++)

                            {

                                grid[i][j].setActionCommand("pressed");

                            }

                        }

                        computerHit(); //Computer misses

                    }

                }, 1500);

            }

        }

        //If miss
        else

            {

            //Turn changes
            moveBox.append("\nMiss! It is " + notPlayerTurn.getName() + "'s turn now."
                    + " (Row: " + row + " Column: " + col + ")");

            //Disables every grid button to avoid clicking before GUI changes turns
            for (int i = 0; i < grid.length; i++)

            {

                for (int j = 0; j < grid[i].length; j++)
                    grid[i][j].setActionCommand("Disabled");

            }

            //Create a task - change turns, updateGUI, and re-enables grid button
            TimerTask waitAfterHitShip = new TimerTask()

            {

                @Override
                public void run()

                {

                    //Change turnDisplayer
                    String otherPlayerName = isPlayerOneTurn? playerTwo.getName() : playerOne.getName();
                    turnDisplayer.setText(otherPlayerName + "'s turn...");
                    isPlayerOneTurn = !isPlayerOneTurn; //Change turns
                    changeGrid(); //Change GUI to match turn

                    for (int i = 0; i < grid.length; i++)

                    {

                        for (int j = 0; j < grid[i].length; j++) //Re-enable grid buttons

                        {

                            grid[i][j].setActionCommand("pressed");

                        }

                    }

                    //If single player mode, get computer to click again
                    if (isSinglePlayer && !isPlayerOneTurn)

                    {

                        computerHit();

                    }

                }

            };

            //Perform the above task after waiting 1.65 seconds
            //(so the player can see that they've missed their hit)
            Timer timer = new Timer();
            timer.schedule(waitAfterHitShip, 1650);

            }

    }

    //method takes in the name of the winning Player and updates the text file
    private void playerWins(String winnerName)

    {

        //Find name of losing player
        String notWinnerName;
        if (winnerName.equals(playerOne.getName()))
            notWinnerName = playerTwo.getName();
        else
            notWinnerName = playerOne.getName();

        //Inform user that a player won
        JOptionPane.showMessageDialog(this, winnerName + " just won! Better luck next time " + notWinnerName + "...");
        JOptionPane.showMessageDialog(this, "Game Over!");

        //Store stats with IO class
        try

        {

            IO.editScore(winnerName, true);
            IO.editScore(notWinnerName, false);

        }

        catch (IOException e)

        {
            System.out.println("IO exception");

        }

        System.exit(0);

    }


    //method checks if the full ship is hit by the player using int row and col as parameters
    private boolean fullShipIsHit(int row, int col)

    {

        Player playerTurn = isPlayerOneTurn? playerOne:playerTwo;
        Player notPlayerTurn = isPlayerOneTurn? playerTwo:playerOne;
        Ship shipHit = null;

        int[][] b1 = notPlayerTurn.battleship1.getLocation();
        int[][] b2 = notPlayerTurn.battleship2.getLocation();
        int[][] c1 = notPlayerTurn.cruiser1.getLocation();
        int[][] c2 = notPlayerTurn.cruiser2.getLocation();
        int[][] d1 = notPlayerTurn.destroyer1.getLocation();
        int[][] d2 = notPlayerTurn.destroyer2.getLocation();

        //Find which ship is occupying specified row & column
        for (int i = 0; i < b1.length; i++)

        {

            if (b1[i][0] == row && b1[i][1] == col) //If any of the locations of b1 match row&col

            {

                shipHit = notPlayerTurn.battleship1;
                break;

            }

            else if (b2[i][0] == row && b2[i][1] == col) //If any of the locations of b2 match row&col

            {
                shipHit = notPlayerTurn.battleship2;
                break;

            }

        }

        if (shipHit == null) //If not destroyers, check cruisers

        {

            for (int i = 0; i < c1.length; i++)

            {

                if (c1[i][0] == row && c1[i][1] == col) //If any of the locations of c1 match row&col

                {

                    shipHit = notPlayerTurn.cruiser1;
                    break;

                }

                else if (c2[i][0] == row && c2[i][1] == col) //If any of the locations of c2 match row&col

                {

                    shipHit = notPlayerTurn.cruiser2;
                    break;

                }

            }

        }

        if (shipHit == null) //If still null, check battleships

        {

            for (int i = 0; i < d1.length; i++)

            {

                if (d1[i][0] == row && d1[i][1] == col) //If any of the locations of d1 match row&col

                {

                    shipHit = notPlayerTurn.destroyer1;
                    break;

                }

                else if (d2[i][0] == row && d2[i][1] == col) //If any of the locations of d2 match row&col

                {

                    shipHit = notPlayerTurn.destroyer2;
                    break;

                }

            }

        }

        //Count the number of pieces hit on the clicked ship
        int piecesHit = 0;
        for (int i = 0; i < shipHit.getLocation().length; i++)

        {

            int[] temp = shipHit.getLocation()[i];
            if (grid[temp[0]][temp[1]].getIcon() != WATER)

            {

                piecesHit++;

            }

            //Icon of the button clicked hasn't been changed yet, so add 1 to piecesHit if it matches row&col
            else if (temp[0] == row && temp[1] == col)

            {

                piecesHit++;

            }

        }

        //If full length of ship is hit
        if (piecesHit == shipHit.getLength())

        {
            //display: Player1 just sunk Player2's battleship! (substring used to cut off the "1" or "2" after ship name)
            moveBox.append("\n" + playerTurn.getName() + " just sunk " + notPlayerTurn.getName() + "'s "
                    + shipHit.getName().toLowerCase().substring(0, shipHit.getName().length() - 1) + "!");

            //Update isHit
            switch (shipHit.getName())

            {

                case "Destroyer 1":
                    notPlayerTurn.destroyer1.setIsHit(true);
                    break;
                case "Destroyer 2":
                    notPlayerTurn.destroyer2.setIsHit(true);
                    break;
                case "Cruiser 1":
                    notPlayerTurn.cruiser1.setIsHit(true);
                    break;
                case "Cruiser 2":
                    notPlayerTurn.cruiser2.setIsHit(true);
                    break;
                case "Battleship 1":
                    notPlayerTurn.battleship1.setIsHit(true);
                    break;
                case "Battleship 2":
                    notPlayerTurn.battleship2.setIsHit(true);
                    break;

            }

            //Copy attributes to player
            if (isPlayerOneTurn)
                playerTwo = notPlayerTurn;
            else
                playerOne = notPlayerTurn;

            return true;

        }

        else  //Only one piece is hit, not whole ship

            {

                return false;

            }

    }

    //ensures that computer clicks on valid grid
    private void computerHit()

    {

        int rowToClick = rand.nextInt(10);
        int colToClick = rand.nextInt(10);

        while (!grid[rowToClick][colToClick].getIcon().equals(WATER))

        {

            rowToClick = rand.nextInt(10);
            colToClick = rand.nextInt(10);

        }

        grid[rowToClick][colToClick].doClick(500);

    }

    //establishes interface and takes in username(s) of player(s)
    private void startPerform()

    {

        this.remove(mainMenuPanel);
        this.add(gameBoardPanel);
        gameBoardPanel.setVisible(true);

        //Set each grid icon to empty
        for (int i = 0; i < grid.length; i++)

        {

            for (int j = 0; j < grid[i].length; j++)

            {

                grid[i][j].setIcon(WATER);

            }

        }

        //Prompt user to enter their name(s)
        String p1Name, p2Name;
        if (isSinglePlayer)

        {

            p1Name = JOptionPane.showInputDialog("Welcome to Karn's Battleship! Enter a username:");
            p2Name = "Computer";

        }

        else

        {

            p1Name = JOptionPane.showInputDialog("Welcome to Karn's Battleship! Enter a username for player 1:");
            p2Name = JOptionPane.showInputDialog("Thanks! Please enter the second player's username: ");

        }

        //Initialize Player objects based on name entry
        if (p1Name != null && !p1Name.equals("")) //If name is not null/empty, create new player with entered name

        {
            playerOne = new Player(p1Name); //If name is null/empty, create new player with default name
        }

        else

        {

            playerOne = new Player();

        }

        if (p2Name != null && !p2Name.equals("") && !p2Name.equals(p1Name))
            playerTwo = new Player(p2Name);

        else if (p2Name.equals(p1Name))
            playerTwo = new Player(p2Name + "2");

        else
            playerTwo = new Player();

        //Rand object instantiated if it is one-player mode
        if (isSinglePlayer)
            rand = new Random();

        //Inform player to place ships
        JOptionPane.showMessageDialog(this, playerOne.getName() + ", it is time to place the ships! "
                + "Select which ship you want to place and click on the grid to place it.");

        //Always starts with p1's turn, so display it
        turnDisplayer.setText("It is " + playerOne.getName() + "'s turn...");

    }

    //method allows both players to place ships
    private void readyPerform()

    {

        Player playerTurn = isPlayerOneTurn? playerOne : playerTwo;

        //If all ships are placed
        if (playerTurn.destroyer1.getIsPlaced() && playerTurn.destroyer2.getIsPlaced() &&
                playerTurn.cruiser1.getIsPlaced() && playerTurn.cruiser2.getIsPlaced() &&
                playerTurn.battleship1.getIsPlaced() && playerTurn.battleship2.getIsPlaced())

        {

            //If player one in two-player mode clicks ready
            if (!isSinglePlayer && isPlayerOneTurn)

            {

                isPlayerOneTurn = false;
                changeGrid();
                JOptionPane.showMessageDialog(this,
                        "Give the mouse to " + playerTwo.getName() + " so they can place their ships!");
                turnDisplayer.setText(playerTwo.getName() + "'s turn...");

            }

            //Player one in one-player mode clicks ready
            else if (isSinglePlayer && isPlayerOneTurn)

            {

                isPlayerOneTurn = false;

                for (int i = 0; i < shipRadioButton.length; i++)

                {

                    shipRadioButton[i].doClick();
                    placeShipPerform(rand.nextInt(10), rand.nextInt(10));
                    placeShipPerform(rand.nextInt(10), rand.nextInt(10));

                }

                readyPerform(); //Calls this method again, but with playerTwo (computer)

            }

            else //Player two clicks ready

            {

                isPlayerOneTurn = true;
                isPlay = true;
                for(int i = 0; i < shipRadioButton.length; i++) gameBoardPanel.remove(shipRadioButton[i]);
                gameBoardPanel.remove(shipInstructions);
                gameBoardPanel.remove(ready);
                gameBoardPanel.remove(rotateShip);
                gameBoardPanel.add(scrollPane);
                gameBoardPanel.add(gameplayInstructions);
                turnDisplayer.setText(playerOne.getName() + "'s turn...");
                repaint();
                changeGrid();
                moveBox.append("Game started! " + playerOne.getName() + "'s turn to hit...");

            }

        }

        else //Not all ships are placed

            {

                JOptionPane.showMessageDialog(this, "Not all ships are placed yet!");

            }

    }

    //grid is changed so that players can see misses/hits during gameplay
    //note: some of the code in this method was found online at: https://github.com/cosenary/Battleship/blob/master/battleship/GameGUI.java
    private void changeGrid()

    {

        Player playerTurn = isPlayerOneTurn ? playerOne:playerTwo;
        Player notPlayerTurn = isPlayerOneTurn ? playerTwo:playerOne;

        if (isPlay)

        {

            for (int i = 0; i < grid.length; i++)

            {

                for (int j = 0; j < grid[i].length; j++)

                {

                    //If player did not click the grid yet, set icon to WATER
                    if (!playerTurn.getHits(i, j))
                        grid[i][j].setIcon(WATER);

                        //If player hit nothing, set icon to MISS
                    else if (playerTurn.getHits(i, j) && !notPlayerTurn.getShips(i, j))
                        grid[i][j].setIcon(MISS);

                        //If player hit opponent's ship, set icon to GOODHIT
                    else if (playerTurn.getHits(i, j) && notPlayerTurn.getShips(i, j))
                        grid[i][j].setIcon(GOODHIT);

                } //end nested for-loop

            } //end for-loop

        } //end if playMode

        else  //Not playmode yet (still placing ships)

            {

            //Make every icon empty first
            for (int i = 0; i < grid.length; i++)

            {

                for (int j = 0; j < grid[i].length; j++)

                {

                    grid[i][j].setIcon(WATER);

                }

            }

            //If any of the ships are placed, change the icon to the appropriate ship
            if (playerTurn.battleship1.getIsPlaced())

            {

                int [][] location = playerTurn.battleship1.getLocation();

                for (int i = 0; i < BATTLESHIP[0].length; i++) { //Change the icon of 5 buttons

                    if (playerTurn.battleship1.getOrientation() == 'l')
                        grid[location[i][0]][location[i][1]].setIcon(BATTLESHIP[0][i]);

                    else if (playerTurn.battleship1.getOrientation() == 'u')
                        grid[location[i][0]][location[i][1]].setIcon(BATTLESHIP[1][i]);

                    else if (playerTurn.battleship1.getOrientation() == 'r')
                        grid[location[i][0]][location[i][1]].setIcon(BATTLESHIP[2][i]);

                    else if (playerTurn.battleship1.getOrientation() == 'd')
                        grid[location[i][0]][location[i][1]].setIcon(BATTLESHIP[3][i]);

                }

            }

            if (playerTurn.battleship2.getIsPlaced())

            {

                int [][] location = playerTurn.battleship2.getLocation();

                for (int i = 0; i < BATTLESHIP[0].length; i++) //Change the icon of 5 buttons

                {
                    if (playerTurn.battleship2.getOrientation() == 'l') //Set icon for a ship facing left
                        grid[location[i][0]][location[i][1]].setIcon(BATTLESHIP[0][i]);

                    else if (playerTurn.battleship2.getOrientation() == 'u') //Set icon for a ship facing up
                        grid[location[i][0]][location[i][1]].setIcon(BATTLESHIP[1][i]);

                    else if (playerTurn.battleship2.getOrientation() == 'r') //Set icon to vertical large ship
                        grid[location[i][0]][location[i][1]].setIcon(BATTLESHIP[2][i]);

                    else if (playerTurn.battleship2.getOrientation() == 'd')
                        grid[location[i][0]][location[i][1]].setIcon(BATTLESHIP[3][i]);

                }

            }

            if (playerTurn.cruiser1.getIsPlaced())

            {

                int [][] location = playerTurn.cruiser1.getLocation();

                for (int i = 0; i < DESTROYER[0].length; i++) //Change the icon of 5 buttons

                {

                    if (playerTurn.cruiser1.getOrientation() == 'l') //Set icon for a ship facing left
                        grid[location[i][0]][location[i][1]].setIcon(DESTROYER[0][i]);

                    else if (playerTurn.cruiser1.getOrientation() == 'u') //Set icon for a ship facing up
                        grid[location[i][0]][location[i][1]].setIcon(DESTROYER[1][i]);

                    else if (playerTurn.cruiser1.getOrientation() == 'r') //Set icon to vertical large ship
                        grid[location[i][0]][location[i][1]].setIcon(DESTROYER[2][i]);

                    else if (playerTurn.cruiser1.getOrientation() == 'd')
                        grid[location[i][0]][location[i][1]].setIcon(DESTROYER[3][i]);

                }

            }

            if (playerTurn.cruiser2.getIsPlaced())

            {

                int [][] location = playerTurn.cruiser2.getLocation();

                for (int i = 0; i < DESTROYER[0].length; i++) //Change the icon of 5 buttons

                {

                    if (playerTurn.cruiser2.getOrientation() == 'l') //Set icon for a ship facing left
                        grid[location[i][0]][location[i][1]].setIcon(DESTROYER[0][i]);

                    else if (playerTurn.cruiser2.getOrientation() == 'u') //Set icon for a ship facing up
                        grid[location[i][0]][location[i][1]].setIcon(DESTROYER[1][i]);

                    else if (playerTurn.cruiser2.getOrientation() == 'r') //Set icon to vertical large ship
                        grid[location[i][0]][location[i][1]].setIcon(DESTROYER[2][i]);

                    else if (playerTurn.cruiser2.getOrientation() == 'd')
                        grid[location[i][0]][location[i][1]].setIcon(DESTROYER[3][i]);

                }

            }

            if (playerTurn.destroyer1.getIsPlaced())

            {

                int [][] location = playerTurn.destroyer1.getLocation();

                for (int i = 0; i < CRUISER[0].length; i++) //Change the icon of 5 buttons

                {

                    if (playerTurn.destroyer1.getOrientation() == 'l') //Set icon for a ship facing left
                        grid[location[i][0]][location[i][1]].setIcon(CRUISER[0][i]);

                    else if (playerTurn.destroyer1.getOrientation() == 'u') //Set icon for a ship facing up
                        grid[location[i][0]][location[i][1]].setIcon(CRUISER[1][i]);

                    else if (playerTurn.destroyer1.getOrientation() == 'r') //Set icon to vertical large ship
                        grid[location[i][0]][location[i][1]].setIcon(CRUISER[2][i]);

                    else if (playerTurn.destroyer1.getOrientation() == 'd')
                        grid[location[i][0]][location[i][1]].setIcon(CRUISER[3][i]);

                }

            }

            if (playerTurn.destroyer2.getIsPlaced())

            {

                int [][] location = playerTurn.destroyer2.getLocation();

                for (int i = 0; i < CRUISER[0].length; i++) //Change the icon of 5 buttons

                {

                    if (playerTurn.destroyer2.getOrientation() == 'l') //Set icon for a ship facing left
                        grid[location[i][0]][location[i][1]].setIcon(CRUISER[0][i]);

                    else if (playerTurn.destroyer2.getOrientation() == 'u') //Set icon for a ship facing up
                        grid[location[i][0]][location[i][1]].setIcon(CRUISER[1][i]);

                    else if (playerTurn.destroyer2.getOrientation() == 'r') //Set icon to vertical large ship
                        grid[location[i][0]][location[i][1]].setIcon(CRUISER[2][i]);

                    else if (playerTurn.destroyer2.getOrientation() == 'd')
                        grid[location[i][0]][location[i][1]].setIcon(CRUISER[3][i]);

                }

            }

        }

    }

    //playSound1 method is used to play explosion sounds during gameplay when grid is clicked
    public static void playSound1()

    {

        //audio clip is found in Sounds folder and converted to clip object, then played
        try

        {

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("Sounds/explosion.wav"));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();

        }

        catch(Exception ex)

        {

            System.out.println("Error with playing sound.");
            ex.printStackTrace();

        }

    }

    //playSound2 method is used to play intro music upon opening grid
    private static void playSound2(boolean flag)

    {

        //audio clip is found in Sounds folder and converted to clip object, then played
        try

        {

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("Sounds/intro.wav"));
            clip2 = AudioSystem.getClip();
            clip2.open(audioInputStream);
            clip2.start();

        }

        catch(Exception ex)

        {

            System.out.println("Error with playing sound.");
            ex.printStackTrace();

        }

        if (!flag)

        {

            clip2.stop();

        }

    }

    //method stops the intro music
    public static void stopSound2()

    {

        clip2.stop();

    }

    //method adjusts the volume of the explosion sounds
    public static void setVolume1(float volume)

    {

        try

        {

            if (volume < 0f || volume > 1f)
                throw new IllegalArgumentException("Volume not valid: " + volume);
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(20f * (float) Math.log10(volume));

        }

        catch (Exception e)

        {

            System.out.println("Error while setting volume...");

        }

    }

    //method allows the adjustment of the intro music
    public static void setVolume2(float volume)

    {

        try

        {

            if (volume < 0f || volume > 1f)
                throw new IllegalArgumentException("Volume not valid: " + volume);
            FloatControl gainControl = (FloatControl) clip2.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(20f * (float) Math.log10(volume));

        }

        catch (Exception e)

        {

            System.out.println("Error while setting volume...");

        }

    }

} //end class
