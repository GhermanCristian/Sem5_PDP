import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    private static Inventory loadInventory() {
        Inventory inventory = new Inventory();
        File file = new File("products.txt");
        
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                inventory.addProduct(new Product(scanner.next(), scanner.nextDouble()), scanner.nextInt() * 10000);
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        return inventory;
    }

    private static Inventory generateInventorySubsets(Inventory inventory) {
        Random random = new Random();
        int productCount = random.nextInt(99) + 1;
        ArrayList<Product> productsAsArray = new ArrayList<>(inventory.getProducts());
        Inventory inventorySubset = new Inventory();
        
        for (int i = 0; i < productCount; i++) {
            boolean foundNewProduct = false;
            do {
                Product selectedProduct = productsAsArray.get(random.nextInt(productsAsArray.size() - 1));
                if (! inventorySubset.containsProduct(selectedProduct)) {
                    foundNewProduct = true;
                    int quantity = random.nextInt(99) + 1;
                    inventorySubset.addProduct(selectedProduct, quantity);
                }
            }
            while (!foundNewProduct);
        }
        
        return inventorySubset;
    }

    private static void run() {
        final int THREAD_COUNT = 1000;
        Inventory inventory = loadInventory();
        double totalInitialValue = inventory.computeValue();

        ArrayList<Sale> sales = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            sales.add(new Sale(inventory, generateInventorySubsets(inventory)));
        }

        InventoryChecker inventoryChecker = new InventoryChecker(totalInitialValue, inventory, sales);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                inventoryChecker.checkInventory();
            }
        }, 0, 1);

        ArrayList<Thread> threads = new ArrayList<>();
        sales.forEach(sale -> threads.add(new Thread(sale)));
        threads.forEach(Thread::start);
        for (Thread thread : threads) {
            try {
                thread.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        timer.cancel();
        try {
            Thread.sleep(1000); // ensure all running checks have finished
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("All sales have finished. Result of the final check - ");
        inventoryChecker.checkInventory();
    }

    public static void main(String[] args) {
        run();
    }
}
