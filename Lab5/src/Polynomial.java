import java.util.List;

public class Polynomial {
    private final List<Integer> coefficients;
    private final int degree;

    public Polynomial(List<Integer> coefficients) {
        this.coefficients = coefficients;
        this.degree = this.coefficients.size() - 1; // polynomial of degree 0 still has a coefficient
    }

    public void add(Polynomial other) {
        int minDegree = other.degree;
        if (this.degree < other.degree) {
            minDegree = this.degree;
            for (int i = this.degree + 1; i <= other.degree; i++) {
                this.coefficients.add(other.coefficients.get(i));
            }
        }

        for (int i = 0; i <= minDegree; i++) {
            this.coefficients.set(i, this.coefficients.get(i) + other.coefficients.get(i));
        }
    }

    public List<Integer> getCoefficients() {
        return this.coefficients;
    }

    public int getDegree() {
        return this.degree;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i <= this.degree; i++) {
            int coefficient = this.coefficients.get(i);
            if (coefficient != 0) {
                stringBuilder.append(coefficient).append("X ^ ").append(i).append(" ");
                if (i < this.degree) {
                    stringBuilder.append("+ ");
                }
            }
        }
        return stringBuilder.toString();
    }
}
