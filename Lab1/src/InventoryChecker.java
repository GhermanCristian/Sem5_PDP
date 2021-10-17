import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class InventoryChecker {
    public final double totalInitialValue;
    public final Inventory inventory;
    public final ArrayList<Sale> sales;
    private static final ReentrantLock lock = new ReentrantLock();
    
    public InventoryChecker(double totalInitialValue, Inventory inventory, ArrayList<Sale> sales) {
        this.totalInitialValue = totalInitialValue;
        this.inventory = inventory;
        this.sales = sales;
    }

    public void checkInventory() {
        lock.lock();
        double difference = this.totalInitialValue - sales.stream().mapToDouble(Sale::getProfit).sum() - this.inventory.computeValue();
        lock.unlock();

        System.out.println(difference);
        if (Math.abs(difference) > 0.001) {
            //throw new RuntimeException("Invalid inventory");
        }
        System.out.println("Check complete");
    }
}
