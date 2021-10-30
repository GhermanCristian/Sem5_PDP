import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final int THREAD_COUNT = 10;

    private static Matrix multiplyManualThreads(Matrix m1, Matrix m2) {
        Matrix result = new Matrix(m1.getRows(), m2.getColumns());
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

        threads.forEach(Thread::start);
        for (MultiplierThread thread : threads) {
            try {
                thread.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static void main(String[] args) {
        Matrix m1 = new Matrix(4, 3);
        Matrix m2 = new Matrix(3, 7);
        m1.fillMatrixRandomly();
        m2.fillMatrixRandomly();

        System.out.println(multiplyManualThreads(m1, m2));
    }
}
