import mpi.MPI;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
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
            List<State> generatedMoves = queueHead.generateMoves();
            if (queue.size() + generatedMoves.size() - 1 > workerCount) {
                break;
            }
            queue.poll();
            queue.addAll(generatedMoves);
        }

        return queue;
    }

    private static void searchMaster(State root) {
        int size = MPI.COMM_WORLD.Size();
        int workerCount = size - 1;
        int minBound = root.getManhattanDistance();
        boolean found = false;
        long startingTime = System.currentTimeMillis();

        Queue<State> queue = generateStartingConfigurations(root, workerCount);

        while (!found) {
            Queue<State> queueCopy = new LinkedList<>(queue);
            for (int i = 0; i < queue.size(); i++) { // send a starting state for each worker
                MPI.COMM_WORLD.Send(new Object[]{new StateDTO(queueCopy.poll(), minBound, false)}, 0, 1, MPI.OBJECT, i + 1, 0);
            }

            Object[] pairs = new Object[size + 1];
            for (int i = 1; i <= queue.size(); i++) {
                MPI.COMM_WORLD.Recv(pairs, i - 1, 1, MPI.OBJECT, i, 0);
            }

            // check if any node found a solution
            int newMinBound = Integer.MAX_VALUE;
            for (int i = 0; i < queue.size(); i++) {
                Pair<Integer, State> currentResponse = (Pair<Integer, State>) pairs[i];
                if (currentResponse.getFirst() == -1) { // found solution
                    System.out.println("Solution :\n" + currentResponse.getSecond());
                    System.out.printf("Steps = %d\nTime = %d ms\n", currentResponse.getSecond().getStepCount(), (System.currentTimeMillis() - startingTime));
                    found = true;
                    break;
                }
                else if (currentResponse.getFirst() < newMinBound) {
                    newMinBound = currentResponse.getFirst();
                }
            }
            if(!found){
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
            State currentState = receivedDTO.getState();
            Pair<Integer, State> result = Common.search(currentState, currentState.getStepCount(), minBound);
            MPI.COMM_WORLD.Send(new Object[]{result}, 0, 1, MPI.OBJECT, 0, 0);
        }
    }
}
