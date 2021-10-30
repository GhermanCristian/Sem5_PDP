import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final int THREAD_COUNT = 10;

    private static void multiplyManualThreads(List<MultiplierThread> tasks, Matrix result) {
        tasks.forEach(Thread::start);
        for (MultiplierThread thread : tasks) {
            try {
                thread.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(result);
        result.reset();
    }

    private static void multiplyThreadPool(List<MultiplierThread> tasks, Matrix result) {
        ExecutorService service = Executors.newFixedThreadPool(THREAD_COUNT);
        tasks.forEach(service::submit);
        try {
            if (! service.awaitTermination(1, TimeUnit.SECONDS)) {
                service.shutdownNow();
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(result);
        result.reset();
    }

    private static List<MultiplierThread> generateTasks(Matrix m1, Matrix m2, Matrix result) {
        List<MultiplierThread> threads = new ArrayList<>();
        int positions = result.getColumns() * result.getRows();
        int basePositionsPerThread = positions / THREAD_COUNT;
        int threadsWithAnExtraPosition = positions % THREAD_COUNT;
        int coveredPositions = 0;

        for (int threadIndex = 0; threadIndex < THREAD_COUNT; threadIndex++) {
            int positionsForCurrentThread = basePositionsPerThread;
            if (threadIndex < threadsWithAnExtraPosition) {
                positionsForCurrentThread++;
            }
            threads.add(new MultiplierThread(m1, m2, result, coveredPositions / result.getColumns(), coveredPositions % result.getColumns(), positionsForCurrentThread));
            coveredPositions += positionsForCurrentThread;
        }

        return threads;
    }

    public static void main(String[] args) {
        Matrix m1 = new Matrix(4, 3);
        Matrix m2 = new Matrix(3, 7);
        Matrix result = new Matrix(m1.getRows(), m2.getColumns());
        m1.fillMatrixRandomly();
        m2.fillMatrixRandomly();

        List<MultiplierThread> tasks = generateTasks(m1, m2, result);
        multiplyManualThreads(tasks, result);
        multiplyThreadPool(tasks, result);
    }
}
