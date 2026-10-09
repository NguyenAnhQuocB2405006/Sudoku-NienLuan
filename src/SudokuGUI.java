import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class SudokuGUI extends JFrame {

    private JTextField[][] cells;

    private SudokuBoard board;
    private SudokuBoard solution;

    private SudokuSolver solver;
    private SudokuFile file;
    private SudokuGenerator generator;

    private JTextArea moveArea;

    private JLabel timeLabel;
    private JLabel errorLabel;
    private JLabel scoreLabel;
    private JLabel hintLabel;
    private JLabel trophyLabel;

    private JComboBox<String> difficultyBox;

    private Timer timer;

    private int seconds = 0;

    private int errorCount = 0;

    private int score = 0;
    private int nextPoint = 1;

    // So luot goi y da su dung trong van hien tai
    private int hintCount = 0;

    private ArrayList<String> moves;

    private boolean updatingBoard = false;

    // Danh dau van choi da giai xong de khong hien hop thoai lap lai
    private boolean gameFinished = false;

    // Danh dau o dang bi khoa do nhap sai
    private boolean[][] errorLocked;

    // Mau sac
    private Color normalColor =
            Color.WHITE;

    private Color fixedColor =
            new Color(235, 235, 235);

    private Color correctColor =
            new Color(170, 210, 255);

    private Color wrongColor =
            new Color(255, 150, 150);

    private Color relatedColor =
            new Color(220, 235, 255);


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SudokuGUI() {

        board = new SudokuBoard();

        solver = new SudokuSolver();

        file = new SudokuFile();

        generator = new SudokuGenerator();

        moves = new ArrayList<>();

        errorLocked =
                new boolean[9][9];

        setTitle("Sudoku");

        setSize(
                1250,
                850
        );

        setMinimumSize(
                new Dimension(
                        950,
                        700
                )
        );

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLocationRelativeTo(null);

        createInterface();

        startTimer();
    }


    // =========================================================
    // TAO GIAO DIEN
    // =========================================================

    private void createInterface() {

        JPanel mainPanel =
                new JPanel(
                        new BorderLayout(
                                20,
                                15
                        )
                );

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        20,
                        15,
                        20
                )
        );


        // -----------------------------------------------------
        // TIEU DE
        // -----------------------------------------------------

        JLabel title =
                new JLabel(
                        "SUDOKU",
                        JLabel.CENTER
                );

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        32
                )
        );

        mainPanel.add(
                title,
                BorderLayout.NORTH
        );


        // -----------------------------------------------------
        // KHU VUC CHINH
        // -----------------------------------------------------

        JPanel centerPanel =
                new JPanel(
                        new BorderLayout(
                                30,
                                0
                        )
                );


        // =====================================================
        // BAN SUDOKU
        // =====================================================

        JPanel boardContainer =
                new JPanel(
                        new GridBagLayout()
                );

        boardContainer.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        10,
                        10,
                        10
                )
        );

        JPanel boardPanel =
                new JPanel(
                        new GridLayout(
                                9,
                                9
                        )
                );

        boardPanel.setPreferredSize(
                new Dimension(
                        570,
                        570
                )
        );

        boardPanel.setMinimumSize(
                new Dimension(
                        450,
                        450
                )
        );

        cells =
                new JTextField[9][9];


        // -----------------------------------------------------
        // TAO 81 O
        // -----------------------------------------------------

        for (int row = 0;
             row < 9;
             row++) {

            for (int col = 0;
                 col < 9;
                 col++) {

                JTextField cell =
                        new JTextField();

                cells[row][col] =
                        cell;

                cell.setHorizontalAlignment(
                        JTextField.CENTER
                );

                cell.setFont(
                        new Font(
                                "Arial",
                                Font.BOLD,
                                24
                        )
                );

                cell.setBackground(
                        normalColor
                );


                // -------------------------------------------------
                // VIEN O
                // -------------------------------------------------

                int top = 1;
                int left = 1;
                int bottom = 1;
                int right = 1;

                if (row % 3 == 0) {
                    top = 3;
                }

                if (col % 3 == 0) {
                    left = 3;
                }

                if (row == 8) {
                    bottom = 3;
                }

                if (col == 8) {
                    right = 3;
                }

                Border border =
                        BorderFactory.createMatteBorder(
                                top,
                                left,
                                bottom,
                                right,
                                Color.BLACK
                        );

                cell.setBorder(border);


                final int r = row;
                final int c = col;


                // -------------------------------------------------
                // DOCUMENT LISTENER
                // -------------------------------------------------

                cell.getDocument()
                        .addDocumentListener(
                                new DocumentListener() {

                                    public void insertUpdate(
                                            DocumentEvent e
                                    ) {
                                        checkInput(
                                                r,
                                                c
                                        );
                                    }

                                    public void removeUpdate(
                                            DocumentEvent e
                                    ) {
                                        checkInput(
                                                r,
                                                c
                                        );
                                    }

                                    public void changedUpdate(
                                            DocumentEvent e
                                    ) {
                                        checkInput(
                                                r,
                                                c
                                        );
                                    }
                                }
                        );


                // -------------------------------------------------
                // KHI O SAI CHI DUOC XOA
                // -------------------------------------------------

                cell.addKeyListener(
                        new KeyAdapter() {

                            @Override
                            public void keyPressed(
                                    KeyEvent e
                            ) {

                                if (!errorLocked[r][c]) {
                                    return;
                                }

                                if (e.getKeyCode()
                                        == KeyEvent.VK_BACK_SPACE
                                        ||
                                        e.getKeyCode()
                                                == KeyEvent.VK_DELETE) {

                                    // Chi cho phep xoa; checkInput se mo khoa
                                    // sau khi o thuc su rong.
                                    return;
                                }

                                e.consume();
                            }
                        }
                );


                // -------------------------------------------------
                // KHONG CHO CLICK SANG O KHAC KHI DANG LOI
                // -------------------------------------------------

                cell.addMouseListener(
                        new MouseAdapter() {

                            @Override
                            public void mousePressed(
                                    MouseEvent e
                            ) {

                                // Neu click vao chinh o sai
                                // thi cho phep
                                if (errorLocked[r][c]) {
                                    return;
                                }

                                int[] lockedCell =
                                        getLockedCell();

                                if (lockedCell != null) {

                                    JOptionPane.showMessageDialog(
                                            SudokuGUI.this,
                                            "Please delete the wrong number first!"
                                    );

                                    final int lockedRow =
                                            lockedCell[0];

                                    final int lockedCol =
                                            lockedCell[1];

                                    SwingUtilities.invokeLater(
                                            () -> {

                                                cells[lockedRow]
                                                        [lockedCol]
                                                        .requestFocusInWindow();

                                                cells[lockedRow]
                                                        [lockedCol]
                                                        .setCaretPosition(
                                                                cells[lockedRow]
                                                                        [lockedCol]
                                                                        .getText()
                                                                        .length()
                                                        );
                                            }
                                    );

                                    e.consume();
                                }
                            }
                        }
                );


                boardPanel.add(cell);
            }
        }

        boardContainer.add(
                boardPanel
        );

        centerPanel.add(
                boardContainer,
                BorderLayout.CENTER
        );


        // =====================================================
        // PANEL BEN PHAI
        // =====================================================

        JPanel rightPanel =
                new JPanel(
                        new BorderLayout(
                                10,
                                10
                        )
                );

        rightPanel.setPreferredSize(
                new Dimension(
                        330,
                        570
                )
        );

        rightPanel.setMinimumSize(
                new Dimension(
                        300,
                        500
                )
        );

        rightPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        210,
                                        210,
                                        210
                                )
                        ),
                        BorderFactory.createEmptyBorder(
                                15,
                                15,
                                15,
                                15
                        )
                )
        );


        // =====================================================
        // THONG TIN
        // =====================================================

        JPanel infoPanel =
                new JPanel(
                        new GridLayout(
                                6,
                                1,
                                5,
                                8
                        )
                );


        // -----------------------------------------------------
        // THOI GIAN
        // -----------------------------------------------------

        timeLabel =
                new JLabel(
                        "Thời gian: 00:00",
                        JLabel.CENTER
                );

        timeLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        18
                )
        );


        // -----------------------------------------------------
        // LOI
        // -----------------------------------------------------

        errorLabel =
                new JLabel(
                        "Lỗi: 0",
                        JLabel.CENTER
                );

        errorLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        18
                )
        );


        // -----------------------------------------------------
        // DIEM
        // -----------------------------------------------------

        scoreLabel =
                new JLabel(
                        "Điểm: 0",
                        JLabel.CENTER
                );

        scoreLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        18
                )
        );


        // -----------------------------------------------------
        // LUOT GOI Y
        // -----------------------------------------------------

        hintLabel =
                new JLabel(
                        "Gợi ý: 0/∞",
                        JLabel.CENTER
                );

        hintLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        18
                )
        );


        // -----------------------------------------------------
        // CUP
        // -----------------------------------------------------

        trophyLabel =
                new JLabel(
                        "\uD83E\uDD49 Bronze",
                        JLabel.CENTER
                );

        trophyLabel.setFont(
                new Font(
                        "Segoe UI Emoji",
                        Font.PLAIN,
                        18
                )
        );


        // -----------------------------------------------------
        // DO KHO
        // -----------------------------------------------------

        JPanel difficultyPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                5,
                                0
                        )
                );

        JLabel difficultyLabel =
                new JLabel(
                        "Độ khó:"
                );

        difficultyLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        16
                )
        );

        difficultyBox =
                new JComboBox<>(
                        new String[]{
                                "Dễ",
                                "Trung bình",
                                "Khó",
                                "Siêu khó"
                        }
                );

        difficultyBox.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        15
                )
        );

        difficultyBox.setPreferredSize(
                new Dimension(
                        125,
                        30
                )
        );

        difficultyPanel.add(
                difficultyLabel
        );

        difficultyPanel.add(
                difficultyBox
        );


        infoPanel.add(
                timeLabel
        );

        infoPanel.add(
                errorLabel
        );

        infoPanel.add(
                scoreLabel
        );

        infoPanel.add(
                hintLabel
        );

        infoPanel.add(
                trophyLabel
        );

        infoPanel.add(
                difficultyPanel
        );

        rightPanel.add(
                infoPanel,
                BorderLayout.NORTH
        );


        // =====================================================
        // LICH SU NUOC DI
        // =====================================================

        JPanel movePanel =
                new JPanel(
                        new BorderLayout(
                                5,
                                5
                        )
                );

        JLabel moveTitle =
                new JLabel(
                        "LỊCH SỬ NƯỚC ĐI",
                        JLabel.CENTER
                );

        moveTitle.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        20
                )
        );

        moveArea =
                new JTextArea();

        moveArea.setEditable(
                false
        );

        moveArea.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        16
                )
        );

        moveArea.setLineWrap(
                true
        );

        moveArea.setWrapStyleWord(
                true
        );

        JScrollPane scrollPane =
                new JScrollPane(
                        moveArea
                );

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        new Color(
                                200,
                                200,
                                200
                        )
                )
        );

        movePanel.add(
                moveTitle,
                BorderLayout.NORTH
        );

        movePanel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        rightPanel.add(
                movePanel,
                BorderLayout.CENTER
        );

        centerPanel.add(
                rightPanel,
                BorderLayout.EAST
        );

        mainPanel.add(
                centerPanel,
                BorderLayout.CENTER
        );


        // =====================================================
        // CAC NUT
        // =====================================================

        JPanel buttonPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                10,
                                5
                        )
                );

        JButton newButton =
                new JButton(
                        "Ván mới"
                );

        JButton solveButton =
                new JButton(
                        "Giải"
                );

        JButton hintButton =
                new JButton(
                        "Gợi ý"
                );

        JButton saveButton =
                new JButton(
                        "Lưu"
                );

        JButton loadButton =
                new JButton(
                        "Tải"
                );

        Dimension buttonSize =
                new Dimension(
                        100,
                        38
                );

        newButton.setPreferredSize(
                buttonSize
        );

        solveButton.setPreferredSize(
                buttonSize
        );

        hintButton.setPreferredSize(
                buttonSize
        );

        saveButton.setPreferredSize(
                buttonSize
        );

        loadButton.setPreferredSize(
                buttonSize
        );

        buttonPanel.add(
                newButton
        );

        buttonPanel.add(
                solveButton
        );

        buttonPanel.add(
                hintButton
        );

        buttonPanel.add(
                saveButton
        );

        buttonPanel.add(
                loadButton
        );

        mainPanel.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        add(mainPanel);


        // =====================================================
        // SU KIEN CAC NUT
        // =====================================================

        newButton.addActionListener(
                e -> newGame()
        );

        solveButton.addActionListener(
                e -> solveSudoku()
        );

        hintButton.addActionListener(
                e -> giveHint()
        );

        saveButton.addActionListener(
                e -> saveGame()
        );

        loadButton.addActionListener(
                e -> loadGame()
        );

        difficultyBox.addActionListener(
                e -> newGame()
        );


        // Tao van dau tien
        newGame();
    }


    // =========================================================
    // TIMER
    // =========================================================

    private void startTimer() {

        timer =
                new Timer(
                        1000,
                        e -> updateTime()
                );

        timer.start();
    }


    private void updateTime() {

        String difficulty =
                (String)
                        difficultyBox
                                .getSelectedItem();


        // -----------------------------------------------------
        // SIÊU KHÓ: DEM NGUOC
        // -----------------------------------------------------

        if (difficulty.equals(
                "Siêu khó"
        )) {

            seconds--;

            int minutes =
                    seconds / 60;

            int second =
                    seconds % 60;

            timeLabel.setText(
                    String.format(
                            "Thời gian: %02d:%02d",
                            minutes,
                            second
                    )
            );


            if (seconds <= 0) {

                timer.stop();

                JOptionPane.showMessageDialog(
                        this,
                        "Time is up!\n"
                                + "You lose."
                );

                newGame();
            }

            return;
        }


        // -----------------------------------------------------
        // DE / TRUNG BINH / KHO
        // DEM LEN
        // -----------------------------------------------------

        seconds++;

        int minutes =
                seconds / 60;

        int second =
                seconds % 60;

        timeLabel.setText(
                String.format(
                        "Thời gian: %02d:%02d",
                        minutes,
                        second
                )
        );
    }


    // =========================================================
    // TAO VAN MOI
    // =========================================================

    private void newGame() {

        gameFinished = false;
        updatingBoard = true;

        String difficulty =
                (String)
                        difficultyBox
                                .getSelectedItem();

        int removeCount = 35;

        if (difficulty.equals("Dễ")) {
            removeCount = 35;
        } else if (difficulty.equals("Trung bình")) {
            removeCount = 45;
        } else if (difficulty.equals("Khó")) {
            removeCount = 52;
        } else if (difficulty.equals("Siêu khó")) {
            removeCount = 58;
        }

        // Tạo ván mới
        board =
                generator.createBoard(
                        removeCount
                );

        // Lấy đáp án của ván mới
        solution =
                generator.getSolution();

        // Reset toàn bộ 81 ô trước khi hiển thị ván mới
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {

                errorLocked[i][j] = false;

                cells[i][j].setText("");

                cells[i][j].setEditable(true);

                cells[i][j].setBackground(
                        normalColor
                );

                cells[i][j].setForeground(
                        Color.BLACK
                );
            }
        }

        // Hiển thị các ô có sẵn của ván mới
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {

                int number =
                        board.getNumber(i, j);

                if (number != 0) {

                    cells[i][j].setText(
                            String.valueOf(number)
                    );

                    cells[i][j].setEditable(false);

                    cells[i][j].setBackground(
                            fixedColor
                    );

                    cells[i][j].setForeground(
                            Color.BLACK
                    );
                }
            }
        }

        // Reset điểm, lỗi và số lượt gợi ý
        errorCount = 0;
        score = 0;
        nextPoint = 1;
        hintCount = 0;
        updateHintLabel();

        if (difficulty.equals("Siêu khó")) {
            errorLabel.setText("Lỗi: 0/3");
        } else {
            errorLabel.setText("Lỗi: 0");
        }

        scoreLabel.setText("Điểm: 0");
        updateTrophy();

        // Reset lịch sử
        moves.clear();

        if (moveArea != null) {
            moveArea.setText("");
        }

        // Reset thời gian
        if (difficulty.equals("Siêu khó")) {

            seconds = 7 * 60;

            timeLabel.setText(
                    "Thời gian: 07:00"
            );

        } else {

            seconds = 0;

            timeLabel.setText(
                    "Thời gian: 00:00"
            );
        }

        updatingBoard = false;

        // Chỉ chạy lại timer nếu timer đã được tạo
        if (timer != null) {
            timer.stop();
            timer.start();
        }
    }


    // =========================================================
    // HIEN THI BAN CO
    // =========================================================

    private void showBoard() {

        updatingBoard = true;

        for (int i = 0;
             i < 9;
             i++) {

            for (int j = 0;
                 j < 9;
                 j++) {

                int number =
                        board.getNumber(
                                i,
                                j
                        );

                if (number == 0) {

                    cells[i][j]
                            .setText("");

                } else {

                    cells[i][j]
                            .setText(
                                    String.valueOf(
                                            number
                                    )
                            );
                }
            }
        }

        updatingBoard = false;
    }


    // =========================================================
    // TIM O DANG BI KHOA
    // =========================================================

    private int[] getLockedCell() {

        for (int i = 0;
             i < 9;
             i++) {

            for (int j = 0;
                 j < 9;
                 j++) {

                if (errorLocked[i][j]) {

                    return new int[]{
                            i,
                            j
                    };
                }
            }
        }

        return null;
    }


    // =========================================================
    // KIEM TRA NHAP
    // =========================================================

    private void checkInput(
            int row,
            int col
    ) {

        if (updatingBoard) {
            return;
        }

        if (!cells[row][col].isEditable()) {
            return;
        }

        String text =
                cells[row][col]
                        .getText()
                        .trim();

        // Ô đang bị khóa vì nhập sai
        if (errorLocked[row][col]) {

            // Chỉ cho phép xóa ô sai
            if (text.isEmpty()) {

                errorLocked[row][col] = false;

                board.setNumber(
                        row,
                        col,
                        0
                );

                // Khoi phuc mau cac o da nhap dung truoc do.
                resetColors();
            }

            return;
        }

        // Xóa ô
        if (text.isEmpty()) {

            board.setNumber(
                    row,
                    col,
                    0
            );

            resetColors();

            return;
        }

        // Chỉ giữ lại 1 ký tự cuối
        // =====================================================
// CHI CHO 1 KY TU
// =====================================================

        if (text.length() > 1) {

            updatingBoard = true;

            // Chi giu lai ky tu dau tien
            cells[row][col]
                    .setText(
                            text.substring(0, 1)
                    );

            updatingBoard = false;

            return;
        }

        int number;

        try {

            number =
                    Integer.parseInt(text);

        } catch (NumberFormatException e) {

            lockWrongCell(
                    row,
                    col
            );

            return;
        }

        // Chỉ cho nhập số từ 1 đến 9
        if (number < 1 || number > 9) {

            lockWrongCell(
                    row,
                    col
            );

            return;
        }

        int oldNumber =
                board.getNumber(
                        row,
                        col
                );

        // Tạm xóa số cũ để kiểm tra
        board.setNumber(
                row,
                col,
                0
        );

        // Kiểm tra trùng hàng, cột, ô 3x3
        if (!board.isValid(
                row,
                col,
                number
        )) {

            board.setNumber(
                    row,
                    col,
                    oldNumber
            );

            lockWrongCell(
                    row,
                    col
            );

            return;
        }

        // Kiểm tra đáp án của ván hiện tại
        if (solution == null ||
                solution.getNumber(
                        row,
                        col
                ) != number) {

            board.setNumber(
                    row,
                    col,
                    oldNumber
            );

            lockWrongCell(
                    row,
                    col
            );

            return;
        }

        // Nhập đúng
        board.setNumber(
                row,
                col,
                number
        );

        correctInput(
                row,
                col,
                number
        );

        // Tính điểm
        int point = nextPoint;

        score += point;

        nextPoint = Math.min(nextPoint + 1, 5);

        scoreLabel.setText(
                "Điểm: " + score
        );

        updateTrophy();

        // Lưu lịch sử
        String move =
                (moves.size() + 1)
                        + ". Hàng "
                        + (row + 1)
                        + ", cột "
                        + (col + 1)
                        + " → "
                        + number
                        + "  (+"
                        + point
                        + ")";

        moves.add(move);

        moveArea.append(
                move + "\n"
        );

        // Kiểm tra hoàn thành
        if (isSolved()) {
            gameCompleted();
        }
    }


    // =========================================================
    // NHAP SAI
    // =========================================================

    private void lockWrongCell(
            int row,
            int col
    ) {

        // Nhap sai thi chuoi tra loi dung lien tiep bi ngat
        nextPoint = 1;

        cells[row][col]
                .setBackground(
                        wrongColor
                );

        cells[row][col]
                .setForeground(
                        Color.RED
                );


        // Khóa ô sai
        errorLocked[row][col] =
                true;


        // Đưa con trỏ về ô sai
        cells[row][col]
                .requestFocusInWindow();


        // Tăng số lỗi
        errorCount++;


        String difficulty =
                (String)
                        difficultyBox
                                .getSelectedItem();


        // -----------------------------------------------------
        // HIEN THI LOI
        // -----------------------------------------------------

        if (difficulty.equals(
                "Siêu khó"
        )) {

            errorLabel.setText(
                    "Lỗi: "
                            + errorCount
                            + "/3"
            );

        } else {

            errorLabel.setText(
                    "Lỗi: "
                            + errorCount
            );
        }


        // Sai reset combo diem
        nextPoint = 1;


        // -----------------------------------------------------
        // SIÊU KHÓ SAI 3 LAN
        // -----------------------------------------------------

        if (difficulty.equals(
                "Siêu khó"
        ) &&
                errorCount >= 3) {

            timer.stop();

            JOptionPane.showMessageDialog(
                    this,
                    "You made 3 mistakes!\n"
                            + "You lose."
            );

            newGame();

            return;
        }


        // -----------------------------------------------------
        // BAO LOI
        // -----------------------------------------------------

        JOptionPane.showMessageDialog(
                this,
                "Wrong number!\n\n"
                        + "Please delete the wrong number first."
        );


        // Dua lai con tro ve o sai
        cells[row][col]
                .requestFocusInWindow();
    }


    // =========================================================
    // NHAP DUNG
    // =========================================================

    private void correctInput(
            int row,
            int col,
            int number
    ) {

        highlightAround(
                row,
                col,
                number
        );


        // O VUA NHAP LUON XANH
        cells[row][col]
                .setBackground(
                        correctColor
                );

        cells[row][col]
                .setForeground(
                        new Color(
                                0,
                                70,
                                160
                        )
                );
    }


    // =========================================================
    // HIGHLIGHT
    // =========================================================

    private void highlightAround(
            int row,
            int col,
            int number
    ) {


        // -----------------------------------------------------
        // RESET MAU
        // -----------------------------------------------------

        for (int i = 0;
             i < 9;
             i++) {

            for (int j = 0;
                 j < 9;
                 j++) {

                if (!cells[i][j]
                        .isEditable()) {

                    cells[i][j]
                            .setBackground(
                                    fixedColor
                            );

                } else {

                    cells[i][j]
                            .setBackground(
                                    normalColor
                            );
                }

                cells[i][j]
                        .setForeground(
                                Color.BLACK
                        );
            }
        }


        // -----------------------------------------------------
        // HIGHLIGHT HANG
        // -----------------------------------------------------

        for (int j = 0;
             j < 9;
             j++) {

            if (cells[row][j]
                    .isEditable()) {

                cells[row][j]
                        .setBackground(
                                relatedColor
                        );
            }
        }


        // -----------------------------------------------------
        // HIGHLIGHT COT
        // -----------------------------------------------------

        for (int i = 0;
             i < 9;
             i++) {

            if (cells[i][col]
                    .isEditable()) {

                cells[i][col]
                        .setBackground(
                                relatedColor
                        );
            }
        }


        // -----------------------------------------------------
        // HIGHLIGHT O 3x3
        // -----------------------------------------------------

        int startRow =
                row - row % 3;

        int startCol =
                col - col % 3;

        for (int i = startRow;
             i < startRow + 3;
             i++) {

            for (int j = startCol;
                 j < startCol + 3;
                 j++) {

                if (cells[i][j]
                        .isEditable()) {

                    cells[i][j]
                            .setBackground(
                                    relatedColor
                            );
                }
            }
        }


        // -----------------------------------------------------
        // HIGHLIGHT CAC SO GIONG NHAU
        // -----------------------------------------------------

        for (int i = 0;
             i < 9;
             i++) {

            for (int j = 0;
                 j < 9;
                 j++) {

                if (board.getNumber(
                        i,
                        j
                ) == number) {

                    if (i != row ||
                            j != col) {

                        cells[i][j]
                                .setBackground(
                                        correctColor
                                );
                    }
                }
            }
        }

        // Khong giu mau xanh cua lan nhap truoc.
        // Lan nhap moi se chi highlight cac o lien quan va cac so giong nhau.
    }


    // =========================================================
    // RESET MAU
    // =========================================================

    private void resetColors() {

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {

                int number = board.getNumber(i, j);

                if (!cells[i][j].isEditable()) {

                    cells[i][j].setBackground(fixedColor);
                    cells[i][j].setForeground(Color.BLACK);

                } else if (errorLocked[i][j]) {

                    cells[i][j].setBackground(wrongColor);
                    cells[i][j].setForeground(Color.RED);

                } else {

                    cells[i][j].setBackground(normalColor);
                    cells[i][j].setForeground(Color.BLACK);
                }
            }
        }
    }


    // =========================================================
    // CUP THEO DIEM
    // =========================================================

    private void updateTrophy() {

        if (score >= 100) {

            trophyLabel.setText(
                    "\uD83D\uDC8E Diamond"
            );

        } else if (score >= 60) {

            trophyLabel.setText(
                    "\uD83E\uDD47 Gold"
            );

        } else if (score >= 25) {

            trophyLabel.setText(
                    "\uD83E\uDD48 Silver"
            );

        } else {

            trophyLabel.setText(
                    "\uD83E\uDD49 Bronze"
            );
        }
    }


    // =========================================================
    // KIEM TRA DA GIAI XONG
    // =========================================================

    private boolean isSolved() {

        if (!board.isFull()) {

            return false;
        }


        for (int i = 0;
             i < 9;
             i++) {

            for (int j = 0;
                 j < 9;
                 j++) {

                int number =
                        board.getNumber(
                                i,
                                j
                        );

                board.setNumber(
                        i,
                        j,
                        0
                );

                boolean valid =
                        board.isValid(
                                i,
                                j,
                                number
                        );

                board.setNumber(
                        i,
                        j,
                        number
                );

                if (!valid) {

                    return false;
                }
            }
        }

        return true;
    }


    // =========================================================
    // HOAN THANH
    // =========================================================

    private void gameCompleted() {

        if (gameFinished) {
            return;
        }

        gameFinished = true;

        if (timer != null) {
            timer.stop();
        }

        // Khoa cac o sau khi da giai xong de giu nguyen ban co hoan chinh
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                cells[i][j].setEditable(false);
            }
        }

        JOptionPane.showMessageDialog(
                this,
                "Bạn đã giải thành công Sudoku!\n\n"
                        + "Điểm: " + score + "\n"
                        + "Danh hiệu: " + getTrophyName() + "\n"
                        + timeLabel.getText()
        );

        String[] options = {"Có", "Không"};
        int upgrade = JOptionPane.showOptionDialog(
                this,
                "Bạn có muốn nâng độ khó không?",
                "Chúc mừng!",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (upgrade == 0) {
            int index = difficultyBox.getSelectedIndex();

            if (index < difficultyBox.getItemCount() - 1) {
                // Chon do kho ke tiep; su kien cua combobox se tao ban moi
                difficultyBox.setSelectedIndex(index + 1);
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Bạn đang ở mức độ khó cao nhất. Độ khó sẽ được giữ nguyên."
                );
                askNewBoardAtSameDifficulty();
            }
        } else if (upgrade == 1) {
            // Khong nang do kho: hoi co muon tao ban moi cung muc do khong
            askNewBoardAtSameDifficulty();
        }
        // Neu dong hop thoai, giu nguyen giao dien ban co da giai xong
    }

    private void askNewBoardAtSameDifficulty() {
        String[] options = {"Có", "Không"};
        int result = JOptionPane.showOptionDialog(
                this,
                "Bạn có muốn tạo mới bàn cờ không?",
                "Tạo bàn cờ mới",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (result == 0) {
            newGame();
        }
        // Neu chon Khong hoac dong hop thoai, giu nguyen ban co da giai xong
    }


    // =========================================================
    // TEN CUP
    // =========================================================

    private String getTrophyName() {

        if (score >= 100) {

            return "Diamond";
        }

        if (score >= 60) {

            return "Gold";
        }

        if (score >= 25) {

            return "Silver";
        }

        return "Bronze";
    }


    // =========================================================
    // NANG DO KHO
    // =========================================================

    private void increaseDifficulty() {
        int index = difficultyBox.getSelectedIndex();
        if (index < difficultyBox.getItemCount() - 1) {
            difficultyBox.setSelectedIndex(index + 1);
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Bạn đang ở mức độ khó cao nhất."
            );
        }
    }


    // =========================================================
    // GIAI SUDOKU
    // =========================================================

    private void solveSudoku() {

        if (!checkBoard()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Bàn cờ đang có số bị trùng!"
            );

            return;
        }


        boolean result =
                solver.solve(
                        board
                );


        if (result) {

            updatingBoard = true;

            showBoard();


            for (int i = 0;
                 i < 9;
                 i++) {

                for (int j = 0;
                     j < 9;
                     j++) {

                    cells[i][j]
                            .setEditable(
                                    false
                            );
                }
            }

            updatingBoard = false;


            if (timer != null) {

                timer.stop();
            }


            JOptionPane.showMessageDialog(
                    this,
                    "Đã giải Sudoku!"
            );

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Không thể giải Sudoku!"
            );
        }
    }


    // =========================================================
    // GIOI HAN LUOT GOI Y
    // =========================================================

    // -1 nghia la khong gioi han
    private int getHintLimit(String difficulty) {
        if ("Dễ".equals(difficulty)) {
            return -1;
        } else if ("Trung bình".equals(difficulty)) {
            return 10;
        } else if ("Khó".equals(difficulty)) {
            return 5;
        } else if ("Siêu khó".equals(difficulty)) {
            return 3;
        }
        return -1;
    }

    // Cap nhat so luot goi y da dung / tong so luot toi da
    private void updateHintLabel() {
        if (hintLabel == null || difficultyBox == null) {
            return;
        }

        String difficulty = (String) difficultyBox.getSelectedItem();
        int hintLimit = getHintLimit(difficulty);

        if (hintLimit == -1) {
            hintLabel.setText("Gợi ý: " + hintCount + "/∞");
        } else {
            hintLabel.setText("Gợi ý: " + hintCount + "/" + hintLimit);
        }
    }


    // =========================================================
    // GOI Y
    // =========================================================

    private void giveHint() {

        // Nếu đang có ô sai thì phải xóa ô sai trước
        int[] lockedCell =
                getLockedCell();

        if (lockedCell != null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please delete the wrong number first!"
            );

            cells[lockedCell[0]][lockedCell[1]]
                    .requestFocusInWindow();

            return;
        }

        // Gioi han luot goi y theo do kho
        String difficulty = (String) difficultyBox.getSelectedItem();
        int hintLimit = getHintLimit(difficulty);

        if (hintLimit != -1 && hintCount >= hintLimit) {
            JOptionPane.showMessageDialog(
                    this,
                    "Bạn đã sử dụng hết " + hintLimit + " lượt gợi ý ở mức độ " + difficulty + "."
            );
            return;
        }

        SudokuBoard temp =
                new SudokuBoard();

        for (int i = 0;
             i < 9;
             i++) {

            for (int j = 0;
                 j < 9;
                 j++) {

                temp.setNumber(
                        i,
                        j,
                        board.getNumber(
                                i,
                                j
                        )
                );
            }
        }

        if (!solver.solve(temp)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Không thể đưa ra gợi ý."
            );

            return;
        }

        for (int i = 0;
             i < 9;
             i++) {

            for (int j = 0;
                 j < 9;
                 j++) {

                if (board.getNumber(
                        i,
                        j
                ) == 0) {

                    int number =
                            solution.getNumber(
                                    i,
                                    j
                            );

                    board.setNumber(
                            i,
                            j,
                            number
                    );

                    hintCount++;
                    updateHintLabel();

                    updatingBoard = true;

                    cells[i][j].setText(
                            String.valueOf(number)
                    );

                    updatingBoard = false;

                    cells[i][j].setBackground(
                            correctColor
                    );

                    cells[i][j].setForeground(
                            new Color(
                                    0,
                                    70,
                                    160
                            )
                    );

                    String move =
                            (moves.size() + 1)
                                    + ". Gợi ý: Hàng "
                                    + (i + 1)
                                    + ", cột "
                                    + (j + 1)
                                    + " → "
                                    + number;

                    moves.add(move);

                    moveArea.append(
                            move + "\n"
                    );

                    if (isSolved()) {
                        gameCompleted();
                    }

                    return;
                }
            }
        }

        JOptionPane.showMessageDialog(
                this,
                "Bàn Sudoku đã đầy!"
        );
    }


    // =========================================================
    // KIEM TRA BAN CO
    // =========================================================

    private boolean checkBoard() {

        getBoard();


        for (int i = 0;
             i < 9;
             i++) {

            for (int j = 0;
                 j < 9;
                 j++) {

                int number =
                        board.getNumber(
                                i,
                                j
                        );


                if (number != 0) {

                    board.setNumber(
                            i,
                            j,
                            0
                    );


                    if (!board.isValid(
                            i,
                            j,
                            number
                    )) {

                        board.setNumber(
                                i,
                                j,
                                number
                        );

                        return false;
                    }


                    board.setNumber(
                            i,
                            j,
                            number
                    );
                }
            }
        }

        return true;
    }


    // =========================================================
    // LAY DU LIEU TU GIAO DIEN
    // =========================================================

    private void getBoard() {

        for (int i = 0;
             i < 9;
             i++) {

            for (int j = 0;
                 j < 9;
                 j++) {

                String text =
                        cells[i][j]
                                .getText()
                                .trim();


                if (text.isEmpty()) {

                    board.setNumber(
                            i,
                            j,
                            0
                    );

                } else {

                    try {

                        int number =
                                Integer.parseInt(
                                        text
                                );


                        if (number >= 1 &&
                                number <= 9) {

                            board.setNumber(
                                    i,
                                    j,
                                    number
                            );

                        } else {

                            board.setNumber(
                                    i,
                                    j,
                                    0
                            );
                        }

                    } catch (
                            NumberFormatException e
                    ) {

                        board.setNumber(
                                i,
                                j,
                                0
                        );
                    }
                }
            }
        }
    }


    // =========================================================
    // LUU GAME
    // =========================================================

    private void saveGame() {

        getBoard();

        file.save(
                board,
                "sudoku.txt"
        );


        JOptionPane.showMessageDialog(
                this,
                "Đã lưu game!"
        );
    }


    // =========================================================
    // TAI GAME
    // =========================================================

    private void loadGame() {

        SudokuBoard newBoard =
                file.load(
                        "sudoku.txt"
                );


        if (newBoard != null) {

            board = newBoard;

            showBoard();


            for (int i = 0;
                 i < 9;
                 i++) {

                for (int j = 0;
                     j < 9;
                     j++) {

                    errorLocked[i][j] =
                            false;


                    if (board.getNumber(
                            i,
                            j
                    ) != 0) {

                        cells[i][j]
                                .setEditable(
                                        false
                                );

                        cells[i][j]
                                .setBackground(
                                        fixedColor
                                );

                    } else {

                        cells[i][j]
                                .setEditable(
                                        true
                                );

                        cells[i][j]
                                .setBackground(
                                        normalColor
                                );
                    }
                }
            }


            JOptionPane.showMessageDialog(
                    this,
                    "Đã tải game!"
            );
        }
    }
}

