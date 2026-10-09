import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class SudokuFile {

    public void save(SudokuBoard board, String fileName) {

        try {
            FileWriter writer = new FileWriter(fileName);

            for (int i = 0; i < 9; i++) {

                for (int j = 0; j < 9; j++) {
                    writer.write(board.getNumber(i, j) + " ");
                }

                writer.write("\n");
            }

            writer.close();

            System.out.println("Da luu game.");

        } catch (IOException e) {

            System.out.println("Khong the luu game.");
        }
    }

    public SudokuBoard load(String fileName) {

        SudokuBoard board = new SudokuBoard();

        try {
            Scanner scanner = new Scanner(new File(fileName));

            for (int i = 0; i < 9; i++) {

                for (int j = 0; j < 9; j++) {

                    int number = scanner.nextInt();

                    board.setNumber(i, j, number);
                }
            }

            scanner.close();

            System.out.println("Da tai game.");

            return board;

        } catch (Exception e) {

            System.out.println("Khong the tai game.");

            return null;
        }
    }
}