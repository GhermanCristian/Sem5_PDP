import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Polynomial x = new Polynomial(Arrays.asList(1, 2)); // 2x + 1
        Polynomial y = new Polynomial(Arrays.asList(4, -2)); // -2x + 4
        System.out.println(Multiplication.regularSequentialMultiplication(x, y));
    }
}
