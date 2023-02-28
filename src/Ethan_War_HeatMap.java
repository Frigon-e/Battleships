import java.util.Arrays;
import java.util.HashMap;

public class Ethan_War_HeatMap {
    public static String makeGuess(char[][] guesses) {
        boolean[] sunkShips = {false, false, false, false, false};
        int[][] HeatMap = new int[10][10];
        int[] allShipLengths = new int[]{2, 3, 3, 4, 5};
        HashMap<Integer, int[]> row_col = new HashMap<>();
        HashMap<Integer, int[]> col_row = new HashMap<>();
        int move = 0;

        // Updates the sunkShips array
        // Adds all hits to a hashmap
        for (int row = 0; row < guesses.length; row++) {
            for (int col = 0; col < guesses.length; col++) {
                if (!sunkShips[0] && guesses[row][col] == '1')
                    sunkShips[0] = true;
                if (!sunkShips[1] && guesses[row][col] == '2')
                    sunkShips[1] = true;
                if (!sunkShips[2] && guesses[row][col] == '3')
                    sunkShips[2] = true;
                if (!sunkShips[3] && guesses[row][col] == '4')
                    sunkShips[3] = true;
                if (!sunkShips[4] && guesses[row][col] == '5')
                    sunkShips[4] = true;

                if (guesses[row][col] == 'X') {
                    addToHashMap(row_col, row, col);
                    addToHashMap(col_row, col, row);
                }

            }
        }


        // Gets rid of ships that have been sunk
        boolean[] boolArr = new boolean[]{true, false};
        int[] aliveShipLengths = new int[countAliveShips(sunkShips)];
        int index = 0;
        for (int i = 0; i < sunkShips.length; i++) {
            if (!sunkShips[i]) {
                aliveShipLengths[index] = allShipLengths[i];
                index++;
            }
        }

        // [3,5,6]
        // ships left sizes [2,3,4,5]
        // 3
        if (!row_col.isEmpty())
            huntSpecialHits(HeatMap, guesses, row_col, aliveShipLengths, true);
        if (!col_row.isEmpty())
            huntSpecialHits(HeatMap, guesses, col_row, aliveShipLengths, false);

        // adds 1 to all the spots around the hits
        for (int row = 0; row < guesses.length; row++) {
            for (int col = 0; col < guesses.length; col++) {
                if (guesses[row][col] == 'X') {
                    addToSurroundingSpots(HeatMap, row, col);
                }
            }
        }
        // HeatMaps the ships that are still alive
        int[] betterAliveShips = new int[aliveShipLengths.length - 1];
        boolean doubleThreeShips = false;
        if (!sunkShips[1] && !sunkShips[2]) {
            doubleThreeShips = true;
            index = 0;
            for (int i = 0; i < sunkShips.length; i++) {
                if (!sunkShips[i] && i != 1) {
                    betterAliveShips[index] = allShipLengths[i];
                    index++;
                }
            }
        }

        for (int length : doubleThreeShips ? betterAliveShips : aliveShipLengths) {
            for (int row = 0; row < guesses.length; row++) {
                for (int col = 0; col < guesses[row].length; col++) {
                    for (boolean dir_vert : boolArr) {
                        if (checkValid(row, col, length, guesses, dir_vert)) {
                            intermentHeatMap(row, col, length, HeatMap, dir_vert, 1);
                        }
                    }
                }
            }
        }

        // printBoard(guesses);
        // printHashMap(row_col);
        // System.out.println();
        // printHashMap(col_row);
        // printBoard(HeatMap);
        // System.out.println();


        return highestNumber(HeatMap, guesses, getLargestShip(aliveShipLengths), move);
    }

