import java.util.ArrayList;
import java.util.List;

public class Multiplication {
    public static Polynomial regularSequentialMultiplication(Polynomial a, Polynomial b) {
        int resultDegree = a.getDegree() + b.getDegree();
        List<Integer> coefficients = new ArrayList<>();
        for (int i = 0; i <= resultDegree; i++) {
            coefficients.add(0);
        }

        for (int i = 0; i <= a.getDegree(); i++) {
            for (int j = 0; j <= b.getDegree(); j++) {
                coefficients.set(i + j, coefficients.get(i + j) + a.getCoefficients().get(i) * b.getCoefficients().get(j));
            }
        }

        Polynomial result = new Polynomial(coefficients);
        result.removeLeadingZeroes();
        return result;
    }
}
