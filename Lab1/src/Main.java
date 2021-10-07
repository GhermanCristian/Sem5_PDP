import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    private static HashMap<Product, Integer> loadProductsWithQuantities() {
        HashMap<Product, Integer> productsWithQuantities = new HashMap<>();
        File file = new File("../products.txt");
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                productsWithQuantities.put(new Product(scanner.next(), scanner.nextDouble()), scanner.nextInt());
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return productsWithQuantities;
    }

    public static void main(String[] args) {
        HashMap<Product, Integer> products = loadProductsWithQuantities();

    }
}
