import java.util.concurrent.locks.ReentrantLock;

public class Sale implements Runnable {
    private Inventory inventory;
    private final Inventory inventorySubset;
    private double profit;
    private final ReentrantLock lock;

    public Sale(Inventory inventory, Inventory inventorySubset) {
        this.inventory = inventory;
        this.inventorySubset = inventorySubset;
        this.profit = 0d;
        this.lock = new ReentrantLock();
    }

    private void sellProduct(Product productForSale) {
        this.lock.lock();
        int quantity = this.inventorySubset.getQuantityOfProduct(productForSale);
        this.inventory.removeProduct(productForSale, quantity);
        this.profit += productForSale.getPrice() * quantity;
        this.lock.unlock();
    }

    public double getProfit() {
        return this.profit;
    }

    @Override
    public void run() {
        this.inventorySubset.getProducts().forEach(this::sellProduct);
    }
}
