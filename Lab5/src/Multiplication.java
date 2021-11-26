import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Multiplication {
    private static final int THREAD_COUNT = 10;

    public static Polynomial regularSequentialMultiplication(Polynomial a, Polynomial b) {
        int resultDegree = a.getDegree() + b.getDegree();
        List<Integer> coefficients = new ArrayList<>();
        for (int i = 0; i <= resultDegree; i++) {
            coefficients.add(0);
        }

        for (int i = 0; i <= a.getDegree(); i++) {
            for (int j = 0; j <= b.getDegree(); j++) {
                coefficients.set(i + j, coefficients.get(i + j) + a.getCoefficients().get(i) * b.getCoefficients().get(j));
            }
        }

        Polynomial result = new Polynomial(coefficients);
        result.removeLeadingZeroes();
        return result;
    }

    public static Polynomial parallelSequentialMultiplication(Polynomial a, Polynomial b) throws InterruptedException {
        int resultDegree = a.getDegree() + b.getDegree();
        List<Integer> coefficients = new ArrayList<>();
        for (int i = 0; i <= resultDegree; i++) {
            coefficients.add(0);
        }

        int sizePerThread = Math.max((resultDegree + 1) / THREAD_COUNT, 1);
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        for (int i = 0; i <= resultDegree; i += sizePerThread) {
            executorService.execute(new SequentialSectionMultiplication(i, i + sizePerThread, a, b, coefficients));
        }

        executorService.shutdown();
        executorService.awaitTermination(2000, TimeUnit.MILLISECONDS);

        Polynomial result = new Polynomial(coefficients);
        result.removeLeadingZeroes();
        return result;
    }
}
