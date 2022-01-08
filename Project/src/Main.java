import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    private static final int THREAD_COUNT = 5;
    private static final int MAX_MOVE_COUNT = 80;
    private static ExecutorService executorService;

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        State initialState = State.readStateFromFile();
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        State solution = solve(initialState);
        System.out.println(solution);
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
    }

    public static State solve(State root) throws ExecutionException, InterruptedException {
        long time = System.currentTimeMillis();
        int minBound = root.getManhattanDistance();
        while (true) {
            Pair<Integer, State> solution = searchParallel(root, 0, minBound, THREAD_COUNT);
            int distance = solution.getFirst();
            if (distance == -1) {
                System.out.println("Solution found in " + solution.getSecond().getStepCount() + " steps");
                System.out.println("Execution time: " + (System.currentTimeMillis() - time) + " ms");
                return solution.getSecond();
            }
            minBound = distance;
        }
    }
    
    public static Pair<Integer, State> searchParallel(State currentState, int stepCount, int bound, int nrThreads) throws ExecutionException, InterruptedException {
        if (nrThreads <= 1) {
            return search(currentState, stepCount, bound);
        }

        int estimation = stepCount + currentState.getManhattanDistance();
        if (estimation > bound) {
            return new Pair<>(estimation, currentState);
        }
        if (estimation > MAX_MOVE_COUNT) {
            return new Pair<>(estimation, currentState);
        }
        if (currentState.getManhattanDistance() == 0) {
            return new Pair<>(-1, currentState);
        }
        
        int min = Integer.MAX_VALUE;
        List<State> moves = currentState.generateMoves();
        List<Future<Pair<Integer, State>>> futures = new ArrayList<>();
        for (State next : moves) {
            Future<Pair<Integer, State>> f = executorService.submit(
                    () -> searchParallel(next, stepCount + 1, bound, nrThreads / moves.size())
            );
            futures.add(f);
        }
        for (Future<Pair<Integer, State>> f : futures) {
            Pair<Integer, State> result = f.get();
            int t = result.getFirst();
            if (t == -1) {
                return new Pair<>(-1, result.getSecond());
            }
            if (t < min) {
                min = t;
            }
        }
        return new Pair<>(min, currentState);
    }

    public static Pair<Integer, State> search(State currentState, int stepCount, int bound) {
        int estimation = stepCount + currentState.getManhattanDistance();
        if (estimation > bound) {
            return new Pair<>(estimation, currentState);
        }
        if (estimation > MAX_MOVE_COUNT) {
            return new Pair<>(estimation, currentState);
        }
        if (currentState.getManhattanDistance() == 0) {
            return new Pair<>(-1, currentState);
        }
        int min = Integer.MAX_VALUE;
        State solution = null;
        for (State next : currentState.generateMoves()) {
            Pair<Integer, State> result = search(next, stepCount + 1, bound);
            int t = result.getFirst();
            if (t == -1) {
                return new Pair<>(-1, result.getSecond());
            }
            if (t < min) {
                min = t;
                solution = result.getSecond();
            }
        }
        return new Pair<>(min, solution);
    }
}
