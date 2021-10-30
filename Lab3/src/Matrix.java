import java.util.Arrays;
import java.util.Random;

public class Matrix {
    private int[][] matrix;
    private final int rows;
    private final int columns;

    private final int MAX_INITIAL_VALUE = 100;
    private final int MIN_INITIAL_VALUE = 10;

    public Matrix(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.matrix = new int[rows][columns];
    }

    public void fillMatrixRandomly() {
        Random randomGenerator = new Random();
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                this.matrix[i][j] = randomGenerator.nextInt(this.MAX_INITIAL_VALUE - this.MIN_INITIAL_VALUE) + this.MIN_INITIAL_VALUE;
            }
        }
    }

    public void reset() {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                this.matrix[i][j] = 0;
            }
        }
    }

    public int getRows() {
        return this.rows;
    }

    public int getColumns() {
        return this.columns;
    }

    public int getElementOnPosition(int x, int y) {
        if (x < 0 || x >= this.rows || y < 0 || y >= this.columns) {
            throw new IndexOutOfBoundsException("Invalid coordinates");
        }
        return this.matrix[x][y];
    }

    public void setElementOnPosition(int x, int y, int newValue) {
        if (x < 0 || x >= this.rows || y < 0 || y >= this.columns) {
            throw new IndexOutOfBoundsException("Invalid coordinates");
        }
        this.matrix[x][y] = newValue;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < this.rows; i++) {
            result.append(Arrays.toString(this.matrix[i])).append("\n");
        }
        return result.toString();
    }
}
