import java.util.Queue;
import java.util.Random;

public class Minefield {
    /**
    Global Section
    */
    public static final String ANSI_YELLOW_BRIGHT = "\u001B[33;1m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE_BRIGHT = "\u001b[34;1m";
    public static final String ANSI_BLUE = "\u001b[34m";
    public static final String ANSI_RED_BRIGHT = "\u001b[31;1m";
    public static final String ANSI_RED = "\u001b[31m";
    public static final String ANSI_GREEN = "\u001b[32m";
    public static final String ANSI_PURPLE = "\u001b[35m";
    public static final String ANSI_CYAN = "\u001b[36m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001b[47m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001b[45m";
    public static final String ANSI_GREY_BACKGROUND = "\u001b[0m";
    public static final String ANSI_ORANGE = "\033[38;5;208m"; //ANSI code for orange
    public static final String ANSI_BLACK = "\033[38;5;16m"; //ANSI code for black
    public static final String ANSI_GREY = "\033[38;5;249m"; //ANSI code for grey

    /* 
     * Class Variable Section
     * 
    */

    private int rows;
    private int cols;
    private int numberOfFlags;
    private boolean[][] flags; // stores the position of flags
    private int numberOfMines;
    private int safeSpots = 0;
    private Cell[][] field;
    public boolean gameOver = false;

    /*Things to Note:
     * Please review ALL files given before attempting to write these functions.
     * Understand the Cell.java class to know what object our array contains and what methods you can utilize
     * Understand the StackGen.java class to know what type of stack you will be working with and methods you can utilize
     * Understand the QGen.java class to know what type of queue you will be working with and methods you can utilize
     */
    
    /**
     * Minefield
     * 
     * Build a 2-d Cell array representing your minefield.
     * Constructor
     * @param rows       Number of rows.
     * @param columns    Number of columns.
     * @param flags      Number of flags, should be equal to mines
     */
    public Minefield(int rows, int columns, int flags) {
        this.rows = rows;
        this.cols = columns;
        this.field = new Cell[rows][cols];
        this.flags = new boolean[rows][cols];
        this.numberOfFlags = flags;
        this.numberOfMines = flags;
        //initialize all cells of field 2d array
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                field[i][j] = new Cell(false, "-");
            }
        }
    }

    /**
     * evaluateField
     * 
     *
     * @function:
     * Evaluate entire array.
     * When a mine is found check the surrounding adjacent tiles. If another
     * mine is found during this check, increment adjacent cells status by 1.
     * 
     */
    public void evaluateField() {
        //iterates through field and updates non-mine tiles to numerical values
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                if(field[i][j].getStatus().equals("M")) {
                    continue;
                }
                int count = 0;
                //counts the # of mines in the 8 adjacent tiles
                for(int k = i - 1; k < i+2; k++) {
                    for(int l = j - 1; l < j+2; l++) {
                        if(k >= 0 && l >= 0 && k < rows && l < cols && field[k][l].getStatus().equals("M")) {
                            count++;
                        }
                    }
                }
                field[i][j].setStatus(Integer.toString(count));
//                field[i][j].setRevealed(true);
            }
        }
    }

    /**
     * createMines
     * 
     * Randomly generate coordinates for possible mine locations.
     * If the coordinate has not already been generated and is not equal to the starting cell set the cell to be a mine.
     * utilize rand.nextInt()
     * 
     * @param x       Start x, avoid placing on this square.
     * @param y        Start y, avoid placing on this square.
     * @param mines      Number of mines to place.
     */
    public void createMines(int x, int y, int mines) {
        //creates mines at random coordinates
        Random rand = new Random();
        while(mines > 0) {
            int xco = rand.nextInt(0, cols);
            int yco = rand.nextInt(0, rows);
            if(!field[xco][yco].getStatus().equals("M") && (xco != x && yco != y)) {
                field[xco][yco].setStatus("M");
                mines--;
            }
        }
    }

    /**
     * guess
     * 
     * Check if the guessed cell is inbounds (if not done in the Main class). 
     * Either place a flag on the designated cell if the flag boolean is true or clear it.
     * If the cell has a 0 call the revealZeroes() method or if the cell has a mine end the game.
     * At the end reveal the cell to the user.
     * 
     * 
     * @param x       The x value the user entered.
     * @param y       The y value the user entered.
     * @param flag    A boolean value that allows the user to place a flag on the corresponding square.
     * @return boolean Return false if guess did not hit mine or if flag was placed, true if mine found.
     */
    public boolean guess(int x, int y, boolean flag) {
        //checks if coordinates are inbounds
        if (x < 0 || x > cols - 1 || y < 0 || y > rows - 1) {
            return false;
        }
        //checks if user wants to flag coordinates
        if(flag) {
            //checks if they ran out of flags
            if (numberOfFlags == 0) {
                return false;
            }
            //user is unflagging tile
            if (flags[y][x]) {
                flags[y][x] = false;
                numberOfFlags++;
            }
            //flagging tile
            else {
                flags[y][x] = true;
                numberOfFlags--;
            }
            return false;
        }
        //check if user opened pocket
        if(field[y][x].getStatus().equals("0")) {
            revealZeroes(x, y);
            //checks if player has won game by opening all safe spots
            if (safeSpots + numberOfMines == rows * cols) {
                System.out.println("looks like you WON!");
                gameOver = true;
            }
            return false;
        }
        //player opened mine and lost
        else if(field[y][x].getStatus().equals("M")){
            field[y][x].setRevealed(true);
            gameOver = true;
            return true;
        }
        //player opened square that is a mine-adjacent tile
        else {
            if (!field[y][x].getRevealed()) {
                field[y][x].setRevealed(true);
                safeSpots++;
            }
            //checks if user won
            if (safeSpots + numberOfMines == rows * cols) {
                System.out.println("looks like you WON!");
                gameOver = true;
            }
            return false;
        }

    }

    /**
     * gameOver
     * 
     * Ways a game of Minesweeper ends:
     * 1. player guesses a cell with a mine: game over -> player loses
     * 2. player has revealed the last cell without revealing any mines -> player wins
     * 
     * @return boolean Return false if game is not over and squares have yet to be revealed, otheriwse return true.
     */
    public boolean gameOver() {
        //checks if game is over
        return gameOver;
    }

    /**
     * Reveal the cells that contain zeroes that surround the inputted cell.
     * Continue revealing 0-cells in every direction until no more 0-cells are found in any direction.
     * Utilize a STACK to accomplish this.
     *
     * This method should follow the psuedocode given in the lab writeup.
     * Why might a stack be useful here rather than a queue?
     *
     * @param x      The x value the user entered.
     * @param y      The y value the user entered.
     */
    public void revealZeroes(int x, int y) {
        //uses DFS to reveal all zeroes adjact to x,y coordinate
        Stack1Gen<Integer> st = new Stack1Gen<Integer>();
        st.push(x);
        st.push(y);
        while(!st.isEmpty()) {
            y = st.pop();
            x = st.pop();

            if (field[y][x].getRevealed()) {

                continue;
            }
            field[y][x].setRevealed(true);

            safeSpots++;
            if (field[y][x].getStatus().equals("0")) {

                //top
                if(x >= 0 && y - 1>= 0 && x < cols && y - 1 < rows) {
                    st.push(x);
                    st.push(y-1);
                }
                //left
                if(x - 1>= 0 && y >= 0 && x - 1 < cols && y < rows) {
                    st.push(x-1);
                    st.push(y);
                }
                //down
                if(x >= 0 && y + 1 >= 0 && x < cols && y + 1 < rows) {
                    st.push(x);
                    st.push(y+1);
                }
                //right
                if(x + 1 >= 0 && y >= 0 && x + 1 < cols && y < rows) {
                    st.push(x+1);
                    st.push(y);
                }

            }
        }
    }

    /**
     * revealStartingArea
     *
     * On the starting move only reveal the neighboring cells of the inital
     * cell and continue revealing the surrounding concealed cells until a mine is found.
     * Utilize a QUEUE to accomplish this.
     * 
     * This method should follow the psuedocode given in the lab writeup.
     * Why might a queue be useful for this function?
     *
     * @param x     The x value the user entered.
     * @param y     The y value the user entered.
     */
    public void revealStartingArea(int x, int y) {
        //uses BFS to reveal neighboring cells of x, y coordinate
        Q1Gen<Integer> q = new Q1Gen<Integer>();
        q.add(x);
        q.add(y);
        while(q.length() > 0) {
            x = q.remove();
            y = q.remove();
            if (field[y][x].getRevealed()) {
                continue;
            }
            field[y][x].setRevealed(true);
            safeSpots++;
            if (field[y][x].getStatus().equals("0")) {
                //top
                if(x >= 0 && y - 1>= 0 && x < cols && y - 1 < rows) {
                    q.add(x);
                    q.add(y-1);
                }
                //left
                if(x - 1>= 0 && y >= 0 && x - 1 < cols && y < rows) {
                    q.add(x-1);
                    q.add(y);
                }
                //down
                if(x >= 0 && y + 1 >= 0 && x < cols && y + 1 < rows) {
                    q.add(x);
                    q.add(y+1);
                }
                //right
                if(x + 1 >= 0 && y >= 0 && x + 1 < cols && y < rows) {
                    q.add(x+1);
                    q.add(y);
                }
            }
        }
    }

    /**
     * For both printing methods utilize the ANSI colour codes provided! 
     * 
     * 
     * 
     * 
     * 
     * debug
     *
     * @function This method should print the entire minefield, regardless if the user has guessed a square.
     * *This method should print out when debug mode has been selected. 
     */
    public void debug() {
        //prints out whole board
        String output = "  ";
        for(int i = 0; i < cols; i++) {
            output += i + " ";
        }
        output += "\n";
        for(int i = 0; i < rows; i++) {
            output += i + " ";
            for(int j = 0; j < cols; j++) {
                if(!field[i][j].getRevealed()) {
                    field[i][j].setRevealed(true);
                    output += renderCell(field[i][j]) + " ";
                    field[i][j].setRevealed(false);
                }
                else {
                    output += renderCell(field[i][j]) + " ";
                }
            }
            output += "\n";
        }
        System.out.println(output);
    }

    public String renderCell(Cell c) {
        //returns current status with corresponding color
        if(c.getRevealed()) {
            switch (c.getStatus()) {
                case "0":
                    return ANSI_YELLOW + "0" + ANSI_GREY_BACKGROUND;
                case "1":
                    return ANSI_BLUE_BRIGHT + "1" + ANSI_GREY_BACKGROUND;
                case "2":
                    return ANSI_GREEN + "2" + ANSI_GREY_BACKGROUND;
                case "3":
                    return ANSI_RED_BRIGHT + "3" + ANSI_GREY_BACKGROUND;
                case "4":
                    return ANSI_BLUE + "4" + ANSI_GREY_BACKGROUND;
                case "5":
                    return ANSI_PURPLE + "5" + ANSI_GREY_BACKGROUND;
                case "6":
                    return ANSI_CYAN + "6" + ANSI_GREY_BACKGROUND;
                case "7":
                    return ANSI_BLACK + "7" + ANSI_GREY_BACKGROUND;
                case "8":
                    return ANSI_GREY + "8" + ANSI_GREY_BACKGROUND;
                case "M":
                    return ANSI_RED + "M" + ANSI_GREY_BACKGROUND;
            }
        }
        return "-";
    }
    /**
     * toString
     *
     * @return String The string that is returned only has the squares that has been revealed to the user or that the user has guessed.
     */
    public String toString() {
        //converts board to string and returns
        String output = "  ";
        for(int i = 0; i < cols; i++) {
            output += i + " ";
        }
        output += "\n";
        for(int i = 0; i < rows; i++) {
            output += i + " ";
            for(int j = 0; j < cols; j++) {
                if (flags[i][j]) {
                    if (field[i][j].getRevealed()) {
                        output += renderCell(field[i][j]) + " ";
                        numberOfFlags--;
                        flags[i][j] = false;
                    } else {
                        output += ANSI_ORANGE + "F " + ANSI_GREY_BACKGROUND;
                    }
                } else {
                    output += renderCell(field[i][j]) + " ";
                }
            }
            output += "\n";
        }
        return output;
    }
}

