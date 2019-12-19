/**
 *  Description: This class contains the methods that describe the Ship object.
 *
 *  Author: Karn Bansal
 *
 *  Date: June 19, 2018
 */

public class Ship

{

    //declaring instance variables for Ship objects
    private String name;
    private boolean isPlaced;
    private boolean isHit;
    private int length;
    private char orientation;
    private int[][] location;


    public Ship (String name, int length)

    {

        setName(name);
        setIsPlaced(false);
        setIsHit(false);
        setLocation(new int[length][2]);
        setOrientation('0'); //No orientation
        this.length = length;

    }

    //accessors and mutators below for the various instance variables associated with each ship

    public String getName()

    {

        return name;

    }

    public void setName(String n)

    {

        name = n;

    }

    public boolean getIsPlaced()

    {

        return isPlaced;

    }

    public void setIsPlaced(boolean p)

    {

        isPlaced = p;

    }

    public boolean getIsHit()

    {

        return isHit;

    }

    public void setIsHit(boolean h)

    {

        isHit = h;

    }

    public int getLength()

    {

        return length;

    }

    public int[][] getLocation()

    {

        return location;

    }

    public void setLocation(int[][] l)

    {

        location = l;

    }

    public char getOrientation()

    {

        return orientation;

    }

    public void setOrientation(char o)

    {

        orientation = o;

    }

}
