import mpi.MPI;
import java.util.Arrays;

public class Main {
    private static final int MASTER_PROCESS_RANK = 0;

    private static Polynomial multiplyRegular(Object o, Object o1, int begin, int end) {
        Polynomial p = (Polynomial) o;
        Polynomial q = (Polynomial) o1;
        Polynomial result = new Polynomial(p.getDegree() + q.getDegree());
        for (int i = begin; i < end; i++) {
            for (int j = 0; j < q.getCoefficients().size(); j++) {
                result.getCoefficients().set(i + j, result.getCoefficients().get(i + j) + p.getCoefficients().get(i) * q.getCoefficients().get(j));
            }
        }
        return result;
    }

    private static Polynomial buildResult(Object[] results) {
        int degree = ((Polynomial) results[0]).getDegree();
        Polynomial result = new Polynomial(degree);
        for (int i = 0; i < result.getCoefficients().size(); i++) {
            for (Object o : results) {
                result.getCoefficients().set(i, result.getCoefficients().get(i) + ((Polynomial) o).getCoefficients().get(i));
            }
        }
        return result;
    }

    private static void multiplyRegularWrapper(int rank) {
        System.out.printf("Worker %d started\n", rank);

        Object[] p = new Object[2];
        Object[] q = new Object[2];
        int[] begin = new int[1];
        int[] end = new int[1];

        MPI.COMM_WORLD.Recv(p, 0, 1, MPI.OBJECT, 0, 0);
        MPI.COMM_WORLD.Recv(q, 0, 1, MPI.OBJECT, 0, 0);
        MPI.COMM_WORLD.Recv(begin, 0, 1, MPI.INT, 0, 0);
        MPI.COMM_WORLD.Recv(end, 0, 1, MPI.INT, 0, 0);

        Polynomial result = multiplyRegular(p[0], q[0], begin[0], end[0]);

        MPI.COMM_WORLD.Send(new Object[]{result}, 0, 1, MPI.OBJECT, 0, 0);
    }

    private static void multiplicationMaster(Polynomial p, Polynomial q, int processCount, String type) {
        long startTime = System.currentTimeMillis();
        int start;
        int finish = 0;
        int len = Math.max(p.getDegree(), q.getDegree()) + 1 / (processCount - 1);

        for (int i = 1; i < processCount; i++) {
            start = finish;
            finish += len;
            if (i == processCount - 1) {
                finish = p.getDegree() + 1;
            }
            MPI.COMM_WORLD.Send(new Object[]{p}, 0, 1, MPI.OBJECT, i, 0);
            MPI.COMM_WORLD.Send(new Object[]{q}, 0, 1, MPI.OBJECT, i, 0);

            MPI.COMM_WORLD.Send(new int[]{start}, 0, 1, MPI.INT, i, 0);
            MPI.COMM_WORLD.Send(new int[]{finish}, 0, 1, MPI.INT, i, 0);
        }

        Object[] results = new Object[processCount - 1];
        for (int i = 1; i < processCount; i++) {
            MPI.COMM_WORLD.Recv(results, i - 1, 1, MPI.OBJECT, i, 0);
        }

        Polynomial result = buildResult(results);
        long endTime = System.currentTimeMillis();
        System.out.println(type + " multiplication of polynomials:\n" + result);
        System.out.println("Execution time: " + (endTime - startTime) + " ms");
    }

    public static void main(String[] args) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        if (rank == MASTER_PROCESS_RANK) {
            Polynomial x = new Polynomial(Arrays.asList(1, 2, -2, 6, 3)); // 3x^4 + 6x^3 -2x^2 + 2x + 1
            Polynomial y = new Polynomial(Arrays.asList(4, -2, 3, 0, 7, 2, 1)); // x^6 + 2x^5 + 7x^4 + 3x^2 -2x + 4
            // 4X ^ 0 + 6X ^ 1 + -9X ^ 2 + 34X ^ 3 + 1X ^ 4 + 28X ^ 5 + 40X ^ 7 + 31X ^ 8 + 12X ^ 9 + 3X ^ 10
            multiplicationMaster(x, y, size, "REGULAR");
        }
        else {
            multiplyRegularWrapper(rank);
        }

        MPI.Finalize();
    }
}