    private static void huntSpecialHits(int[][] heatMap, char[][] guesses, HashMap<Integer, int[]> integerHashMap, int[] aliveShipLengths, boolean isRow) {
        // Goes through each ship length
        int length = aliveShipLengths[aliveShipLengths.length - 1];
        // Goes through each row/col that has a hit
        for (int key : integerHashMap.keySet()) {
            int[] hit_arr = integerHashMap.get(key);
            // row or col array of guesses depending on isRow
            char[] row_or_col = isRow ? guesses[key] : getCol(guesses, key);
            if (hit_arr.length > 1) {
                // Finds the longest consecutive hits
//                System.out.println("key: " + key);
//                System.out.println("row_or_col: " + Arrays.toString(row_or_col));
//                System.out.println("hit_arr: " + Arrays.toString(hit_arr));
//                System.out.print("Getting indices of gap: ");
                // Returns all consecutive indices of '.' between hits in the row/col
                int[] indices = findGapIndices(row_or_col, hit_arr, length);
                if (indices.length > 0) {
                    if (isRow) {
                        heatMap[key][indices[0]] = 100;
                    } else {
                        heatMap[indices[0]][key] = 100;
                    }
                }
                // System.out.println(Arrays.toString(indices));
                // System.out.println();

                int[] longestConsecutiveHits = findLongestConsecutiveHits(hit_arr);
                // System.out.print("Finding longest consecutive hits Start/End: ");
                // System.out.println(Arrays.toString(longestConsecutiveHits));
                if (!Arrays.equals(longestConsecutiveHits, new int[]{0, 0})) {
                    if (longestConsecutiveHits[0] - 1 > 0) {
                        if (isRow) {
                            heatMap[key][longestConsecutiveHits[0] - 1] += 30;
                        } else {
                            heatMap[longestConsecutiveHits[0] - 1][key] += 30;
                        }
                    }
                    if (longestConsecutiveHits[1] + 1 < 10) {
                        if (isRow) {
                            heatMap[key][longestConsecutiveHits[1] + 1] += 30;
                        } else {
                            heatMap[longestConsecutiveHits[1] + 1][key] += 30;
                        }
                    }
                }
            }
        }

    }

    /**
     * @param rowOrCol: row or col array of guesses depending on isRow
     * @param hitArr:   array of hits in the row/col
     * @param length:   length of the longest ship
     * @return all consecutive indices of '.' between hits in the row/column
     */
    private static int[] findGapIndices(char[] rowOrCol, int[] hitArr, int length) {
        int[] indices = new int[0];
        // Goes through each hit
        for (int index1 = 0; index1 < hitArr.length; index1++) {
            for (int index2 = index1 + 1; index2 < hitArr.length; index2++) {
                // Makes sure the gap is less than the current ship
                int gap = hitArr[index2] - hitArr[index1];
                if (gap <= length && isGapValid(hitArr[index1], hitArr[index2], rowOrCol)) {
                    int[] temp = new int[indices.length + gap - 1];
                    System.arraycopy(indices, 0, temp, 0, indices.length);
                    for (int i = hitArr[index1] + 1; i < hitArr[index2]; i++) {
                        temp[indices.length + i - (hitArr[index1] + 1)] = i;
                    }
                    indices = temp;
                }
            }
        }

        return indices;
    }

    /**
     * @param hitArr: array of hits in the row/col
     * @return the first and last index of the longest consecutive hits
     */
    private static int[] findLongestConsecutiveHits(int[] hitArr) {
        int[] longestConsecutiveHits = new int[2];
        int currentConsecutiveHits = 0;
        int longestConsecutiveHitsLength = 0;
        // Goes through each hit Counting the longest consecutive hits
        for (int i = 0; i < hitArr.length - 1; i++) {
            if (hitArr[i] + 1 == hitArr[i + 1]) {
                currentConsecutiveHits++;
                if (currentConsecutiveHits > longestConsecutiveHitsLength) {
                    longestConsecutiveHitsLength = currentConsecutiveHits;
                    longestConsecutiveHits[0] = hitArr[i + 1 - currentConsecutiveHits];
                    longestConsecutiveHits[1] = hitArr[i + 1];
                }
            } else {
                currentConsecutiveHits = 0;
            }
        }
        return longestConsecutiveHits;
    }


