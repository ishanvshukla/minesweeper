//Import Section
import java.util.Random;
import java.util.Scanner;

/*
 * Provided in this class is the neccessary code to get started with your game's implementation
 * You will find a while loop that should take your minefield's gameOver() method as its conditional
 * Then you will prompt the user with input and manipulate the data as before in project 2
 * 
 * Things to Note:
 * 1. Think back to project 1 when we asked our user to give a shape. In this project we will be asking the user to provide a mode. Then create a minefield accordingly
 * 2. You must implement a way to check if we are playing in debug mode or not.
 * 3. When working inside your while loop think about what happens each turn. We get input, user our methods, check their return values. repeat.
 * 4. Once while loop is complete figure out how to determine if the user won or lost. Print appropriate statement.
 */

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        //picking difficulty
        System.out.println("What difficulty would you like to play? (e, m, h)");
        String diff = sc.nextLine();
        int numberOfMines = 0;
        Minefield minefield = null;
        if(diff.equals("e")) {
            numberOfMines = 5;
            minefield = new Minefield(5, 5, numberOfMines);
        }
        if(diff.equals("m")) {
            numberOfMines = 12;
            minefield = new Minefield(9, 9, numberOfMines);
        }
        if(diff.equals("h")) {
            numberOfMines = 40;
            minefield = new Minefield(20, 20, numberOfMines);
        }
        //playing in debug mode
        System.out.println("Would you like to play in debug mode? y/n");
        String debug = sc.nextLine();
        //open starting position
        System.out.println("Enter starting coordinate: [x] [y]");
        String[] s = sc.nextLine().split(" ");
        int x = Integer.parseInt(s[0]);
        int y = Integer.parseInt(s[1]);
        minefield.createMines(x, y, numberOfMines);
        minefield.evaluateField();
        if(debug.equals("y")) {
            minefield.debug();
        }
        minefield.revealStartingArea(x, y);
        System.out.println(minefield);
        boolean wonGame = true;
        while(!minefield.gameOver()) {
            //user guesses coordinate
            System.out.println("Enter a coordinate and if you wish to place a flag: [x] [y] -1 for flag.");
            s = sc.nextLine().split(" ");
            x = Integer.parseInt(s[0]);
            y = Integer.parseInt(s[1]);
            int f = Integer.parseInt(s[2]);
            //inputs player's move and updates board and win condition
            if(minefield.guess(x, y, f == -1)) {
                wonGame = false;
            }
            if(debug.equals("y")) {
                minefield.debug();
            }
            System.out.println(minefield);
        }
        //user wins
        if(wonGame) {
            System.out.println("looks like you WON!");
        }
        //user loses
        else {
            System.out.println("you suck never play minesweeper ever again >:(");
        }

    }
}
