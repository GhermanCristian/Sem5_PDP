import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Polynomial x = new Polynomial(Arrays.asList(1, 2, 3));
        Polynomial y = new Polynomial(Arrays.asList(4, -1, -1));
        x.add(y);
        System.out.println(x);
    }
}
