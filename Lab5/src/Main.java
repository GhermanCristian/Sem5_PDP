import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        /*Polynomial x = new Polynomial(Arrays.asList(1, 2)); // 2x + 1
        Polynomial y = new Polynomial(Arrays.asList(4, -2)); // -2x + 4
        // 4X ^ 0 + 6X ^ 1 + -4X ^ 2*/

        Polynomial x = new Polynomial(Arrays.asList(1, 2, -2, 6, 3)); // 3x^4 + 6x^3 -2x^2 + 2x + 1
        Polynomial y = new Polynomial(Arrays.asList(4, -2, 3, 0, 7, 2, 1)); // x^6 + 2x^5 + 7x^4 + 3x^2 -2x + 4
        // 4X ^ 0 + 6X ^ 1 + -9X ^ 2 + 34X ^ 3 + 1X ^ 4 + 28X ^ 5 + 40X ^ 7 + 31X ^ 8 + 12X ^ 9 + 3X ^ 10
        System.out.println(Multiplication.regularSequentialMultiplication(x, y));
        System.out.println(Multiplication.regularParallelMultiplication(x, y));
        System.out.println(Multiplication.karatsubaSequentialMultiplication(x, y));
    }
}
