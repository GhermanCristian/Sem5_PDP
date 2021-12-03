import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    private static final int THREAD_COUNT = 4;

    private static List<List<Integer>> getGraph1() {
        List<List<Integer>> graph = new ArrayList<>();
        List<Integer> neighbours0 = new ArrayList<>();
        neighbours0.add(1);
        List<Integer> neighbours1 = new ArrayList<>();
        neighbours1.add(2);
        neighbours1.add(4);
        List<Integer> neighbours2 = new ArrayList<>();
        neighbours2.add(3);
        List<Integer> neighbours3 = new ArrayList<>();
        neighbours3.add(1);
        neighbours3.add(4);
        List<Integer> neighbours4 = new ArrayList<>();
        neighbours4.add(0);
        graph.add(neighbours0);
        graph.add(neighbours1);
        graph.add(neighbours2);
        graph.add(neighbours3);
        graph.add(neighbours4);

        return graph;
    }

    public static void main(String[] args) throws InterruptedException {
        List<List<Integer>> graph = getGraph1();

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        AtomicBoolean foundHamiltonianCycle = new AtomicBoolean(false);
        List<Integer> output = new ArrayList<>();
        for(int i = 0; i < graph.size(); i++) {
            executorService.submit(new HamiltonianSearchTask(graph, i, foundHamiltonianCycle, output));
        }
        executorService.shutdown();
        executorService.awaitTermination(20, TimeUnit.SECONDS);

        System.out.println(output);
    }
}
