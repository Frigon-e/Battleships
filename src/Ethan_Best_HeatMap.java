import java.util.Arrays;
import java.util.HashMap;


public class Ethan_Best_HeatMap {
    public static String makeGuess(char[][] guesses) {
        boolean[] sunkShips = {false, false, false, false, false};
        int[][] HeatMap = new int[10][10];
        int[] allShipLengths = new int[]{2, 3, 3, 4, 5};
        int[] quadrant = new int[]{0, 0, 0, 0};
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
                if (guesses[row][col] != '.') {
                    move++;
                    if (row < 5 && col < 5)
                        quadrant[0]++;
                    else if (row < 5 && col >= 5)
                        quadrant[1]++;
                    else if (row >= 5 && col < 5)
                        quadrant[2]++;
                    else
                        quadrant[3]++;
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
        for (int length : aliveShipLengths) {
            for (int row = 0; row < guesses.length; row++) {
                for (int col = 0; col < guesses[row].length; col++) {
                    for (boolean dir_vert : boolArr) {
                        if (checkValid(row, col, length, guesses, dir_vert)) {
                            intermentHeatMap(row, col, length, HeatMap, dir_vert);
                        }
                    }
                }
            }
        }

        return highestNumber(HeatMap, guesses, quadrant, move);
    }

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
                // Returns all consecutive indices of '.' between hits in the row/col
                int[] indices = findGapIndices(row_or_col, hit_arr, length);
                if (indices.length > 0) {
                    if (isRow) {
                        heatMap[key][indices[0]] = 100;
                    } else {
                        heatMap[indices[0]][key] = 100;
                    }
                }

                int[] longestConsecutiveHits = findLongestConsecutiveHits(hit_arr);
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

    // Returns the longest consecutive hits in a row or column
    // returns the indices of the first and last hit
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


    private static String highestNumber(int[][] heatMap, char[][] guesses, int[] quadrant, int move) {
        int highest = 0;
        int bestRow = 0;
        int bestCol = 0;
        for (int row = 0; row < heatMap.length; row++) {
            for (int col = 0; col < heatMap[row].length; col++) {
                // Stops it from guessing a spot that has already been guessed
                if (guesses[row][col] != '.')
                    continue;
                if (heatMap[row][col] > highest) {
                    highest = heatMap[row][col];
                    bestRow = row;
                    bestCol = col;
                } else if (heatMap[row][col] == highest) {
                    boolean change_value;
                    // for some reason works better when quadrant has more activity in it
                    if (move <= 7) {
                        change_value = quadrant[getQuadrant(row, col)] > quadrant[getQuadrant(bestRow, bestCol)];
                    } else {
                        change_value = tiebreaker(heatMap, bestRow, bestCol, row, col, 5);
                    }
                    if (change_value) {
                        bestRow = row;
                        bestCol = col;
                    }
                }
            }
        }
        return formatGuess(bestRow, bestCol);
    }

    // Adds up the neighboring values the best spot vs new spot and returns the best spot
    private static boolean tiebreaker(int[][] heatMap, int bestRow, int bestCol, int row, int col, int set_length) {
        int bestScore = sumNeighbors(heatMap, bestRow, bestCol, set_length);
        int newScore = sumNeighbors(heatMap, row, col, set_length);

        boolean output = bestScore <= newScore;
        if ((bestScore == newScore) && set_length > 3) {
            // best min length 3
            output = tiebreaker(heatMap, bestRow, bestCol, row, col, Math.max(set_length - 1, 2));
        }

        return output;
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

    // Makes sure the gap is only empty spaces
    private static boolean isGapValid(int start, int end, char[] row_or_col) {
        if (start + 1 == end)
            return false;

        for (int i = start + 1; i < end; i++) {
            if (row_or_col[i] != '.')
                return false;
        }
        return true;
    }

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

    private static void intermentHeatMap(int row, int col, int length, int[][] heatMap, boolean dir_vert) {
        for (int i = 0; i < length; i++) {
            if (dir_vert) {
                heatMap[row + i][col]++;
            } else {
                heatMap[row][col + i]++;
            }
        }
    }


    private static String formatGuess(int row, int col) {
        char a = (char) ((int) 'A' + row);
        return a + Integer.toString(col + 1);
    }

    private static int countAliveShips(boolean[] sunkShips) {
        int count = 0;
        for (boolean sunkShip : sunkShips) {
            if (!sunkShip) {
                count++;
            }
        }
        return count;
    }

    private static char[] getCol(char[][] guesses, int key) {
        char[] col = new char[guesses.length];
        for (int i = 0; i < guesses.length; i++) {
            col[i] = guesses[i][key];
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
