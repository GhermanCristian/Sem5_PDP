import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    private static final int THREAD_COUNT = 5;
    private static ExecutorService executorService;

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        State initialState = State.readStateFromFile();
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        solve(initialState);
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
    }

    public static void solve(State root) throws ExecutionException, InterruptedException {
        long startingTime = System.currentTimeMillis();
        int minBound = root.getManhattanDistance();
        while (true) {
            Pair<Integer, State> solution = searchParallel(root, 0, minBound, THREAD_COUNT);
            int distance = solution.getFirst();
            if (distance == -1) {
                System.out.println(solution.getSecond());
                System.out.printf("Steps: %d\nTime: %d ms\n ", solution.getSecond().getStepCount(), (System.currentTimeMillis() - startingTime));
                return;
            }
            minBound = distance;
        }
    }
    
    public static Pair<Integer, State> searchParallel(State currentState, int stepCount, int bound, int threadCount) throws ExecutionException, InterruptedException {
        if (threadCount <= 1) {
            return Common.search(currentState, stepCount, bound);
        }

        Pair<Integer, State> estimationCheck = Common.checkEstimation(currentState, stepCount, bound);
        if (estimationCheck != null) {
            return estimationCheck;
        }

        List<State> moves = currentState.generateMoves();

        List<Future<Pair<Integer, State>>> futures = new ArrayList<>();
        moves.forEach(nextMove -> futures.add(executorService.submit(
                () -> searchParallel(nextMove, stepCount + 1, bound, threadCount / moves.size())
        )));

        int min = Integer.MAX_VALUE;
        for (Future<Pair<Integer, State>> f : futures) {
            Pair<Integer, State> result = f.get();
            int t = result.getFirst();
            if (t == -1) { // propagate that a solution has already been found
                return new Pair<>(-1, result.getSecond());
            }
            if (t < min) {
                min = t;
            }
        }
        return new Pair<>(min, currentState);
    }
}
