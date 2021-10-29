import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    private static final int NULL = -1;

    private static int partialResult = NULL; // such that the producer starts first
    private static final ReentrantLock lock = new ReentrantLock();
    private static final Condition readyToSendProduct = lock.newCondition();
    private static final Condition readyToReceiveProduct = lock.newCondition();

    public static void main(String[] args) {
        ArrayList<Integer> v1 = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6));
        ArrayList<Integer> v2 = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6));

        Thread producer = new Thread(() -> {
            int size = v1.size(); // should be the same for v2
            for (int i = 0; i < size; i++) {
                lock.lock();
                try {
                    while (partialResult != NULL) { // can't send the product because the previous one hasn't been processed
                        readyToSendProduct.await();
                    }
                    partialResult = v1.get(i) * v2.get(i);
                    readyToReceiveProduct.signal(); // tell the consumer to prepare to receive a new product
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    lock.unlock();
                }
            }
        });
        Thread consumer = new Thread(() -> {
            int size = v1.size(); // should be the same for v2
            int totalSum = 0;
            for (int i = 0; i < size; i++) {
                try {
                    lock.lock();
                    while (partialResult == NULL) { // can't receive a product because it's still computing on the producer side
                        readyToReceiveProduct.await();
                    }
                    totalSum += partialResult;
                    partialResult = NULL;
                    readyToSendProduct.signal(); // tell the producer that it can send a new product
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    lock.unlock();
                }
            }
            System.out.println(totalSum);
        });

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
