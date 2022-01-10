public class Common {
    private static final int MAX_MOVE_COUNT = 80;

    public static Pair<Integer, State> checkEstimation(State currentState, int stepCount, int bound) {
        int estimation = stepCount + currentState.getManhattanDistance();
        if (estimation > bound) {
            return new Pair<>(estimation, currentState);
        }
        if (estimation > MAX_MOVE_COUNT) {
            return new Pair<>(estimation, currentState);
        }
        if (currentState.getManhattanDistance() == 0) { // all pieces are where they are supposed to be => found solution
            return new Pair<>(-1, currentState);
        }
        return null;
    }

    public static Pair<Integer, State> search(State currentState, int stepCount, int bound) {
        Pair<Integer, State> estimationCheck = checkEstimation(currentState, stepCount, bound);
        if (estimationCheck != null) {
            return estimationCheck;
        }

        int minMoveCount = Integer.MAX_VALUE;
        State solution = null;
        for (State next : currentState.generateMoves()) {
            Pair<Integer, State> result = search(next, stepCount + 1, bound);
            int moveCount = result.getFirst();
            if (moveCount == -1) {
                return new Pair<>(-1, result.getSecond());
            }
            if (moveCount < minMoveCount) {
                minMoveCount = moveCount;
                solution = result.getSecond();
            }
        }
        return new Pair<>(minMoveCount, solution);
    }
}
