import java.util.Scanner;

public class SudokuGame {

    private SudokuBoard board;
    private SudokuSolver solver;
    private SudokuFile file;
    private Scanner scanner;

    public SudokuGame() {
        board = new SudokuBoard();
        solver = new SudokuSolver();
        file = new SudokuFile();
        scanner = new Scanner(System.in);
    }

    public void start() {

        int choice;

        do {
            System.out.println();
            System.out.println("===== TRO CHOI SUDOKU =====");

            board.display();

            System.out.println("1. Nhap so");
            System.out.println("2. Xoa so");
            System.out.println("3. Giai Sudoku");
            System.out.println("4. Tao van moi");
            System.out.println("5. Luu game");
            System.out.println("6. Tai game");
            System.out.println("0. Thoat");

            System.out.print("Chon: ");
            choice = scanner.nextInt();

            switch (choice) {

                case 1:
                    enterNumber();
                    break;

                case 2:
                    deleteNumber();
                    break;

                case 3:
                    solveSudoku();
                    break;

                case 4:
                    newGame();
                    break;

                case 5:
                    saveGame();
                    break;

                case 6:
                    loadGame();
                    break;

                case 0:
                    System.out.println("Ket thuc tro choi.");
                    break;

                default:
                    System.out.println("Lua chon khong hop le.");
            }

        } while (choice != 0);
    }

    private void enterNumber() {

        System.out.print("Nhap hang (1-9): ");
        int row = scanner.nextInt() - 1;

        System.out.print("Nhap cot (1-9): ");
        int col = scanner.nextInt() - 1;

        System.out.print("Nhap so (1-9): ");
        int number = scanner.nextInt();

        if (row < 0 || row >= 9 ||
                col < 0 || col >= 9) {

            System.out.println("Vi tri khong hop le.");
            return;
        }

        if (number < 1 || number > 9) {

            System.out.println("So phai tu 1 den 9.");
            return;
        }

        if (board.getNumber(row, col) != 0) {

            System.out.println("O nay da co so.");
            return;
        }

        if (!board.isValid(row, col, number)) {

            System.out.println("So bi trung trong hang, cot hoac o 3x3.");
            return;
        }

        board.setNumber(row, col, number);

        System.out.println("Da nhap so.");

        if (board.isFull()) {
            System.out.println("Ban da dien day ban Sudoku.");
        }
    }

    private void deleteNumber() {

        System.out.print("Nhap hang (1-9): ");
        int row = scanner.nextInt() - 1;

        System.out.print("Nhap cot (1-9): ");
        int col = scanner.nextInt() - 1;

        if (row < 0 || row >= 9 ||
                col < 0 || col >= 9) {

            System.out.println("Vi tri khong hop le.");
            return;
        }

        board.setNumber(row, col, 0);

        System.out.println("Da xoa so.");
    }

    private void solveSudoku() {

        boolean result = solver.solve(board);

        if (result) {
            System.out.println("Da giai Sudoku.");
        } else {
            System.out.println("Khong the giai Sudoku.");
        }
    }

    private void newGame() {

        int[][] data = {
                {5, 3, 0, 0, 7, 0, 0, 0, 0},
                {6, 0, 0, 1, 9, 5, 0, 0, 0},
                {0, 9, 8, 0, 0, 0, 0, 6, 0},

                {8, 0, 0, 0, 6, 0, 0, 0, 3},
                {4, 0, 0, 8, 0, 3, 0, 0, 1},
                {7, 0, 0, 0, 2, 0, 0, 0, 6},

                {0, 6, 0, 0, 0, 0, 2, 8, 0},
                {0, 0, 0, 4, 1, 9, 0, 0, 5},
                {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };

        board = new SudokuBoard(data);

        System.out.println("Da tao van Sudoku moi.");
    }

    private void saveGame() {

        file.save(board, "sudoku.txt");
    }

    private void loadGame() {

        SudokuBoard newBoard = file.load("sudoku.txt");

        if (newBoard != null) {
            board = newBoard;
        }
    }
}