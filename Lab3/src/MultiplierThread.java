public class MultiplierThread extends Thread {
    private final Matrix m1;
    private final Matrix m2;
    private Matrix result;
    private final int row;
    private final int column;
    private final int positionsToFill;

    private void computeProductOnPosition(int x, int y) {
        int sum = 0;
        for (int c = 0; c < this.m1.getColumns(); c++) {
            sum += this.m1.getElementOnPosition(x, c) * this.m2.getElementOnPosition(c, y);
        }
        this.result.setElementOnPosition(x, y, sum);
    }

    public MultiplierThread(Matrix m1, Matrix m2, Matrix result, int row, int column, int positionsToFill) {
        if (m1.getColumns() != m2.getRows() || m1.getRows() != result.getRows() || m2.getColumns() != result.getColumns()) {
            throw new ArithmeticException("Invalid matrix sizes");
        }
        this.m1 = m1;
        this.m2 = m2;
        this.result = result;
        this.row = row;
        this.column = column;
        this.positionsToFill = positionsToFill;
    }
    
    @Override
    public void run() {
        int currentRow = this.row;
        int currentColumn = this.column;
        for (int pos = 0; pos < this.positionsToFill; pos++) {
            this.computeProductOnPosition(currentRow, currentColumn);

            currentColumn++;
            if (currentColumn >= this.result.getColumns()) {
                currentColumn = 0;
                currentRow++;
            }
        }
    }
}
