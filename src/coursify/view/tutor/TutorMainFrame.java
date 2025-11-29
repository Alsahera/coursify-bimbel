package coursify.view.tutor;

import coursify.database.DataStore;
import coursify.model.Siswa;
import coursify.model.Tutor;
import coursify.view.Login;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

public class TutorMainFrame extends JFrame {

    private Tutor tutor;
    private JTabbedPane tabbedPane;

    // ===========================
    // COLOR PALETTE (SAMA DENGAN ADMIN)
    // ===========================
    private static final Color PRIMARY_DARK       = new Color(18, 40, 76);
    private static final Color PRIMARY_MEDIUM     = new Color(32, 70, 122);
    private static final Color PRIMARY_LIGHT      = new Color(120, 181, 255);
    private static final Color ACCENT_SOFT        = new Color(240, 245, 252);
    private static final Color TEXT_DARK          = new Color(45, 52, 64);
    private static final Color BACKGROUND_LIGHT   = new Color(245, 247, 250);

    private static final Color SUCCESS_GREEN      = new Color(46, 204, 113);
    private static final Color DANGER_RED         = new Color(231, 76, 60);
    private static final Color WARNING_ORANGE     = new Color(241, 196, 15);

    // label info di panel kelas
    private JLabel infoLabel;

    public TutorMainFrame(Tutor tutor) {
        this.tutor = tutor;

        setTitle("Coursify - Tutor Dashboard");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setUIFont(new FontUIResource("Poppins", Font.PLAIN, 13));

        JPanel headerPanel = createHeaderPanel();
        tabbedPane = createStyledTabbedPane();

        // TAB
        tabbedPane.addTab("Kelas", createClassManagementPanel());
        tabbedPane.addTab("Siswa", createStudentListPanel());
        tabbedPane.addTab("Jadwal Tutor", createSchedulePanel());

        // wrapper card utama, mirip AdminMainFrame
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(BACKGROUND_LIGHT);
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(16, 24, 24, 24));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 229, 235), 1, true),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        card.add(tabbedPane, BorderLayout.CENTER);

        centerWrapper.add(card, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(centerWrapper, BorderLayout.CENTER);

        getContentPane().setBackground(BACKGROUND_LIGHT);
    }

    // ======================================================
    // GLOBAL FONT
    // ======================================================
    private static void setUIFont(FontUIResource f) {
        java.util.Enumeration<?> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key   = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }

    // ======================================================
    // HEADER
    // ======================================================
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(
                        0, 0, PRIMARY_DARK,
                        w, 0, PRIMARY_MEDIUM
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);
            }
        };
        headerPanel.setPreferredSize(new Dimension(0, 90));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        // LEFT: avatar + text
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        left.setOpaque(false);

        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setColor(new Color(255, 255, 255, 80));
                g2.drawOval(2, 2, getWidth() - 5, getHeight() - 5);
            }
        };
        avatarPanel.setPreferredSize(new Dimension(48, 48));
        avatarPanel.setOpaque(false);
        avatarPanel.setLayout(new BorderLayout());

        JLabel avatarEmoji = new JLabel("👤", SwingConstants.CENTER);
        avatarEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        avatarPanel.add(avatarEmoji, BorderLayout.CENTER);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel welcomeLabel = new JLabel("Selamat Datang, " + tutor.getUsername());
        welcomeLabel.setFont(new Font("Poppins", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);

        String spec = tutor.getSpecialization() != null ? tutor.getSpecialization() : "-";
        JLabel roleLabel = new JLabel("Tutor · " + spec);
        roleLabel.setFont(new Font("Poppins", Font.PLAIN, 13));
        roleLabel.setForeground(new Color(220, 235, 250));

        textPanel.add(welcomeLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(roleLabel);

        left.add(avatarPanel);
        left.add(textPanel);

        JButton logoutButton = createStyledButton("Sign out", PRIMARY_MEDIUM, Color.WHITE);
        logoutButton.setPreferredSize(new Dimension(120, 40));
        logoutButton.addActionListener(e -> logout());

        headerPanel.add(left, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        return headerPanel;
    }

    // ======================================================
    // TABBED PANE STYLE
    // ======================================================
    private JTabbedPane createStyledTabbedPane() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Poppins", Font.BOLD, 13));
        tabs.setBackground(Color.WHITE);
        tabs.setOpaque(false);
        tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        tabs.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                              int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected) {
                    g2.setColor(Color.WHITE);
                } else {
                    g2.setColor(ACCENT_SOFT);
                }
                g2.fillRoundRect(x + 2, y + 7, w - 4, h - 10, 18, 18);
            }

            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                          int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(210, 216, 225));
                g2.drawRoundRect(x + 2, y + 7, w - 4, h - 10, 18, 18);
            }

            @Override
            protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects,
                                               int tabIndex, Rectangle iconRect,
                                               Rectangle textRect, boolean isSelected) {
                // no focus outline
            }

            @Override
            protected Insets getTabInsets(int tabPlacement, int tabIndex) {
                return new Insets(10, 40, 10, 40);
            }

            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                // border dari card saja
            }
        });

        return tabs;
    }

    // ======================================================
    // BUTTON STYLE
    // ======================================================
    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Poppins", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void makeActionButtonsWide(JButton... buttons) {
        Dimension d = new Dimension(170, 36);
        for (JButton b : buttons) {
            b.setPreferredSize(d);
        }
    }

    // ======================================================
    // TABLE STYLE
    // ======================================================
    private void styleTable(JTable table) {
        table.setFont(new Font("Poppins", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(Color.WHITE);
        table.setShowVerticalLines(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(240, 243, 247));
        header.setForeground(TEXT_DARK);
        header.setFont(new Font("Poppins", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(0, 36));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                    setForeground(TEXT_DARK);
                }
                setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
                return c;
            }
        });
    }

    // ======================================================
    // UTIL: LIST MAPEL PER SPECIALIZATION
    // ======================================================
    private String[] getSubSubjectsForTutor() {
        String spec = tutor.getSpecialization() == null ? "" : tutor.getSpecialization().toLowerCase();

        if (spec.contains("speaking")) {
            // Tutor speaking – Fista
            return new String[]{
                    "Pronunciation & Intonation",
                    "Vocabulary for Fluency",
                    "Daily Conversations & Role-Play"
            };
        } else if (spec.contains("grammar")) {
            // Tutor grammar
            return new String[]{
                    "Tenses (Basic & Advanced)",
                    "Sentence Structure",
                    "Parts of Speech & Punctuation"
            };
        } else if (spec.contains("toefl")) {
            // Tutor TOEFL – Alsa
            return new String[]{
                    "Reading Strategies",
                    "Listening Strategies",
                    "Speaking Tasks Practice",
                    "Writing Tasks Practice"
            };
        } else {
            return new String[]{"-"};
        }
    }

    private boolean isStudentBelongsToTutorPackage(Siswa s) {
        if (s == null) return false;
        String kelas = s.getKelas() == null ? "" : s.getKelas().toLowerCase();
        String spec  = tutor.getSpecialization() == null ? "" : tutor.getSpecialization().toLowerCase();

        if (spec.contains("speaking")) {
            return kelas.contains("speaking");
        } else if (spec.contains("grammar")) {
            return kelas.contains("grammar");
        } else if (spec.contains("toefl")) {
            return kelas.contains("toefl");
        }
        return false;
    }

    // ======================================================
    // UTIL: REKAP NILAI PER SISWA (MID + FINAL)
    // Struktur hasil: [id_siswa, nama, mid, final, batch, tanggal_terakhir]
    // ======================================================
    private List<Object[]> buildRekapNilaiPerSiswa(List<Object[]> nilaiList) {
        List<Object[]> result = new ArrayList<>();
        if (nilaiList == null || nilaiList.isEmpty()) return result;

        Map<String, Object[]> map = new LinkedHashMap<>();

        for (Object[] n : nilaiList) {
            if (n == null || n.length < 6) continue;

            String idSiswa = n[0] != null ? n[0].toString() : "-";
            String nama    = n[1] != null ? n[1].toString() : "-";

            int nilai = 0;
            if (n[2] instanceof Integer) {
                nilai = (Integer) n[2];
            } else if (n[2] != null) {
                try { nilai = Integer.parseInt(n[2].toString()); } catch (NumberFormatException ignored) {}
            }

            String ket      = n[3] != null ? n[3].toString() : "";
            String batch    = n[4] != null ? n[4].toString() : "-";
            String tanggal  = n[5] != null ? n[5].toString() : "";

            boolean isMid   = ket.toLowerCase().contains("mid");
            boolean isFinal = ket.toLowerCase().contains("final");

            Object[] row = map.get(idSiswa);
            if (row == null) {
                row = new Object[]{idSiswa, nama, null, null, batch, tanggal};
                map.put(idSiswa, row);
            }

            if (isMid)   row[2] = nilai;   // kolom Mid-test
            if (isFinal) row[3] = nilai;   // kolom Final-test

            // update batch & tanggal terakhir
            row[4] = batch;
            row[5] = tanggal;
        }

        result.addAll(map.values());
        return result;
    }

    // ==========================================
    // PANEL MANAJEMEN KELAS (MAPEL FIX PER TUTOR)
    // ==========================================
    private JPanel createClassManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        // Top panel with title
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setOpaque(false);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Manajemen Kelas - Paket " + tutor.getSpecialization());
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_DARK);
        titlePanel.add(titleLabel);

        topPanel.add(titlePanel, BorderLayout.WEST);

        // Table: Mata Pelajaran (mapel), Batch, Jumlah Siswa
        String[] columns = {"Mata Pelajaran (Paket)", "Batch", "Jumlah Siswa"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        // muat data mapel + info batch/siswa
        int totalBatch = loadClassManagementData(model);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JButton viewStudentButton = createStyledButton("Lihat Siswa", PRIMARY_MEDIUM, Color.WHITE);
        JButton inputNilaiButton  = createStyledButton("Input Nilai", PRIMARY_MEDIUM, Color.WHITE);
        JButton viewNilaiButton   = createStyledButton("Lihat Nilai", PRIMARY_MEDIUM, Color.WHITE);
        JButton refreshButton     = createStyledButton("Refresh", PRIMARY_MEDIUM, Color.WHITE);
        makeActionButtonsWide(viewStudentButton, inputNilaiButton, viewNilaiButton, refreshButton);

        viewStudentButton.addActionListener(e -> viewClassStudents(table));
        inputNilaiButton.addActionListener(e -> inputClassGrades(table));
        viewNilaiButton.addActionListener(e -> viewClassGrades(table));
        refreshButton.addActionListener(e -> {
            int batchCount = loadClassManagementData(model);
            infoLabel.setText("Total Batch yang Diajar: " + batchCount + " kelas");
        });

        buttonPanel.add(viewStudentButton);
        buttonPanel.add(inputNilaiButton);
        buttonPanel.add(viewNilaiButton);
        buttonPanel.add(refreshButton);

        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(220, 220, 220)));
        infoLabel = new JLabel("Total Batch yang Diajar: " + totalBatch + " kelas");
        infoLabel.setFont(new Font("Poppins", Font.BOLD, 13));
        infoLabel.setForeground(TEXT_DARK);
        infoPanel.add(infoLabel);

        JPanel bottomPanel = new JPanel(new BorderLayout(0, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        // *** mapelPanel DIHAPUS, jadi tidak ada list bullet mapel di bawah tombol ***
        bottomPanel.add(infoPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Mengisi tabel kelas dengan:
     *  - BARIS = mapel tetap per tutor (3 untuk speaking/grammar, 4 untuk TOEFL)
     *  - Kolom Batch & Jumlah Siswa diambil dari kelas pertama tutor (jika ada)
     *  Mengembalikan: jumlah batch/kelas di DB.
     */
    private int loadClassManagementData(DefaultTableModel model) {
        model.setRowCount(0);

        List<Object[]> kelasList = DataStore.getKelasByTutor(tutor.getId_user());
        int totalBatch = (kelasList == null) ? 0 : kelasList.size();

        String batch = "-";
        Object jumlahSiswa = "-";

        if (kelasList != null && !kelasList.isEmpty()) {
            Object[] kelasPertama = kelasList.get(0);
            batch = kelasPertama[3] != null ? kelasPertama[3].toString() : "-";
            if (kelasPertama.length > 4 && kelasPertama[4] != null) {
                jumlahSiswa = kelasPertama[4];
            }
        }

        String[] subs = getSubSubjectsForTutor();
        for (String mapel : subs) {
            model.addRow(new Object[]{ mapel, batch, jumlahSiswa });
        }

        return totalBatch;
    }

    // ===============================
    // LIHAT SISWA (SELALU KELAS PERTAMA)
    // ===============================
    private void viewClassStudents(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu mata pelajaran terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Object[]> kelasList = DataStore.getKelasByTutor(tutor.getId_user());
        if (kelasList == null || kelasList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Belum ada kelas untuk tutor ini.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Object[] kelas = kelasList.get(0); // SELALU pakai kelas pertama
        String idKelas   = (String) kelas[0];
        String batchKelas = kelas[3] != null ? kelas[3].toString() : "-";
        String namaKelas = tutor.getSpecialization() + " - " + batchKelas;

        JDialog dialog = new JDialog(this, "Siswa di Kelas: " + namaKelas, true);
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        panel.setBackground(BACKGROUND_LIGHT);

        JLabel titleLabel = new JLabel("Daftar Siswa - " + namaKelas);
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_DARK);

        String[] columns = {"ID Siswa", "Nama", "Email", "Telepon", "Tanggal Masuk", "Status"};
        DefaultTableModel studentModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable studentTable = new JTable(studentModel);
        styleTable(studentTable);
        JScrollPane scrollPane = new JScrollPane(studentTable);

        List<Object[]> siswaList = DataStore.getSiswaByKelas(idKelas);
        if (siswaList != null) {
            for (Object[] siswa : siswaList) {
                studentModel.addRow(siswa);
            }
        }

        JLabel countLabel = new JLabel("Total Siswa: " + studentModel.getRowCount() + " orang");
        countLabel.setFont(new Font("Poppins", Font.BOLD, 13));
        countLabel.setHorizontalAlignment(JLabel.CENTER);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(countLabel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // ======================================================
    // INPUT NILAI (PAKAI KELAS PERTAMA, PER MATA PELAJARAN)
    // ======================================================
    private void inputClassGrades(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu mata pelajaran terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // MATA PELAJARAN YANG DIPILIH DI TABEL
        String mapelTerpilih = table.getValueAt(selectedRow, 0).toString();

        List<Object[]> kelasList = DataStore.getKelasByTutor(tutor.getId_user());
        if (kelasList == null || kelasList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Belum ada kelas untuk tutor ini.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Object[] kelas = kelasList.get(0); // selalu kelas pertama
        String idKelas    = (String) kelas[0];
        String batchKelas = kelas[3] != null ? kelas[3].toString() : "-";

        JDialog dialog = new JDialog(this, "Input Nilai - " + mapelTerpilih, true);
        dialog.setSize(900, 600);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        mainPanel.setBackground(BACKGROUND_LIGHT);

        JLabel titleLabel = new JLabel("Input Nilai Siswa - " + mapelTerpilih + " (" + batchKelas + ")");
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_DARK);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Form Input Nilai"),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        // ambil siswa di kelas ini
        List<Object[]> siswaList = DataStore.getSiswaByKelas(idKelas);
        String[] siswaNames;
        if (siswaList == null || siswaList.isEmpty()) {
            siswaNames = new String[]{"- (Belum ada siswa)"};
        } else {
            siswaNames = new String[siswaList.size()];
            for (int i = 0; i < siswaList.size(); i++) {
                siswaNames[i] = siswaList.get(i)[0] + " - " + siswaList.get(i)[1]; // ID - Nama
            }
        }

        // row1: siswa, nilai, batch
        JPanel row1 = new JPanel(new GridLayout(1, 3, 10, 0));
        row1.setOpaque(false);

        JPanel siswaPanel = new JPanel(new BorderLayout(5, 5));
        siswaPanel.setOpaque(false);
        siswaPanel.add(new JLabel("Pilih Siswa:"), BorderLayout.NORTH);
        JComboBox<String> siswaCombo = new JComboBox<>(siswaNames);
        siswaPanel.add(siswaCombo, BorderLayout.CENTER);

        JPanel nilaiPanel = new JPanel(new BorderLayout(5, 5));
        nilaiPanel.setOpaque(false);
        nilaiPanel.add(new JLabel("Nilai (0-100):"), BorderLayout.NORTH);
        JTextField nilaiField = new JTextField();
        nilaiPanel.add(nilaiField, BorderLayout.CENTER);

        JPanel batchPanel = new JPanel(new BorderLayout(5, 5));
        batchPanel.setOpaque(false);
        batchPanel.add(new JLabel("Batch:"), BorderLayout.NORTH);
        JTextField batchField = new JTextField(batchKelas);
        batchPanel.add(batchField, BorderLayout.CENTER);

        row1.add(siswaPanel);
        row1.add(nilaiPanel);
        row1.add(batchPanel);

        // row2: jenis tes
        JPanel row2 = new JPanel(new BorderLayout(5, 5));
        row2.setOpaque(false);
        row2.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        row2.add(new JLabel("Jenis Tes:"), BorderLayout.NORTH);
        JComboBox<String> jenisTesCombo = new JComboBox<>(new String[]{
                "Mid-test (Minggu ke-2)",
                "Final-test (Minggu ke-4)"
        });
        row2.add(jenisTesCombo, BorderLayout.CENTER);

        // row3: keterangan tambahan
        JPanel row3 = new JPanel(new BorderLayout(5, 5));
        row3.setOpaque(false);
        row3.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        row3.add(new JLabel("Catatan Tambahan (opsional):"), BorderLayout.NORTH);
        JTextArea keteranganArea = new JTextArea(3, 30);
        keteranganArea.setLineWrap(true);
        keteranganArea.setWrapStyleWord(true);
        keteranganArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane keteranganScroll = new JScrollPane(keteranganArea);
        row3.add(keteranganScroll, BorderLayout.CENTER);

        // tombol simpan
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonRow.setOpaque(false);

        // TABEL NILAI YANG SUDAH ADA (rekap mid/final)
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBackground(BACKGROUND_LIGHT);

        JLabel tableTitle = new JLabel("Nilai yang Sudah Diinput - " + mapelTerpilih);
        tableTitle.setFont(new Font("Poppins", Font.BOLD, 14));
        tablePanel.add(tableTitle, BorderLayout.NORTH);

        String[] nilaiColumns = {
                "ID Siswa", "Nama",
                "Nilai Mid-test", "Nilai Final-test",
                "Batch", "Tanggal Input Terakhir"
        };
        DefaultTableModel nilaiModel = new DefaultTableModel(nilaiColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable nilaiTable = new JTable(nilaiModel);
        styleTable(nilaiTable);
        JScrollPane nilaiScroll = new JScrollPane(nilaiTable);
        tablePanel.add(nilaiScroll, BorderLayout.CENTER);

        // muat awal: PER MATA PELAJARAN YANG DIPILIH
        List<Object[]> nilaiListAwal =
                DataStore.getNilaiByKelas(idKelas, mapelTerpilih);
        List<Object[]> rekapAwal = buildRekapNilaiPerSiswa(nilaiListAwal);
        for (Object[] r : rekapAwal) {
            nilaiModel.addRow(r);
        }

        JButton saveButton = createStyledButton("Simpan Nilai", PRIMARY_MEDIUM, Color.WHITE);
        makeActionButtonsWide(saveButton);
        saveButton.addActionListener(e -> {
            try {
                String selectedSiswa = (String) siswaCombo.getSelectedItem();
                if (selectedSiswa == null || selectedSiswa.startsWith("-")) {
                    JOptionPane.showMessageDialog(dialog, "Belum ada siswa di kelas ini.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String idSiswa = selectedSiswa.split(" - ")[0];
                int nilai;
                try {
                    nilai = Integer.parseInt(nilaiField.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Nilai harus berupa angka 0-100!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (nilai < 0 || nilai > 100) {
                    JOptionPane.showMessageDialog(dialog, "Nilai harus antara 0-100!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String batch = batchField.getText().trim();
                if (batch.isEmpty()) batch = batchKelas;

                // kategori nilai
                String kategori;
                if (nilai >= 85) kategori = "Sangat Baik";
                else if (nilai >= 70) kategori = "Baik";
                else if (nilai >= 60) kategori = "Cukup";
                else kategori = "Kurang";

                String jenisTesFull  = (String) jenisTesCombo.getSelectedItem();
                String jenisTesShort = jenisTesFull.toLowerCase().contains("mid") ? "Mid-test" : "Final-test";

                String catatanTambahan = keteranganArea.getText().trim();
                // mapel detail sekarang = mapelTerpilih
                String mapelDetail = mapelTerpilih;
                String keterangan = jenisTesShort + " | " + mapelDetail + " | " + kategori;
                if (!catatanTambahan.isEmpty()) {
                    keterangan += " | " + catatanTambahan;
                }

                String tahunAjaran = batchKelas;

                boolean success = DataStore.inputNilaiKelas(
                        idSiswa,
                        tutor.getId_user(),
                        idKelas,
                        mapelTerpilih,  // <== simpan per mata pelajaran
                        nilai,
                        keterangan,
                        batch,          // kolom semester di DB = batch
                        tahunAjaran
                );

                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Nilai berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    nilaiField.setText("");
                    keteranganArea.setText("");

                    // reload tabel rekap KHUSUS mapelTerpilih
                    List<Object[]> nilaiList =
                            DataStore.getNilaiByKelas(idKelas, mapelTerpilih);
                    List<Object[]> rekap = buildRekapNilaiPerSiswa(nilaiList);
                    nilaiModel.setRowCount(0);
                    for (Object[] r : rekap) {
                        nilaiModel.addRow(r);
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog, "Gagal menyimpan nilai!", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonRow.add(saveButton);

        formPanel.add(row1);
        formPanel.add(row2);
        formPanel.add(row3);
        formPanel.add(buttonRow);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, tablePanel);
        splitPane.setDividerLocation(260);
        splitPane.setResizeWeight(0.45);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    // ======================================================
    // LIHAT NILAI (PAKAI KELAS PERTAMA, PER MATA PELAJARAN)
    // ======================================================
    private void viewClassGrades(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih salah satu mata pelajaran terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // MATA PELAJARAN YANG DIPILIH
        String mapelTerpilih = table.getValueAt(selectedRow, 0).toString();

        List<Object[]> kelasList = DataStore.getKelasByTutor(tutor.getId_user());
        if (kelasList == null || kelasList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Belum ada kelas untuk tutor ini.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Object[] kelas = kelasList.get(0); // kelas pertama
        String idKelas    = (String) kelas[0];
        String batchKelas = kelas[3] != null ? kelas[3].toString() : "-";
        String namaKelas  = mapelTerpilih + " - " + batchKelas;

        JDialog dialog = new JDialog(this, "Nilai Siswa - " + namaKelas, true);
        dialog.setSize(900, 520);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        panel.setBackground(BACKGROUND_LIGHT);

        JLabel titleLabel = new JLabel("Daftar Nilai - " + namaKelas);
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_DARK);

        String[] columns = {
                "ID Siswa", "Nama",
                "Nilai Mid-test", "Nilai Final-test",
                "Batch", "Tanggal Input Terakhir"
        };
        DefaultTableModel nilaiModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable nilaiTable = new JTable(nilaiModel);
        styleTable(nilaiTable);

        // renderer khusus kolom nilai mid & final
        DefaultTableCellRenderer nilaiRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                int nilai = 0;
                if (value instanceof Integer) nilai = (int) value;
                else if (value != null) {
                    try { nilai = Integer.parseInt(value.toString()); } catch (NumberFormatException ignored) {}
                }

                if (!isSelected) {
                    if (nilai >= 85) {
                        c.setBackground(new Color(212, 237, 218));
                        setForeground(new Color(21, 87, 36));
                    } else if (nilai >= 70) {
                        c.setBackground(new Color(209, 236, 241));
                        setForeground(new Color(11, 83, 148));
                    } else if (nilai >= 60) {
                        c.setBackground(new Color(255, 243, 205));
                        setForeground(new Color(133, 100, 4));
                    } else {
                        c.setBackground(new Color(248, 215, 218));
                        setForeground(new Color(114, 28, 36));
                    }
                }
                setHorizontalAlignment(JLabel.CENTER);
                setFont(new Font("Poppins", Font.BOLD, 13));
                return c;
            }
        };
        nilaiTable.getColumnModel().getColumn(2).setCellRenderer(nilaiRenderer); // mid
        nilaiTable.getColumnModel().getColumn(3).setCellRenderer(nilaiRenderer); // final

        JScrollPane scrollPane = new JScrollPane(nilaiTable);

        // muat & rekap KHUSUS mapelTerpilih
        List<Object[]> nilaiList = DataStore.getNilaiByKelas(idKelas, mapelTerpilih);
        List<Object[]> rekap = buildRekapNilaiPerSiswa(nilaiList);

        int totalSiswa = 0;
        int sangatBaik = 0, baik = 0, cukup = 0, kurang = 0;

        for (Object[] r : rekap) {
            nilaiModel.addRow(r);

            Integer mid   = r[2] instanceof Integer ? (Integer) r[2] : null;
            Integer fin   = r[3] instanceof Integer ? (Integer) r[3] : null;

            int nilaiAkhir = 0;
            if (fin != null) nilaiAkhir = fin;
            else if (mid != null) nilaiAkhir = mid;
            else continue;

            totalSiswa++;

            if (nilaiAkhir >= 85) sangatBaik++;
            else if (nilaiAkhir >= 70) baik++;
            else if (nilaiAkhir >= 60) cukup++;
            else kurang++;
        }

        // Statistik panel (sederhana)
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(220, 220, 220)));

        JLabel totalLabel = new JLabel("Total Siswa: " + totalSiswa);
        JLabel sbLabel    = new JLabel("Sangat Baik: " + sangatBaik);
        JLabel bLabel     = new JLabel("Baik: " + baik);
        JLabel cLabel     = new JLabel("Cukup: " + cukup);
        JLabel kLabel     = new JLabel("Kurang: " + kurang);

        totalLabel.setFont(new Font("Poppins", Font.BOLD, 13));
        sbLabel.setFont(new Font("Poppins", Font.PLAIN, 13));
        bLabel.setFont(new Font("Poppins", Font.PLAIN, 13));
        cLabel.setFont(new Font("Poppins", Font.PLAIN, 13));
        kLabel.setFont(new Font("Poppins", Font.PLAIN, 13));

        sbLabel.setForeground(SUCCESS_GREEN);
        bLabel.setForeground(new Color(11, 83, 148));
        cLabel.setForeground(WARNING_ORANGE);
        kLabel.setForeground(DANGER_RED);

        statsPanel.add(totalLabel);
        statsPanel.add(new JLabel("|"));
        statsPanel.add(sbLabel);
        statsPanel.add(bLabel);
        statsPanel.add(cLabel);
        statsPanel.add(kLabel);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(statsPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // ==========================================
    // PANEL DAFTAR SISWA (PER PAKET)
    // ==========================================
    private JPanel createStudentListPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Daftar Siswa - " + tutor.getSpecialization());
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_DARK);
        titlePanel.add(titleLabel);

        String[] columns = {"ID Siswa", "Nama", "Kelas", "Email", "Telepon"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        loadAllStudentsData(model);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JButton refreshButton = createStyledButton("Refresh", PRIMARY_MEDIUM, Color.WHITE);
        makeActionButtonsWide(refreshButton);
        refreshButton.addActionListener(e -> loadAllStudentsData(model));

        buttonPanel.add(refreshButton);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        infoPanel.setOpaque(false);
        JLabel infoLabel = new JLabel("Total Siswa: " + model.getRowCount() + " siswa");
        infoLabel.setFont(new Font("Poppins", Font.BOLD, 13));
        infoLabel.setForeground(TEXT_DARK);
        infoPanel.add(infoLabel);

        model.addTableModelListener(e -> {
            infoLabel.setText("Total Siswa: " + model.getRowCount() + " siswa");
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titlePanel, BorderLayout.WEST);
        topPanel.add(infoPanel, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadAllStudentsData(DefaultTableModel model) {
        model.setRowCount(0);
        List<Siswa> siswaList = DataStore.getAllSiswa();
        if (siswaList == null) return;

        for (Siswa siswa : siswaList) {
            // FILTER: hanya siswa yang kelasnya sesuai paket tutor
            if (!isStudentBelongsToTutorPackage(siswa)) continue;

            model.addRow(new Object[]{
                    siswa.getId_siswa(),
                    siswa.getNama(),
                    siswa.getKelas() != null ? siswa.getKelas() : "-",
                    siswa.getEmail(),
                    siswa.getTelepon()
            });
        }
    }

    // ==========================================
    // PANEL JADWAL MENGAJAR
    // ==========================================
    private JPanel createSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Jadwal Mengajar - " + tutor.getSpecialization());
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_DARK);
        titlePanel.add(titleLabel);

        String[] columns = {"Hari", "Jam", "Mata Pelajaran", "Kelas", "Ruangan"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        loadScheduleData(model);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(220, 220, 220)));
        JLabel infoLabel = new JLabel("Total Sesi Mengajar: " + model.getRowCount() + " per minggu");
        infoLabel.setFont(new Font("Poppins", Font.BOLD, 13));
        infoLabel.setForeground(TEXT_DARK);
        infoPanel.add(infoLabel);

        model.addTableModelListener(e -> {
            infoLabel.setText("Total Sesi Mengajar: " + model.getRowCount() + " per minggu");
        });

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadScheduleData(DefaultTableModel model) {
        model.setRowCount(0);
        List<Object[]> jadwalList = DataStore.getJadwalByTutor(tutor.getId_user());
        if (jadwalList == null) return;

        for (Object[] row : jadwalList) {
            model.addRow(row);
        }
    }

    // ======================================================
    // LOGOUT
    // ======================================================
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin logout?",
                "Konfirmasi Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            tutor.logout();
            dispose();
            new Login().setVisible(true);
        }
    }
}
