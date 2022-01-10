import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class State implements Serializable {
    private static final int[] DIRECTION_X = new int[]{0, -1, 0, 1};
    private static final int[] DIRECTION_Y = new int[]{-1, 0, 1, 0};
    private static final String[] DIRECTIONS = new String[]{"LEFT", "UP", "RIGHT", "DOWN"};

    private final int[][] pieces;
    private final int freePosX;
    private final int freePosY;

    private final State previousState;
    private final int manhattanDistance;
    private final String move;
    private final int stepCount;

    public State(int[][] pieces, int freePosX, int freePosY, int stepCount, State previousState, String move) {
        this.pieces = pieces;
        this.freePosX = freePosX;
        this.freePosY = freePosY;
        this.stepCount = stepCount;
        this.previousState = previousState;
        this.move = move;
        this.manhattanDistance = this.computeManhattanDistance();
    }

    public static State readStateFromFile() throws IOException {
        int[][] pieces = new int[4][4];
        int freeX = -1;
        int freeY = -1;
        Scanner scanner = new Scanner(new File("input.txt"));
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                pieces[i][j] = scanner.nextInt();
                if (pieces[i][j] == 0) { // found the empty position
                    freeX = i;
                    freeY = j;
                }
            }
        }
        scanner.close();
        return new State(pieces, freeX, freeY, 0, null, "");
    }

    private int computeManhattanDistance() { // between a piece and the position it is supposed to be in (assume bottom right = target empty space)
        int distance = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (this.pieces[i][j] != 0) {
                    int targetI = (this.pieces[i][j] - 1) / 4;
                    int targetJ = (this.pieces[i][j] - 1) % 4;
                    distance += Math.abs(i - targetI) + Math.abs(j - targetJ);
                }
            }
        }
        return distance;
    }

    public List<State> generateMoves() {
        List<State> moves = new ArrayList<>();
        for (int currentDirectionIndex = 0; currentDirectionIndex < 4; currentDirectionIndex++) {
            if (freePosX + DIRECTION_X[currentDirectionIndex] >= 0 && freePosX + DIRECTION_X[currentDirectionIndex] < 4 && freePosY + DIRECTION_Y[currentDirectionIndex] >= 0 && freePosY + DIRECTION_Y[currentDirectionIndex] < 4) {
                // try a new position
                int movedFreePosX = freePosX + DIRECTION_X[currentDirectionIndex];
                int movedFreePosY = freePosY + DIRECTION_Y[currentDirectionIndex];

                // don't move s.t. it reaches the same state as before
                if (previousState != null && movedFreePosX == previousState.freePosX && movedFreePosY == previousState.freePosY) {
                    continue;
                }

                // make the actual move
                int[][] movedTiles = new int[4][4];
                for (int i = 0; i < 4; i++) {
                    System.arraycopy(this.pieces[i], 0, movedTiles[i], 0, 4);
                }
                movedTiles[freePosX][freePosY] = movedTiles[movedFreePosX][movedFreePosY];
                movedTiles[movedFreePosX][movedFreePosY] = 0;

                moves.add(new State(movedTiles, movedFreePosX, movedFreePosY, stepCount + 1, this, DIRECTIONS[currentDirectionIndex]));
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
            sb.append("\n").append(currentState.move).append("\n");
            Arrays.stream(currentState.pieces).forEach(row -> sb.append(Arrays.toString(row)).append("\n"));
            stringRepresentation.add(sb.toString());
            currentState = currentState.previousState;
        }
        Collections.reverse(stringRepresentation);
        return String.join("", stringRepresentation);
    }

    public int getStepCount() {
        return this.stepCount;
    }

    public int getManhattanDistance() {
        return this.manhattanDistance;
    }
}
