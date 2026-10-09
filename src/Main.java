import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            SudokuGUI gui = new SudokuGUI();
            gui.setVisible(true);
        });
    }
}