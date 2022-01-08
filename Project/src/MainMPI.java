import mpi.MPI;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class MainMPI {
    private static final int MASTER_PROCESS_RANK = 0;
    
    public static void main(String[] args) throws IOException {
        MPI.Init(args);
        int me = MPI.COMM_WORLD.Rank();
        if (me == MASTER_PROCESS_RANK) {
            searchMaster(State.readStateFromFile());
        }
        else { // worker process
            searchWorker();
        }
        MPI.Finalize();
    }

    private static Queue<State> generateStartingConfigurations(State root, int workerCount) {
        Queue<State> queue = new LinkedList<>();
        queue.add(root);

        while (true) {
            State queueHead = queue.peek();
            assert queueHead != null;
            if (queue.size() + queueHead.generateMoves().size() - 1 > workerCount) {
                break;
            }
            queue.poll();
            queue.addAll(queueHead.generateMoves());
        }

        return queue;
    }

    private static void searchMaster(State root) {
        int size = MPI.COMM_WORLD.Size();
        int workerCount = size - 1;
        int minBound = root.getManhattanDistance();
        boolean found = false;
        long time = System.currentTimeMillis();

        Queue<State> queue = generateStartingConfigurations(root, workerCount);

        while (!found) {
            Queue<State> temp = new LinkedList<>(queue);
            for (int i = 0; i < queue.size(); i++) { // send a starting state for each worker
                MPI.COMM_WORLD.Send(new Object[]{new StateDTO(temp.poll(), minBound, false)}, 0, 1, MPI.OBJECT, i + 1, 0);
            }

            Object[] pairs = new Object[size + 5];
            for (int i = 1; i <= queue.size(); i++) {
                MPI.COMM_WORLD.Recv(pairs, i - 1, 1, MPI.OBJECT, i, 0);
            }

            // check if any node found a solution
            int newMinBound = Integer.MAX_VALUE;
            for (int i = 0; i < queue.size(); i++) {
                Pair<Integer, State> p = (Pair<Integer, State>) pairs[i];
                if (p.getFirst() == -1) { // found solution
                    System.out.println("Solution found in " + p.getSecond().getStepCount() + " steps");
                    System.out.println("Solution is:\n" + p.getSecond());
                    System.out.println("Execution time: " + (System.currentTimeMillis() - time) + " ms");
                    found = true;
                    break;
                }
                else if (p.getFirst() < newMinBound) {
                    newMinBound = p.getFirst();
                }
            }
            if(!found){
                System.out.println("Current depth = " + newMinBound);
                minBound = newMinBound;
            }
        }

        for (int i = 1; i < size; i++) {
            MPI.COMM_WORLD.Send(new Object[]{new StateDTO(queue.poll(), minBound, true)}, 0, 1, MPI.OBJECT, i, 0);
        }
    }

    private static void searchWorker() {
        while (true) {
            Object[] received = new Object[2];
            MPI.COMM_WORLD.Recv(received, 0, 1, MPI.OBJECT, 0, 0);
            StateDTO receivedDTO = (StateDTO) received[0];
            if (receivedDTO.isFinished()) { // found solution => end
                return;
            }
            int minBound = receivedDTO.getBound();
            State current = receivedDTO.getState();
            Pair<Integer, State> result = search(current, current.getStepCount(), minBound);
            MPI.COMM_WORLD.Send(new Object[]{result}, 0, 1, MPI.OBJECT, 0, 0);
        }
    }

    public static Pair<Integer, State> search(State current, int stepCount, int bound) {
        int estimation = stepCount + current.getManhattanDistance();
        if (estimation > bound) {
            return new Pair<>(estimation, current);
        }
        if (estimation > 80) {
            return new Pair<>(estimation, current);
        }
        if (current.getManhattanDistance() == 0) {
            return new Pair<>(-1, current);
        }
        int min = Integer.MAX_VALUE;
        State solution = null;
        for (State next : current.generateMoves()) {
            Pair<Integer, State> result = search(next, stepCount + 1, bound);
            int moveCount = result.getFirst();
            if (moveCount == -1) {
                return new Pair<>(-1, result.getSecond());
            }
            if (moveCount < min) {
                min = moveCount;
                solution = result.getSecond();
            }
        }
        return new Pair<>(min, solution);
    }
}
