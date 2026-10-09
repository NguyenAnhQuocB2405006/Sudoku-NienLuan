public class SudokuSolver {

    public boolean solve(SudokuBoard board) {

        // Tim o trong
        for (int row = 0; row < 9; row++) {

            for (int col = 0; col < 9; col++) {

                if (board.getNumber(row, col) == 0) {

                    // Thu cac so tu 1 den 9
                    for (int number = 1; number <= 9; number++) {

                        if (board.isValid(row, col, number)) {

                            // Dat so vao o
                            board.setNumber(row, col, number);

                            // Tiep tuc giai
                            if (solve(board)) {
                                return true;
                            }

                            // Khong giai duoc thi quay lui
                            board.setNumber(row, col, 0);
                        }
                    }

                    return false;
                }
            }
        }

        return true;
    }
}