    /**
     * @param integerHashMap: HashMap of Integer / int[] pairs
     * @param key:            key to check
     * @param value:          value to add
     */
    private static void addToHashMap(HashMap<Integer, int[]> integerHashMap, int key, int value) {
        int[] temp;
        if (integerHashMap.containsKey(key)) {
            temp = integerHashMap.get(key);
            temp = Arrays.copyOf(temp, temp.length + 1);
            temp[temp.length - 1] = value;
        } else {
            temp = new int[1];
            temp[0] = value;
        }
        integerHashMap.put(key, temp);
    }


    private static String highestNumber(int[][] heatMap, char[][] guesses, int largestShip, int move) {
        int highestRaw = 0;
        int highestSum = 0;
        int bestRow = 0;
        int bestCol = 0;
        for (int row = 0; row < heatMap.length; row++) {
            for (int col = 0; col < heatMap[row].length; col++) {
                boolean change_value = false;
                // Stops it from guessing a spot that has already been guessed
                if (guesses[row][col] != '.')
                    continue;
                if (heatMap[row][col] > highestRaw) {
                    change_value = sumNeighbors(heatMap, row, col, largestShip) > highestSum;

                } else if (heatMap[row][col] == highestRaw) {
                    // for some reason works better when quadrant has more activity in it
                    change_value = tiebreaker(heatMap, bestRow, bestCol, row, col, largestShip);

                }
                if (change_value) {
                    highestRaw = heatMap[row][col];
                    highestSum = sumNeighborsStar(heatMap, row, col, largestShip);
                    bestRow = row;
                    bestCol = col;
                }
            }
        }
        return formatGuess(bestRow, bestCol);
    }

    // Adds up the neighboring values the best spot vs new spot and returns the best spot
    private static boolean tiebreaker(int[][] heatMap, int bestRow, int bestCol, int row, int col, int set_length) {
        int bestScore = sumNeighbors(heatMap, bestRow, bestCol, set_length);
        int newScore = sumNeighbors(heatMap, row, col, set_length);

        if (Math.abs(bestScore - newScore) < 3) {
            if (isEvenSquare(bestRow, bestCol) && !isEvenSquare(row, col)) {
                return false;
            } else if (!isEvenSquare(bestRow, bestCol) && isEvenSquare(row, col)) {
                return true;
            }
        }

        return bestScore <= newScore;
    }

    private static int sumNeighborsStar(int[][] heatMap, int row, int col, int set_length) {
        int sum = heatMap[row][col];
        for (int i = -set_length; i < set_length; i++) {
            if (i == 0)
                continue;
            if (row + i < 10 && row + i >= 0) {
                sum += heatMap[row + i][col];
            }
            if (col + i < 10 && col + i >= 0) {
                sum += heatMap[row][col + i];
            }
        }
        return sum;
    }

    // Best with box method
    private static int sumNeighbors(int[][] heatMap, int row, int col, int length) {
        int sum = 0;
        for (int rowOffset = -length; rowOffset <= length; rowOffset++) {
            for (int colOffset = -length; colOffset <= length; colOffset++) {
                if (row + rowOffset >= 0 && row + rowOffset < heatMap.length && col + colOffset >= 0 && col + colOffset < heatMap[0].length) {
                    sum += heatMap[row + rowOffset][col + colOffset];
                }
            }
        }

        // ((length * 2 + 1) * (length * 2 + 1)) by bit manipulation
        return sum;
    }

    private static int getQuadrant(int row, int col) {
        if (row < 5 && col < 5) {
            return 0;
        } else if (row < 5 && col >= 5) {
            return 1;
        } else if (row >= 5 && col < 5) {
            return 2;
        } else {
            return 3;
        }
    }

    // given a row and col returns if it is on a black square
    private static boolean isEvenSquare(int row, int col) {
        if (row + col == 0)
            return false;
        return (row + col) % 2 == 0;
    }

