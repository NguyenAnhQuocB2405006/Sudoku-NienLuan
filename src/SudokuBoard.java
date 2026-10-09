public class SudokuBoard {

    private int[][] board;

    public SudokuBoard() {
        board = new int[9][9];
    }

    public SudokuBoard(int[][] data) {
        board = new int[9][9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                board[i][j] = data[i][j];
            }
        }
    }

    public int getNumber(int row, int col) {
        return board[row][col];
    }

    public void setNumber(int row, int col, int number) {
        board[row][col] = number;
    }

    public boolean isValid(int row, int col, int number) {

        // Kiem tra hang
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == number) {
                return false;
            }
        }

        // Kiem tra cot
        for (int i = 0; i < 9; i++) {
            if (board[i][col] == number) {
                return false;
            }
        }

        // Kiem tra o 3x3
        int startRow = row - row % 3;
        int startCol = col - col % 3;

        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (board[i][j] == number) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean isFull() {

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }

        return true;
    }

    public void display() {

        System.out.println("-------------------------");

        for (int i = 0; i < 9; i++) {

            for (int j = 0; j < 9; j++) {

                if (board[i][j] == 0) {
                    System.out.print(". ");
                } else {
                    System.out.print(board[i][j] + " ");
                }

                if ((j + 1) % 3 == 0) {
                    System.out.print("| ");
                }
            }

            System.out.println();

            if ((i + 1) % 3 == 0) {
                System.out.println("-------------------------");
            }
        }
    }
}