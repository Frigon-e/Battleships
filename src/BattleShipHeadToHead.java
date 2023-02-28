import java.util.Arrays;

public class BattleShipHeadToHead {
    public static final int DIMENSIONS = 10;
    public static int p1searchhit = 0;
    public static int p1sinkhit = 0;
    public static int p1searchmiss = 0;
    public static int p1sinkmiss = 0;
    public static int p2searchhit = 0;
    public static int p2sinkhit = 0;
    public static int p2searchmiss = 0;
    public static int p2sinkmiss = 0;
    public static int p1VictoryShots = 0;
    public static int p2VictoryShots = 0;
    public static String p1name;
    public static String p2name;

    public static void single() {
        int[] results = new int[100000];
        int[] buckets = new int[101];
        long startTime = System.nanoTime();
        int best = 100;
        int worst = 0;
        int total = 0;
        for (int i = 0; i < results.length; i++) {
            results[i] = onePlayerGame();
            System.out.println("Game " + i + " in " + results[i]);
            total += results[i];
            buckets[results[i]]++;
            if (results[i] < best) {
                best = results[i];
            }
            if (results[i] > worst) {
                worst = results[i];
            }
        }
        Arrays.sort(results);
        double median;
        if (results.length % 2 == 0)
            median = ((double) results[results.length / 2] + (double) results[results.length / 2 - 1]) / 2;
        else
            median = (double) results[results.length / 2];
        long estimatedTime = System.nanoTime() - startTime;
        double seconds = (double) estimatedTime / 1000000000.0;
        System.out.println("Average score is: " + (double) total / results.length);
        System.out.println("Best was " + best);
        System.out.println("Worst was " + worst);
        System.out.println("Median was " + median);
        double p1search = (double) p1searchhit * 100.0 / (p1searchhit + p1searchmiss);
        System.out.println("P1 search: " + p1search);
        System.out.println("P1 sink: " + p1sinkhit * 100.0 / (p1sinkhit + p1sinkmiss));

        System.out.println("It took " + seconds + " seconds to run " + results.length + " games");

        int winsU42 = 0;
        for (int i = 1; i < 101; i++) {
            if (i < 43)
                winsU42 += buckets[i];
        }

        System.out.println("Wins under 43 moves: " + winsU42);
    }

    public static void main(String[] args) {

        int[] results = new int[100000];
        int[] p1bucket = new int[101];
        int[] p2bucket = new int[101];
        long startTime = System.nanoTime();
        int p1points = 0;
        int p2points = 0;
        int p1 = 0;
        int p2 = 0;
        int tie = 0;
        for (int i = 0; i < results.length; i++) {
            results[i] = twoPlayerGame();
            //System.out.println("Results: " + results[i]);
            if (results[i] > 0) {
                p1bucket[p1VictoryShots]++;
                p1++;// results[i];
                p1points += results[i];
            }
            if (results[i] < 0) {
                p2bucket[p2VictoryShots]++;
                p2++;// -results[i];
                p2points += -results[i];
            }
            if (results[i] == 0) {
                tie++;
            }
            if (i % 40 == 0) {
                System.out.println('\f');
                System.out.println(p1name + " Wins: " + p1);
                System.out.println(p2name + " Wins: " + p2);

                if (p1 > p2)
                    System.out.println("\n\n " + p1name + " is ahead by " + (p1 - p2));
                else
                    System.out.println("\n\n " + p2name + " is ahead by " + (p2 - p1));

            }
            if (Math.abs(p1 - p2) >= 2000) {
                break;
            }
        }
        System.out.println('\f');
        long estimatedTime = System.nanoTime() - startTime;
        double seconds = (double) estimatedTime / 1000000000.0;
        System.out.println("\n");

        if (p1 - p2 > 200)
            System.out.println(p1name + " Wins by " + (p1 - p2) + " games!");
        else if (p2 - p1 > 200)
            System.out.println(p2name + " Wins by " + (p2 - p1) + " games!");
        else
            System.out.println("Too close to call");
        System.out.println("\n\n");
        System.out.println(p1name + " Wins: " + p1);
        System.out.println(p2name + " Wins: " + p2);
        System.out.println("Ties: " + tie);
        int games = p1 + p2 + tie;
        double p1search = (double) p1searchhit * 100.0 / (p1searchhit + p1searchmiss);
        System.out.println("P1 search: " + p1search);
        System.out.println("P1 sink: " + p1sinkhit * 100.0 / (p1sinkhit + p1sinkmiss));
        double p2search = (double) p2searchhit * 100.0 / (p2searchhit + p2searchmiss);
        System.out.println("P2 search: " + p2search);
        System.out.println("P2 sink: " + p2sinkhit * 100.0 / (p2sinkhit + p2sinkmiss));
        System.out.println("It took " + seconds + " seconds to run " + games + " games");


    }

