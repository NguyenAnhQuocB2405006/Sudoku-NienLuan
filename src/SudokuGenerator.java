import java.util.Random;

public class SudokuGenerator {

    private Random random = new Random();

    private SudokuBoard solution;

    public SudokuBoard createBoard() {
        return createBoard(45);
    }

    public SudokuBoard createBoard(int removeCount) {

        SudokuBoard board = new SudokuBoard();

        // Tao bang Sudoku day du
        createSolution(board);

        // Luu lai dap an day du
        solution = new SudokuBoard();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {

                solution.setNumber(
                        i,
                        j,
                        board.getNumber(i, j)
                );
            }
        }

        // Xoa mot so o
        removeNumbers(
                board,
                removeCount
        );

        return board;
    }

    public SudokuBoard getSolution() {
        return solution;
    }

    private boolean createSolution(
            SudokuBoard board
    ) {

        for (int row = 0;
             row < 9;
             row++) {

            for (int col = 0;
                 col < 9;
                 col++) {

                if (board.getNumber(
                        row,
                        col
                ) == 0) {

                    int[] numbers =
                            createNumbers();

                    for (int i = 0;
                         i < 9;
                         i++) {

                        int number =
                                numbers[i];

                        if (board.isValid(
                                row,
                                col,
                                number
                        )) {

                            board.setNumber(
                                    row,
                                    col,
                                    number
                            );

                            if (createSolution(
                                    board
                            )) {

                                return true;
                            }

                            // Quay lui
                            board.setNumber(
                                    row,
                                    col,
                                    0
                            );
                        }
                    }

                    return false;
                }
            }
        }

        return true;
    }

    private int[] createNumbers() {

        int[] numbers = {
                1, 2, 3,
                4, 5, 6,
                7, 8, 9
        };

        // Tron ngau nhien
        for (int i = 0;
             i < 9;
             i++) {

            int j =
                    random.nextInt(9);

            int temp =
                    numbers[i];

            numbers[i] =
                    numbers[j];

            numbers[j] =
                    temp;
        }

        return numbers;
    }

    private void removeNumbers(
            SudokuBoard board,
            int removeCount
    ) {

        int count = 0;

        while (count < removeCount) {

            int row =
                    random.nextInt(9);

            int col =
                    random.nextInt(9);

            if (board.getNumber(
                    row,
                    col
            ) != 0) {

                board.setNumber(
                        row,
                        col,
                        0
                );

                count++;
            }
        }
    }
}