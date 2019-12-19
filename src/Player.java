/**
 *  Description: This class contains the methods that describe the Player object.
 *
 *  Author: Karn Bansal
 *
 *  Date: June 19, 2018
 */

public class Player

{

    //declaring instance variables for Player object
    private String name;
    private boolean[][] hits;
    private boolean[][] ships;
    public Ship destroyer1, destroyer2, cruiser1, cruiser2, battleship1, battleship2;


    public Player()

    {

        this("Guest"); //Calls parameterized constructor with "Guest" as the name

    }

    public Player(String name)

    {

        this.setName(name);

        //Set all hits to false
        hits = new boolean[10][10];
        for (int i = 0; i < hits.length; i++)

        {

            for (int j = 0; j < hits[i].length; j++)
                hits[i][j] = false;

        }

        //Set all ship placements to false
        ships = new boolean[10][10];
        for (int i = 0; i < hits.length; i++)

        {

            for (int j = 0; j < hits[i].length; j++)
                hits[i][j] = false;

        }

        //Initialize ships
        destroyer1 = new Ship("Destroyer 1", 2);
        destroyer2 = new Ship("Destroyer 2", 2);
        cruiser1 = new Ship("Cruiser 1", 3);
        cruiser2 = new Ship("Cruiser 2", 3);
        battleship1 = new Ship("Battleship 1", 4);
        battleship2 = new Ship("Battleship 2", 4);

    }


    public String getName()

    {

        return this.name;

    }

    public void setName(String n)

    {

        n = n.toUpperCase();
        n = n.trim();
        this.name = n;

    }

    public boolean getHits(int x, int y)

    {

        return this.hits[x][y];

    }

    public void setHits(int x, int y, boolean hasHit)

    {

        this.hits[x][y] = hasHit;

    }

    public boolean getShips(int x, int y)

    {

        return this.ships[x][y];

    }

    public void setShips(int x, int y, boolean hasShip)

    {

        this.ships[x][y] = hasShip;

    }

}