    public static int twoPlayerGame() {
        //System.out.println("Start two player game");
        char[][] s1Guesses = new char[DIMENSIONS][DIMENSIONS];
        char[][] s2Guesses = new char[DIMENSIONS][DIMENSIONS];

        for (int row = 0; row < DIMENSIONS; row++) {
            for (int col = 0; col < DIMENSIONS; col++) {
                s1Guesses[row][col] = '.';
                s2Guesses[row][col] = '.';
            }
        }
        //int[][] totals = new int[DIMENSIONS][DIMENSIONS];
        int[][] gameBoard = new int[DIMENSIONS][DIMENSIONS];
        randomBoard(gameBoard);
        //problemBoard(gameBoard);
        char[][] s1Copy = new char[10][10];
        char[][] s2Copy = new char[10][10];
        int moves;
        for (moves = 1; moves < DIMENSIONS * DIMENSIONS; moves++) {
            //copy array before passing to students in case they cheat
            //char[][] s1Copy = Arrays.copyOf(s1Guesses, s1Guesses.length);
            //char[][] s1Copy = s1Guesses.clone();

            for (int i = 0; i < s1Guesses.length; i++) {
                for (int j = 0; j < s1Guesses[i].length; j++) {
                    s1Copy[i][j] = s1Guesses[i][j];
                    s2Copy[i][j] = s2Guesses[i][j];
                }
            }

            //char[][] s2Copy = Arrays.copyOf(s2Guesses, s2Guesses.length);
            boolean p1sink = false;
            boolean p2sink = false;
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (s1Copy[i][j] == 'X')
                        p1sink = true;
                    if (s2Copy[i][j] == 'X')
                        p2sink = true;
                }
            }

            String guess1 = Ethan_Best_HeatMap.makeGuess(s1Copy);
            String guess2 = Ethan_War_HeatMap.makeGuess(s2Copy);
            p1name = "Best Ethan   ";
            p2name = "Wars Ethan   ";


            if (hit1miss0(guess1, gameBoard) == 1) {
                if (p1sink)
                    p1sinkhit++;
                else
                    p1searchhit++;
            } else {
                if (p1sink)
                    p1sinkmiss++;
                else
                    p1searchmiss++;
            }

            if (hit1miss0(guess2, gameBoard) == 1) {
                if (p2sink)
                    p2sinkhit++;
                else
                    p2searchhit++;
            } else {
                if (p2sink)
                    p2sinkmiss++;
                else
                    p2searchmiss++;
            }

            boolean p1 = updateGuess(s1Guesses, guess1, gameBoard);
            boolean p2 = updateGuess(s2Guesses, guess2, gameBoard);

