import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class Sale implements Runnable{
    private HashMap<Product, Integer> initialInventory;
    private HashMap<Product, Integer> productsToSale;
    private final ReentrantLock lock;
    private double profit;

    public HashMap<Product, Integer> getProductsToSale() {
        return this.productsToSale;
    }

    public Sale(HashMap<Product, Integer> initialInventory, HashMap<Product, Integer> productsToSale) {
        this.initialInventory = initialInventory;
        this.productsToSale = productsToSale;
        this.lock = new ReentrantLock();
    }

    private void removeProductOccurrencesFromInventory(Product productToSale) {
        if (! this.initialInventory.containsKey(productToSale)) {
            throw new RuntimeException("Product does not exist"); // automatically unlocks
        }

        int previousQuantity = this.initialInventory.get(productToSale);
        int productCountToSale = this.productsToSale.get(productToSale);

        if (productCountToSale > previousQuantity) {
            throw new RuntimeException("Not enough products"); // automatically unlocks
        }

        this.initialInventory.replace(productToSale, previousQuantity - productCountToSale);
        if (previousQuantity == productCountToSale) {
            this.initialInventory.remove(productToSale);
        }
    }

    private void computeProfit() {
        this.profit = 0d;
        this.productsToSale.forEach((product, quantity) -> this.profit += product.getPrice() * quantity);
    }

    public double getProfit() {
        return this.profit;
    }

    @Override
    public void run() {
        this.productsToSale.keySet().forEach(productToSale -> {
            this.lock.lock();
            this.removeProductOccurrencesFromInventory(productToSale);
            this.lock.unlock();
        });
        this.computeProfit();
    }
}
