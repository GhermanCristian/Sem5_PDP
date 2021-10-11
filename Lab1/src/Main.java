import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static HashMap<Product, Integer> loadProductsWithQuantities() {
        HashMap<Product, Integer> productsWithQuantities = new HashMap<>();
        File file = new File("products.txt");
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                productsWithQuantities.put(new Product(scanner.next(), scanner.nextDouble()), scanner.nextInt() * 100);
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return productsWithQuantities;
    }

    private static HashMap<Product, Integer> generateProductsToSale(HashMap<Product, Integer> allProducts) {
        Random random = new Random();
        int productCount = random.nextInt(9) + 1;
        ArrayList<Product> productsAsArray = new ArrayList<>(allProducts.keySet());
        HashMap<Product, Integer> productsToSale = new HashMap<>();
        for (int i = 0; i < productCount; i++) {
            boolean foundNewProduct = false;
            do {
                Product selectedProduct = productsAsArray.get(random.nextInt(productsAsArray.size() - 1));
                if (! productsToSale.containsKey(selectedProduct)) {
                    foundNewProduct = true;
                    int quantity = random.nextInt(9) + 1;
                    productsToSale.put(selectedProduct, quantity);
                }
            }
            while (!foundNewProduct);
        }
        return productsToSale;
    }

    public static void main(String[] args) {
        final int THREAD_COUNT = 5;
        HashMap<Product, Integer> products = loadProductsWithQuantities();
        ArrayList<Sale> sales = new ArrayList<>();
        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 0; i < THREAD_COUNT; i++) {
            sales.add(new Sale(products, generateProductsToSale(products)));
        }

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
    }
}
