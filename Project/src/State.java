import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class State implements Serializable {
    private static final int[] DIRECTION_X = new int[]{0, -1, 0, 1};
    private static final int[] DIRECTION_Y = new int[]{-1, 0, 1, 0};
    private static final String[] movesStrings = new String[]{"LEFT", "UP", "RIGHT", "DOWN"};

    private final int[][] tiles;
    private final int stepCount;
    private final int freePosI;
    private final int freePosJ;

    private final State previousState;
    private final int manhattan;
    private final String move;

    public State(int[][] tiles, int freePosI, int freePosJ, int stepCount, State previousState, String move) {
        this.tiles = tiles;
        this.freePosI = freePosI;
        this.freePosJ = freePosJ;
        this.stepCount = stepCount;
        this.previousState = previousState;
        this.move = move;
        this.manhattan = manhattanDistance();
    }

    public static State readStateFromFile() throws IOException {
        int[][] tiles = new int[4][4];
        int freeI = -1, freeJ = -1;
        Scanner scanner = new Scanner(new File("input.txt"));
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                tiles[i][j] = scanner.nextInt();
                if (tiles[i][j] == 0) {
                    freeI = i;
                    freeJ = j;
                }
            }
        }
        scanner.close();
        return new State(tiles, freeI, freeJ, 0, null, "");
    }

    public int manhattanDistance() {
        int distance = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (tiles[i][j] != 0) {
                    int targetI = (tiles[i][j] - 1) / 4;
                    int targetJ = (tiles[i][j] - 1) % 4;
                    distance += Math.abs(i - targetI) + Math.abs(j - targetJ);
                }
            }
        }
        return distance;
    }

    public List<State> generateMoves() {
        List<State> moves = new ArrayList<>();
        for (int currentDirectionIndex = 0; currentDirectionIndex < 4; currentDirectionIndex++) {
            if (freePosI + DIRECTION_X[currentDirectionIndex] >= 0 && freePosI + DIRECTION_X[currentDirectionIndex] < 4 && freePosJ + DIRECTION_Y[currentDirectionIndex] >= 0 && freePosJ + DIRECTION_Y[currentDirectionIndex] < 4) {
                // try a new position
                int movedFreePosI = freePosI + DIRECTION_X[currentDirectionIndex];
                int movedFreePosJ = freePosJ + DIRECTION_Y[currentDirectionIndex];
                if (previousState != null && movedFreePosI == previousState.freePosI && movedFreePosJ == previousState.freePosJ) {
                    continue;
                }

                // make the actual move
                int[][] movedTiles = new int[4][4];
                for (int i = 0; i < 4; i++) {
                    System.arraycopy(tiles[i], 0, movedTiles[i], 0, 4);
                }
                movedTiles[freePosI][freePosJ] = movedTiles[movedFreePosI][movedFreePosJ];
                movedTiles[movedFreePosI][movedFreePosJ] = 0;

                moves.add(new State(movedTiles, movedFreePosI, movedFreePosJ, stepCount + 1, this, movesStrings[currentDirectionIndex]));
            }
        }
        return moves;
    }

    @Override
    public String toString() {
        State currentState = this;
        List<String> stringRepresentation = new ArrayList<>();
        while (currentState != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            sb.append(currentState.move);
            sb.append("\n");
            Arrays.stream(currentState.tiles).forEach(row -> sb.append(Arrays.toString(row)).append("\n"));
            stringRepresentation.add(sb.toString());
            currentState = currentState.previousState;
        }
        Collections.reverse(stringRepresentation);
        return String.join("", stringRepresentation) + "\nstepCount = " + stepCount;
    }

    public int getStepCount() {
        return stepCount;
    }

    public int getManhattanDistance() {
        return manhattan;
    }
}