    /**
     * @param heatMap: the heat map to interment
     * @param row:     the row index
     * @param col:     the col index
     *                 it adds 1 to all the spots around the hit
     */
    private static void addToSurroundingSpots(int[][] heatMap, int row, int col) {
        int interment = 20;
        if (row - 1 >= 0 && heatMap[row - 1][col] != -1)
            heatMap[row - 1][col] += interment;
        if (row + 1 < heatMap.length && heatMap[row + 1][col] != -1)
            heatMap[row + 1][col] += interment;
        if (col - 1 >= 0 && heatMap[row][col - 1] != -1)
            heatMap[row][col - 1] += interment;
        if (col + 1 < heatMap.length && heatMap[row][col + 1] != -1)
            heatMap[row][col + 1] += interment;
    }

    /**
     * @param start:      start index
     * @param end:        end index
     * @param row_or_col: either a row or column of guesses
     * @return true if there are no hits in the gap
     */
    private static boolean isGapValid(int start, int end, char[] row_or_col) {
        if (start + 1 == end)
            return false;

        for (int i = start + 1; i < end; i++) {
            if (row_or_col[i] != '.')
                return false;
        }
        return true;
    }

    /**
     * @param row:      row index
     * @param col:      col index
     * @param length:   length of ship
     * @param guesses:  guesses board
     * @param dir_vert: true if vertical, false if horizontal
     * @return true if the ship can be placed
     */
    private static boolean checkValid(int row, int col, int length, char[][] guesses, boolean dir_vert) {
        for (int i = 0; i < length; i++) {
            if (dir_vert) {
                if (row + i >= guesses.length || (guesses[row + i][col] != '.' && guesses[row + i][col] != 'X')) {
                    return false;
                }
            } else {
                if (col + i >= guesses.length || (guesses[row][col + i] != '.' && guesses[row][col + i] != 'X')) {
                    return false;
                }
            }
        }
        return true;

    }

    /**
     * @param row:      row index
     * @param col:      col index
     * @param length:   length of ship
     * @param heatMap:  heat map to increment
     * @param dir_vert: true if vertical, false if horizontal
     */
    private static void intermentHeatMap(int row, int col, int length, int[][] heatMap, boolean dir_vert, int amount) {
        for (int i = 0; i < length; i++) {
            if (dir_vert) {
                heatMap[row + i][col] += amount;
            } else {
                heatMap[row][col + i] += amount;
            }
        }
    }


    /**
     * @param row: row index of guess
     * @param col: col index of guess
     * @return formatted guess in the form of "A1"
     */
    private static String formatGuess(int row, int col) {
        char a = (char) ((int) 'A' + row);
        return a + Integer.toString(col + 1);
    }

    /**
     * @param sunkShips: array of true or false for each ship
     * @return number of alive ships
     */
    private static int countAliveShips(boolean[] sunkShips) {
        int count = 0;
        for (boolean sunkShip : sunkShips) {
            if (!sunkShip) {
                count++;
            }
        }
        return count;
    }

    private static int getLargestShip(int[] aliveShips) {
        int largest = 0;
        for (int aliveShip : aliveShips) {
            if (aliveShip > largest) {
                largest = aliveShip;
            }
        }
        return largest;
    }

    /**
     * @param guesses: guesses board
     * @param colNum:  which column get contents of
     * @return array of contents of column
     */
    private static char[] getCol(char[][] guesses, int colNum) {
        char[] col = new char[guesses.length];
        for (int i = 0; i < guesses.length; i++) {
            col[i] = guesses[i][colNum];
        }
        return col;
    }

    public static void printBoard(char[][] board) {
        for (char[] chars : board) {
            for (char aChar : chars) {
                System.out.print(aChar + " ");
            }
            System.out.println();
        }
    }

    public static void printBoard(int[][] board) {
        for (int[] chars : board) {
            for (int aChar : chars) {
                System.out.print(aChar + " ");
            }
            System.out.println();
        }
    }

    public static void printHashMap(HashMap<Integer, int[]> hashMap) {
        for (int key : hashMap.keySet()) {
            System.out.print(key + ": ");
            for (int value : hashMap.get(key)) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }

}
