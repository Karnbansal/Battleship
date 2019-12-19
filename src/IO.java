import java.io.*;
import java.nio.file.*;
import java.util.List;

/**
 *  Description: This class contains the methods that allow for IO manipulation
 *  to be done (for username and number of wins).
 *
 *  Author: Karn Bansal
 *
 *  Date: June 19, 2018
 */

public class IO

{

    private static PrintWriter fileOut;
    private static BufferedReader fileIn;
    private static final String filePath = "PlayerInfo/PlayerInfo";


    public static int[] getScore(String name) throws IOException

    {

        //int[] with 2 indexes store player's total wins and total games
        int[] score = new int[2];

        //Open input file
        openInputFile(filePath);

        //Go to next line until reaches end or finds name
        String line = readLine();
        while (line != null)

        {

            if (line.contains(name)) break;
            line = readLine();

        }

        closeInputFile(); //Close input file

        //Set score to values before and after '/' in the text file
        if (line != null)

        {

            score[0] = Integer.parseInt(String.valueOf(line.charAt(line.indexOf('/') - 1)));
            score[1] = Integer.parseInt(String.valueOf(line.charAt(line.indexOf('/') + 1)));

        }
        //If line is null (name wasn't found) score stays 0, 0

        return score;

    }

    /**
     * Edits the PlayerInfo file depending on whether or not a player won a game.
     * @param name The name of the player to edit the score of.
     * @param isWinner Whether or not the player won.
     * @throws IOException
     */
    public static void editScore(String name, boolean isWinner) throws IOException

    {

        //Open input and output file
        openInputFile(filePath);
        openOutputFile(filePath);

        //Go to next line until reaches end or finds name
        String line = readLine();
        while (line != null)

        {

            if (line.contains(name)) break; //If line contains name, store the line and exit loop
            line = readLine();

        }

        //If player doesn't exist, add player
        if (line == null)

        {

            if (isWinner)
                println(name + ": 1/1");
            else
                println(name + ": 0/1");

        }

        //If player does exist, add 1 to each number before and after '/'
        else

            {

            int wins = Integer.parseInt(String.valueOf(line.charAt(line.indexOf('/') - 1)));
            int totalGames = Integer.parseInt(String.valueOf(line.charAt(line.indexOf('/') + 1)));
            if (isWinner) wins++; //If they are the winner add one to wins
            totalGames++;

            //Find line to replace and replaces it (using java.nio (new IO))
            List<String> fileContent = Files.readAllLines(Paths.get(filePath));
            for (int i = 0; i < fileContent.size(); i++) {
                if (fileContent.get(i).contains(name)){
                    fileContent.set(i, name + ": " + wins + "/" + totalGames);
                    break;

                }

            }

            Files.write(Paths.get(filePath), fileContent);

        }

        //Close input and output file
        closeInputFile();
        closeOutputFile();

    }

    private static void openInputFile(String filePath)

    {

        try

        {

            fileIn = new BufferedReader(new FileReader(filePath));

        }

        catch (FileNotFoundException e)

        {

            System.out.println("Cannot open file");

        }

    }

    private static void closeInputFile() throws IOException

    {

        fileIn.close();

    }

    private static void openOutputFile(String filePath)

    {

        try

        {

            fileOut = new PrintWriter(new BufferedWriter(new FileWriter(filePath, true)));

        }

        catch (IOException e)

        {

            System.out.println("Cannot open file");

        }

    }

    private static void closeOutputFile()

    {

        fileOut.close();

    }

    private static String readLine() throws IOException

    {

        return fileIn.readLine();

    }

    private static void println(String text)

    {

        fileOut.append(text + "\n");

    }

}