            if (p1 || p2) {

                if (p1 && p2) {
                    //System.out.println("Tie! at " + moves);
                    return 0;
                } else if (p1) {
                    //System.out.println("P1 wins in " + moves);
                    p1VictoryShots = moves;
                    return checkShips(s2Guesses);
                } else {
                    //System.out.println("P2 wins in " + moves);
                    p2VictoryShots = moves;
                    return -checkShips(s1Guesses);
                }

            }
        }
        return 0;
    }

    public static int checkShips(char[][] guesses) {
        boolean[] shipsAlive = {true, true, true, true, true};
        for (int y = 0; y < guesses.length; y++) {
            for (int x = 0; x < guesses.length; x++) {
                if (guesses[y][x] >= '1' && guesses[y][x] <= '5') {
                    shipsAlive[guesses[y][x] - '1'] = false;
                }
            }
        }
        int total = 0;
        for (int i = 0; i < shipsAlive.length; i++) {
            if (shipsAlive[i])
                total++;
        }
        return total;
    }

    public static int hit1miss0(String guess, int[][] gameBoard) {
        int y = Character.toUpperCase(guess.charAt(0)) - 'A';
        int x;
        if (guess.charAt(1) == '1' && guess.length() > 2 && guess.charAt(2) == '0') {
            x = 9;
        } else {
            x = (guess.charAt(1) - '1');
        }

        if (x >= 0 && x < gameBoard.length && y >= 0 && y < gameBoard.length) {
            if (gameBoard[y][x] == 0) {
                return 0;
            } else {
                return 1;

            }
        }
        return 0;
    }

    public static int onePlayerGame() {
        char[][] s1Guesses = new char[DIMENSIONS][DIMENSIONS];

        for (int row = 0; row < DIMENSIONS; row++) {
            for (int col = 0; col < DIMENSIONS; col++) {
                s1Guesses[row][col] = '.';
            }
        }

        int[][] gameBoard = new int[DIMENSIONS][DIMENSIONS];
        randomBoard(gameBoard);

        int moves;

        for (moves = 1; moves < DIMENSIONS * DIMENSIONS; moves++) {
            //copy array before passing to students in case they cheat
            char[][] s1Copy = Arrays.copyOf(s1Guesses, s1Guesses.length);
            boolean p1sink = false;
            boolean p2sink = false;
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (s1Copy[i][j] == 'X')
                        p1sink = true;

                }
            }
            //ask student for guess based on Guesses Map
            String guess1 = Ethan_Best_HeatMap.makeGuess(s1Copy);
            //String guess1 = Ethan_HeatMap.makeGuess(s1Copy);

            // System.out.println("Guess was " + guess1);

            if (hit1miss0(guess1, gameBoard) == 1) {
                if (p1sink)
                    p1sinkhit++;
                else
                    p1searchhit++;
            } else {
                if (p1sink)
                    p1sinkmiss++;
                else
                    p1searchmiss++;
            }

            boolean p1 = updateGuess(s1Guesses, guess1, gameBoard);
            //update S1Guesses
            if (p1) {
                break;
            }
        }

        if (moves == DIMENSIONS * DIMENSIONS) {
            System.out.println("Out of Moves");
            printBoard(gameBoard);
            System.out.println();
            printPlayerBoard(s1Guesses);
            System.out.println();
        }

        return moves;
    }

    public static void testRandom() {
        long start = System.nanoTime();
        int[][] totals = new int[DIMENSIONS][DIMENSIONS];
        for (int k = 0; k < 500000; k++) {
            int[][] gameBoard = new int[DIMENSIONS][DIMENSIONS];
            randomBoard(gameBoard);
            //problemBoard(gameBoard);

            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (gameBoard[i][j] > 0)
                        totals[i][j]++;

                }
            }
        }
        long end = System.nanoTime();
        double time = (end - start) / 1000000000.0;
        System.out.println("That took " + time);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                //System.out.printf("%.5f ", totals[i][j]/1700000.0);
                System.out.print(totals[i][j] + " ");
            }
            System.out.println();
        }

        for (int i = 0; i < 10; i++) {
            int r = 0;
            for (int j = 0; j < 10; j++) {
                r += totals[i][j];
            }
            System.out.println("Row " + i + " is " + r);
        }

    }

    private static void shuffleArray(int[] array) {
        int index, temp;

        for (int i = array.length - 1; i > 0; i--) {
            index = (int) (Math.random() * (i + 1));
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    public static void randomBoard(int[][] gameBoard) {
        int[] ships = new int[5];
        int[] shipIndex = {0, 1, 2, 3, 4};
        ships[0] = 2;
        ships[1] = 3;
        ships[2] = 3;
        ships[3] = 4;
        ships[4] = 5;

        shuffleArray(shipIndex);
        for (int temp = 0; temp < ships.length; temp++) {
            int ship = shipIndex[temp];
            boolean valid = false;
            int orient = (int) (Math.random() * 2);
            int x, y;
            if (orient == 0) {// horizontal
                while (!valid) {
                    x = (int) (Math.random() * (gameBoard.length - ships[ship] + 1));
                    y = (int) (Math.random() * (gameBoard.length));
                    int check = 0;
                    for (int i = 0; i < ships[ship]; i++) {
                        check += gameBoard[x + i][y];
                    }
                    if (check == 0) {
                        for (int i = 0; i < ships[ship]; i++) {
                            gameBoard[x + i][y] = ship + 1;
                        }
                        valid = true;
                    }
                }
            } else {
                while (!valid) {
                    x = (int) (Math.random() * (gameBoard.length));
                    y = (int) (Math.random() * (gameBoard.length - ships[ship] + 1));
                    int check = 0;
                    for (int i = 0; i < ships[ship]; i++) {
                        check += gameBoard[x][y + i];
                    }
                    if (check == 0) {
                        for (int i = 0; i < ships[ship]; i++) {
                            gameBoard[x][y + i] = ship + 1;
                        }
                        valid = true;
                    }
                }
            }
        }
    }

    public static void problemBoard(int[][] gameBoard) {

        gameBoard[9][8] = 1;
        gameBoard[9][9] = 1;
        gameBoard[2][1] = 2;
        gameBoard[3][1] = 2;
        gameBoard[4][1] = 2;
        gameBoard[8][7] = 3;
        gameBoard[8][8] = 3;
        gameBoard[8][9] = 3;
        gameBoard[5][3] = 4;
        gameBoard[6][3] = 4;
        gameBoard[7][3] = 4;
        gameBoard[8][3] = 4;
        gameBoard[3][9] = 5;
        gameBoard[4][9] = 5;
        gameBoard[5][9] = 5;
        gameBoard[6][9] = 5;
        gameBoard[7][9] = 5;

    }

    public static void printBoard(int[][] gameBoard) {
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard.length; j++) {
                System.out.print(gameBoard[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void printPlayerBoard(char[][] playerBoard) {
        for (int i = 0; i < playerBoard.length; i++) {
            for (int j = 0; j < playerBoard.length; j++) {
                System.out.print(playerBoard[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static boolean updateGuess(char[][] guessMap, String guess, int[][] gameBoard) {
        boolean complete = false;
        int y = Character.toUpperCase(guess.charAt(0)) - 'A';
        int x;
        if (guess.charAt(1) == '1' && guess.length() > 2 && guess.charAt(2) == '0') {
            x = 9;
        } else {
            x = (guess.charAt(1) - '1');
        }

        if (x >= 0 && x < gameBoard.length && y >= 0 && y < gameBoard.length) {
            complete = true;
            if (gameBoard[y][x] == 0) {
                guessMap[y][x] = 'O';
                complete = false;
            } else {
                guessMap[y][x] = 'X';
                int num = gameBoard[y][x];
                boolean last = true;
                for (int row = 0; row < gameBoard.length; row++) {
                    for (int col = 0; col < gameBoard[row].length; col++) {
                        if (num == gameBoard[row][col] && guessMap[row][col] != 'X') {
                            last = false;
                        }
                    }
                }

                if (last) {
                    for (int row = 0; row < gameBoard.length; row++) {
                        for (int col = 0; col < gameBoard[row].length; col++) {
                            if (num == gameBoard[row][col])
                                guessMap[row][col] = (char) (gameBoard[row][col] + 48);
                        }
                    }
                } else {
                    guessMap[y][x] = 'X';
                }

                for (int row = 0; row < gameBoard.length; row++) {
                    for (int col = 0; col < gameBoard[row].length; col++) {
                        if (gameBoard[row][col] > 0 && guessMap[row][col] == '.')
                            complete = false;
                    }
                }

            }
        }
        return complete;
    }
}
