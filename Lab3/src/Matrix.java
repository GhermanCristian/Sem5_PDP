public class Matrix {
    private int[][] matrix;
    private final int rows;
    private final int columns;

    public Matrix(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.matrix = new int[rows][columns];
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
}
