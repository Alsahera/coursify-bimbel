package coursify.view.admin;

import coursify.database.DataStore;
import coursify.model.Pembayaran;
import coursify.model.Siswa;
import coursify.model.Tutor;
import coursify.model.Admin;
import coursify.view.Login;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AdminMainFrame extends JFrame {

    private final Admin admin;
    private JTabbedPane tabbedPane;

    // ===========================
    // COLOR PALETTE
    // ===========================
    private static final Color PRIMARY_DARK       = new Color(18, 40, 76);
    private static final Color PRIMARY_MEDIUM     = new Color(32, 70, 122);
    private static final Color PRIMARY_LIGHT      = new Color(120, 181, 255);
    private static final Color ACCENT_SOFT        = new Color(240, 245, 252);
    private static final Color TEXT_DARK          = new Color(45, 52, 64);
    private static final Color BACKGROUND_LIGHT   = new Color(245, 247, 250);

    public AdminMainFrame(Admin admin) {
        this.admin = admin;

        setTitle("Coursify - Admin Dashboard");
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // global font (fallback kalau Poppins tidak ada)
        setUIFont(new FontUIResource("Poppins", Font.PLAIN, 13));

        JPanel headerPanel = createHeaderPanel();
        tabbedPane = createStyledTabbedPane();

        // TAB PANELS
        tabbedPane.addTab("Siswa", createStudentPanel());
        tabbedPane.addTab("Tutor", createTutorPanel());
        tabbedPane.addTab("Pembayaran", createPaymentPanel());
        tabbedPane.addTab("Kelas", createClassPanel());
        tabbedPane.addTab("Jadwal Tutor", createScheduleAdminPanel());
        // TAB "Rekap Nilai" DIHAPUS

        // CARD WRAPPER UNTUK AREA MENU + KONTEN
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
    // HEADER PANEL
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

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        left.setOpaque(false);

        // avatar bulat
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

        JLabel welcome = new JLabel("Selamat Datang, " + admin.getUsername());
        welcome.setForeground(Color.WHITE);
        welcome.setFont(new Font("Poppins", Font.BOLD, 18));

        JLabel role = new JLabel("Administrator · " + admin.getDepartment());
        role.setForeground(new Color(220, 235, 250));
        role.setFont(new Font("Poppins", Font.PLAIN, 13));

        textPanel.add(welcome);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(role);

        left.add(avatarPanel);
        left.add(textPanel);

        JButton logout = createStyledButton("Sign out", PRIMARY_DARK, Color.WHITE);
        logout.setPreferredSize(new Dimension(120, 40));
        logout.addActionListener(e -> logout());

        headerPanel.add(left, BorderLayout.WEST);
        headerPanel.add(logout, BorderLayout.EAST);
        return headerPanel;
    }

    // ======================================================
    // TABBED PANE MENU ATAS
    // ======================================================
    private JTabbedPane createStyledTabbedPane() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Poppins", Font.BOLD, 13));
        tabs.setBackground(Color.WHITE);
        tabs.setOpaque(false);
        tabs.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
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
                // diperlebar supaya kelihatan seperti button panjang
                return new Insets(10, 40, 10, 40);
            }

            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                // border konten di-handle oleh panel card
            }
        });

        return tabs;
    }

    // ======================================================
    // BUTTON STYLE
    // ======================================================
    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFont(new Font("Poppins", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });

        return btn;
    }

    // supaya tombol aksi bawah lebih panjang (supaya teks kelihatan semua)
    private void makeActionButtonsWide(JButton... buttons) {
        Dimension d = new Dimension(190, 36);
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
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
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
    // PANEL: MANAJEMEN SISWA
    // ======================================================
    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel title = new JLabel("Data Siswa");
        title.setFont(new Font("Poppins", Font.BOLD, 18));
        title.setForeground(TEXT_DARK);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchPanel.setOpaque(false);
        JLabel lblCari = new JLabel("Cari Siswa:");
        JTextField txtSearch = new JTextField(20);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        JButton btnCari = createStyledButton("Cari", PRIMARY_MEDIUM, Color.WHITE);

        searchPanel.add(lblCari);
        searchPanel.add(txtSearch);
        searchPanel.add(btnCari);

        top.add(title, BorderLayout.WEST);
        top.add(searchPanel, BorderLayout.EAST);

        String[] cols = {"ID", "Nama", "Paket", "Alamat", "Telepon", "Email", "Tanggal Lahir"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        styleTable(table);

        // Sorter agar ID siswa selalu terurut (ID kolom 0)
        TableRowSorter<DefaultTableModel> studentSorter = new TableRowSorter<>(model);
        table.setRowSorter(studentSorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        studentSorter.setSortKeys(sortKeys);
        studentSorter.sort();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        loadStudentData(model);

        btnCari.addActionListener(e -> {
            String kw = txtSearch.getText().trim();
            if (kw.isEmpty()) {
                loadStudentData(model);
            } else {
                searchStudent(model, kw);
            }
        });
        txtSearch.addActionListener(e -> btnCari.doClick());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        buttons.setBackground(Color.WHITE);
        buttons.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JButton btnAdd    = createStyledButton("Tambah Siswa", PRIMARY_MEDIUM, Color.WHITE);
        JButton btnEdit   = createStyledButton("Edit Siswa", PRIMARY_MEDIUM, Color.WHITE);
        JButton btnDelete = createStyledButton("Hapus Siswa", PRIMARY_MEDIUM, Color.WHITE);
        JButton btnRefresh= createStyledButton("Refresh", PRIMARY_MEDIUM, Color.WHITE);
        makeActionButtonsWide(btnAdd, btnEdit, btnDelete, btnRefresh);

        btnAdd.addActionListener(e -> addStudent(model));
        btnEdit.addActionListener(e -> editStudent(table, model));
        btnDelete.addActionListener(e -> deleteStudent(table, model));
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadStudentData(model);
        });

        buttons.add(btnAdd);
        buttons.add(btnEdit);
        buttons.add(btnDelete);
        buttons.add(btnRefresh);

        JLabel info = new JLabel("Total Siswa: " + model.getRowCount() + " orang");
        info.setFont(new Font("Poppins", Font.BOLD, 13));
        info.setForeground(TEXT_DARK);
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createMatteBorder(0,1,1,1,new Color(220,220,220)));
        infoPanel.add(info);

        model.addTableModelListener(e -> info.setText("Total Siswa: " + model.getRowCount() + " orang"));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(buttons, BorderLayout.NORTH);
        bottom.add(infoPanel, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private void loadStudentData(DefaultTableModel model) {
        model.setRowCount(0);
        List<Siswa> list = DataStore.getAllSiswa();
        for (Siswa s : list) {
            model.addRow(new Object[]{
                    s.getId_siswa(),
                    s.getNama(),
                    s.getKelas() != null ? s.getKelas() : "-",
                    s.getAlamat(),
                    s.getTelepon(),
                    s.getEmail(),
                    s.getTanggalLahir()
            });
        }
    }

    private void searchStudent(DefaultTableModel model, String keyword) {
        model.setRowCount(0);
        List<Siswa> list = DataStore.searchSiswa(keyword);
        for (Siswa s : list) {
            model.addRow(new Object[]{
                    s.getId_siswa(),
                    s.getNama(),
                    s.getKelas() != null ? s.getKelas() : "-",
                    s.getAlamat(),
                    s.getTelepon(),
                    s.getEmail(),
                    s.getTanggalLahir()
            });
        }
    }

    // ==== ADD STUDENT (ID OTOMATIS) ====
    private void addStudent(DefaultTableModel model) {
        String generatedID = generateStudentID();

        JTextField nama    = new JTextField();
        JTextField paket   = new JTextField();
        JTextField alamat  = new JTextField();
        JTextField telp    = new JTextField();
        JTextField email   = new JTextField();
        JTextField tgl     = new JTextField(LocalDate.now().toString());

        Object[] msg = {
                "ID Siswa (otomatis): " + generatedID,
                "Nama:", nama,
                "Paket (Speaking/Grammar/TOEFL):", paket,
                "Alamat:", alamat,
                "Telepon:", telp,
                "Email:", email,
                "Tanggal Lahir (YYYY-MM-DD):", tgl
        };

        int opt = JOptionPane.showConfirmDialog(this, msg, "Tambah Siswa", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                Siswa s = new Siswa(generatedID, nama.getText(), alamat.getText(),
                        telp.getText(), email.getText(), tgl.getText());
                s.setKelas(paket.getText());
                if (DataStore.addSiswa(s)) {
                    loadStudentData(model);
                    JOptionPane.showMessageDialog(this, "Siswa berhasil ditambahkan");
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menambah siswa", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editStudent(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih siswa terlebih dahulu");
            return;
        }

        String idLama     = (String) model.getValueAt(row, 0);
        String namaLama   = (String) model.getValueAt(row, 1);
        String paketLama  = (String) model.getValueAt(row, 2);
        String alamatLama = (String) model.getValueAt(row, 3);
        String telpLama   = (String) model.getValueAt(row, 4);
        String emailLama  = (String) model.getValueAt(row, 5);
        String tglLama    = (String) model.getValueAt(row, 6);

        JTextField id      = new JTextField(idLama);
        id.setEditable(false);
        id.setBackground(new Color(240, 240, 240));
        JTextField nama    = new JTextField(namaLama);
        JTextField paket   = new JTextField(paketLama.equals("-") ? "" : paketLama);
        JTextField alamat  = new JTextField(alamatLama);
        JTextField telp    = new JTextField(telpLama);
        JTextField email   = new JTextField(emailLama);
        JTextField tgl     = new JTextField(tglLama);

        Object[] msg = {
                "ID Siswa:", id,
                "Nama:", nama,
                "Paket (Speaking/Grammar/TOEFL):", paket,
                "Alamat:", alamat,
                "Telepon:", telp,
                "Email:", email,
                "Tanggal Lahir (YYYY-MM-DD):", tgl
        };

        int opt = JOptionPane.showConfirmDialog(this, msg, "Edit Siswa", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                Siswa s = new Siswa(id.getText(), nama.getText(), alamat.getText(),
                        telp.getText(), email.getText(), tgl.getText());
                s.setKelas(paket.getText());
                if (DataStore.updateSiswa(s)) {
                    loadStudentData(model);
                    JOptionPane.showMessageDialog(this, "Data siswa berhasil diupdate");
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal update siswa", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteStudent(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih siswa terlebih dahulu");
            return;
        }
        String id   = (String) model.getValueAt(row, 0);
        String nama = (String) model.getValueAt(row, 1);
        int c = JOptionPane.showConfirmDialog(this,
                "Hapus siswa " + nama + " (" + id + ")?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            Siswa s = new Siswa(id, nama, null, null, null, null);
            if (DataStore.removeSiswa(s)) {
                loadStudentData(model);
                JOptionPane.showMessageDialog(this, "Siswa berhasil dihapus");
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus siswa", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ======================================================
    // PANEL: MANAJEMEN TUTOR
    // ======================================================
    private JPanel createTutorPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel title = new JLabel("Data Tutor");
        title.setFont(new Font("Poppins", Font.BOLD, 18));
        title.setForeground(TEXT_DARK);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.WEST);

        String[] cols = {"ID", "Username", "Password", "Spesialisasi", "No. Telepon"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        styleTable(table);

        // dikelompokkan berdasar spesialisasi/paket
        TableRowSorter<DefaultTableModel> tutorSorter = new TableRowSorter<>(model);
        table.setRowSorter(tutorSorter);
        List<RowSorter.SortKey> keys = new ArrayList<>();
        keys.add(new RowSorter.SortKey(3, SortOrder.ASCENDING)); // kolom spesialisasi
        tutorSorter.setSortKeys(keys);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));

        loadTutorData(model);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        buttons.setBackground(Color.WHITE);
        buttons.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        JButton add    = createStyledButton("Tambah Tutor", PRIMARY_MEDIUM, Color.WHITE);
        JButton edit   = createStyledButton("Edit Tutor", PRIMARY_MEDIUM, Color.WHITE);
        JButton del    = createStyledButton("Hapus Tutor", PRIMARY_MEDIUM, Color.WHITE);
        JButton ref    = createStyledButton("Refresh", PRIMARY_MEDIUM, Color.WHITE);
        makeActionButtonsWide(add, edit, del, ref);

        add.addActionListener(e -> addTutor(model));
        edit.addActionListener(e -> editTutor(table, model));
        del.addActionListener(e -> deleteTutor(table, model));
        ref.addActionListener(e -> loadTutorData(model));

        buttons.add(add);
        buttons.add(edit);
        buttons.add(del);
        buttons.add(ref);

        JLabel info = new JLabel("Total Tutor: " + model.getRowCount() + " orang");
        info.setFont(new Font("Poppins", Font.BOLD, 13));
        info.setForeground(TEXT_DARK);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        infoPanel.setOpaque(false);
        infoPanel.add(info);

        model.addTableModelListener(e -> info.setText("Total Tutor: " + model.getRowCount() + " orang"));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(buttons, BorderLayout.NORTH);
        bottom.add(infoPanel, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private void loadTutorData(DefaultTableModel model) {
        model.setRowCount(0);
        List<Tutor> list = DataStore.getAllTutors();
        for (Tutor t : list) {
            model.addRow(new Object[]{
                    t.getId_user(),
                    t.getUsername(),
                    t.getPassword(),
                    t.getSpecialization(),
                    t.getPhoneNumber() != null ? t.getPhoneNumber() : "-"
            });
        }
    }

    // ==== ADD TUTOR (ID OTOMATIS) ====
    private void addTutor(DefaultTableModel model) {
        String generatedID = generateTutorID();

        JTextField user = new JTextField();
        JPasswordField pass = new JPasswordField();
        JTextField spes = new JTextField();
        JTextField hp   = new JTextField();

        Object[] msg = {
                "ID Tutor (otomatis): " + generatedID,
                "Username:", user,
                "Password:", pass,
                "Spesialisasi:", spes,
                "No. Telepon:", hp
        };
        int opt = JOptionPane.showConfirmDialog(this, msg, "Tambah Tutor", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            Tutor t = new Tutor(generatedID, user.getText(),
                    new String(pass.getPassword()), spes.getText());
            t.setPhoneNumber(hp.getText());
            if (DataStore.addTutor(t)) {
                loadTutorData(model);
                JOptionPane.showMessageDialog(this, "Tutor berhasil ditambahkan");
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menambah tutor", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editTutor(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih tutor dahulu"); return; }

        String idLama   = (String) model.getValueAt(row, 0);
        String userLama = (String) model.getValueAt(row, 1);
        String passLama = (String) model.getValueAt(row, 2);
        String spesLama = (String) model.getValueAt(row, 3);
        String hpLama   = (String) model.getValueAt(row, 4);

        JTextField id   = new JTextField(idLama);
        id.setEditable(false);
        id.setBackground(new Color(240,240,240));
        JTextField user = new JTextField(userLama);
        JPasswordField pass = new JPasswordField(passLama);
        JTextField spes = new JTextField(spesLama);
        JTextField hp   = new JTextField(hpLama.equals("-") ? "" : hpLama);

        Object[] msg = {
                "ID Tutor:", id,
                "Username:", user,
                "Password:", pass,
                "Spesialisasi:", spes,
                "No. Telepon:", hp
        };
        int opt = JOptionPane.showConfirmDialog(this, msg, "Edit Tutor", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            Tutor t = new Tutor(id.getText(), user.getText(),
                    new String(pass.getPassword()), spes.getText());
            t.setPhoneNumber(hp.getText());
            if (DataStore.updateTutor(t)) {
                loadTutorData(model);
                JOptionPane.showMessageDialog(this, "Data tutor berhasil diupdate");
            } else {
                JOptionPane.showMessageDialog(this, "Gagal update tutor", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteTutor(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih tutor dahulu"); return; }

        String id = (String) model.getValueAt(row, 0);
        String user = (String) model.getValueAt(row, 1);
        int c = JOptionPane.showConfirmDialog(this,
                "Hapus tutor " + user + " (" + id + ")?\nSemua jadwal & nilainya juga ikut terhapus!",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c == JOptionPane.YES_OPTION) {
            Tutor t = new Tutor(id, user, "", "");
            if (DataStore.removeTutor(t)) {
                loadTutorData(model);
                JOptionPane.showMessageDialog(this, "Tutor berhasil dihapus");
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus tutor", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ======================================================
    // PANEL: PEMBAYARAN
    // ======================================================
    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel title = new JLabel("Data Pembayaran");
        title.setFont(new Font("Poppins", Font.BOLD, 18));
        title.setForeground(TEXT_DARK);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filterPanel.setOpaque(false);
        String[] bulanOpt = {"Semua", "Januari","Februari","Maret","April","Mei","Juni",
                "Juli","Agustus","September","Oktober","November","Desember"};
        JComboBox<String> cbBulan = new JComboBox<>(bulanOpt);
        String thnNow = String.valueOf(LocalDate.now().getYear());
        String[] thOpt = {thnNow, String.valueOf(Integer.parseInt(thnNow)-1),
                String.valueOf(Integer.parseInt(thnNow)-2)};
        JComboBox<String> cbTahun = new JComboBox<>(thOpt);
        JLabel lblBulan = new JLabel("Bulan:");
        JLabel lblTahun = new JLabel("Tahun:");
        JButton btnFilter = createStyledButton("Filter", PRIMARY_MEDIUM, Color.WHITE);

        filterPanel.add(lblBulan); filterPanel.add(cbBulan);
        filterPanel.add(lblTahun); filterPanel.add(cbTahun);
        filterPanel.add(btnFilter);

        top.add(title, BorderLayout.WEST);
        top.add(filterPanel, BorderLayout.EAST);

        String[] cols = {"ID", "ID Siswa", "Nama Siswa", "Jumlah", "Tanggal", "Metode", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        styleTable(table);

        // SORTER: supaya kolom Status bisa diklik & urut Pending/Lunas
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        sorter.setComparator(6, (o1, o2) -> {
            String s1 = (o1 == null) ? "" : o1.toString();
            String s2 = (o2 == null) ? "" : o2.toString();
            int v1 = "Belum Lunas".equalsIgnoreCase(s1) ? 0 : 1;
            int v2 = "Belum Lunas".equalsIgnoreCase(s2) ? 0 : 1;
            return Integer.compare(v1, v2);
        });
        table.setRowSorter(sorter);

        // highlight status
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = value != null ? value.toString() : "";
                if (!isSelected) {
                    if ("Lunas".equalsIgnoreCase(status)) {
                        c.setBackground(new Color(214, 234, 248));
                        setForeground(new Color(21, 67, 96));
                    } else {
                        c.setBackground(new Color(255, 243, 205));
                        setForeground(new Color(120, 80, 10));
                    }
                }
                setHorizontalAlignment(CENTER);
                setFont(getFont().deriveFont(Font.BOLD));
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));

        loadPaymentData(model);

        btnFilter.addActionListener(e -> {
            if (cbBulan.getSelectedIndex() == 0) {
                loadPaymentData(model);
            } else {
                int bulan = cbBulan.getSelectedIndex();
                int tahun = Integer.parseInt((String) cbTahun.getSelectedItem());
                filterPaymentByMonth(model, bulan, tahun);
            }
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        buttons.setBackground(Color.WHITE);
        buttons.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                BorderFactory.createEmptyBorder(8,10,8,10)
        ));
        JButton add = createStyledButton("Tambah Pembayaran", PRIMARY_MEDIUM, Color.WHITE);
        JButton edit = createStyledButton("Edit Pembayaran", PRIMARY_MEDIUM, Color.WHITE);
        JButton del  = createStyledButton("Hapus", PRIMARY_MEDIUM, Color.WHITE);
        JButton ref  = createStyledButton("Refresh", PRIMARY_MEDIUM, Color.WHITE);
        makeActionButtonsWide(add, edit, del, ref);

        add.addActionListener(e -> addPayment(model));
        edit.addActionListener(e -> editPayment(table, model));
        del.addActionListener(e -> deletePayment(table, model));
        ref.addActionListener(e -> {
            cbBulan.setSelectedIndex(0);
            loadPaymentData(model);
        });

        buttons.add(add); buttons.add(edit); buttons.add(del); buttons.add(ref);

        JLabel info = new JLabel();
        info.setFont(new Font("Poppins", Font.BOLD, 13));
        info.setForeground(TEXT_DARK);
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createMatteBorder(0,1,1,1,new Color(220,220,220)));
        infoPanel.add(info);

        Runnable updateInfo = () -> {
            long lunas = countPaymentByStatus(model, "Lunas");
            long belumLunas = countPaymentByStatus(model, "Belum Lunas");
            double total = calculateTotalPayment(model);
            info.setText("Lunas: " + lunas + "   |   Belum Lunas: " + belumLunas +
                    "   |   Total: Rp " + String.format("%,.0f", total));
        };
        model.addTableModelListener(e -> updateInfo.run());
        updateInfo.run();

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(buttons, BorderLayout.NORTH);
        bottom.add(infoPanel, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private void loadPaymentData(DefaultTableModel model) {
        model.setRowCount(0);
        List<Pembayaran> list = DataStore.getAllPembayaran();
        for (Pembayaran p : list) {
            model.addRow(new Object[]{
                    p.getId_pembayaran(),
                    p.getId_siswa(),
                    p.getNamaSiswa(),
                    "Rp " + String.format("%,d", (long) p.getJumlah()),
                    p.getTanggalBayar(),
                    p.getMetodePembayaran(),
                    p.getStatus()
            });
        }
    }

    private void filterPaymentByMonth(DefaultTableModel model, int bulan, int tahun) {
        model.setRowCount(0);
        List<Pembayaran> list = DataStore.getPembayaranByMonth(bulan, tahun);
        for (Pembayaran p : list) {
            model.addRow(new Object[]{
                    p.getId_pembayaran(),
                    p.getId_siswa(),
                    p.getNamaSiswa(),
                    "Rp " + String.format("%,d", (long) p.getJumlah()),
                    p.getTanggalBayar(),
                    p.getMetodePembayaran(),
                    p.getStatus()
            });
        }
    }

    // ==== ADD PEMBAYARAN (ID OTOMATIS) ====
    private void addPayment(DefaultTableModel model) {
        String generatedID = generatePembayaranID();

        JTextField ids = new JTextField();
        JTextField nama= new JTextField();
        JTextField jml = new JTextField();
        JTextField tgl = new JTextField(LocalDate.now().toString());
        JComboBox<String> metode = new JComboBox<>(new String[]{"Transfer Bank","Cash","E-Wallet"});
        JComboBox<String> status = new JComboBox<>(new String[]{"Lunas","Belum Lunas"});

        Object[] msg = {
                "ID Pembayaran (otomatis): " + generatedID,
                "ID Siswa:", ids,
                "Nama Siswa:", nama,
                "Jumlah:", jml,
                "Tanggal (YYYY-MM-DD):", tgl,
                "Metode:", metode,
                "Status:", status
        };

        int opt = JOptionPane.showConfirmDialog(this, msg, "Tambah Pembayaran", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                Pembayaran p = new Pembayaran(
                        generatedID,
                        ids.getText(),
                        nama.getText(),
                        Double.parseDouble(jml.getText()),
                        tgl.getText(),
                        (String) metode.getSelectedItem(),
                        (String) status.getSelectedItem()
                );
                if (DataStore.addPembayaran(p)) {
                    loadPaymentData(model);
                    JOptionPane.showMessageDialog(this, "Pembayaran berhasil ditambahkan");
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menambah pembayaran","Error",JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editPayment(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Pilih pembayaran dahulu"); return; }

        String idLama  = (String) model.getValueAt(row,0);
        String idsLama = (String) model.getValueAt(row,1);
        String namaLama= (String) model.getValueAt(row,2);
        String jmlLama = ((String) model.getValueAt(row,3)).replace("Rp ","").replace(".","").replace(",","");
        String tglLama = (String) model.getValueAt(row,4);
        String metLama = (String) model.getValueAt(row,5);
        String stLama  = (String) model.getValueAt(row,6);

        JTextField id  = new JTextField(idLama);
        id.setEditable(false);
        id.setBackground(new Color(240,240,240));
        JTextField ids = new JTextField(idsLama);
        JTextField nama= new JTextField(namaLama);
        JTextField jml = new JTextField(jmlLama);
        JTextField tgl = new JTextField(tglLama);
        JComboBox<String> metode = new JComboBox<>(new String[]{"Transfer Bank","Cash","E-Wallet"});
        metode.setSelectedItem(metLama);
        JComboBox<String> status = new JComboBox<>(new String[]{"Lunas","Belum Lunas"});
        status.setSelectedItem(stLama);

        Object[] msg = {
                "ID Pembayaran:", id,
                "ID Siswa:", ids,
                "Nama Siswa:", nama,
                "Jumlah:", jml,
                "Tanggal (YYYY-MM-DD):", tgl,
                "Metode:", metode,
                "Status:", status
        };

        int opt = JOptionPane.showConfirmDialog(this, msg, "Edit Pembayaran", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                Pembayaran p = new Pembayaran(
                        id.getText(),
                        ids.getText(),
                        nama.getText(),
                        Double.parseDouble(jml.getText()),
                        tgl.getText(),
                        (String) metode.getSelectedItem(),
                        (String) status.getSelectedItem()
                );
                if (DataStore.updatePembayaran(p)) {
                    loadPaymentData(model);
                    JOptionPane.showMessageDialog(this, "Pembayaran berhasil diupdate");
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal update pembayaran","Error",JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deletePayment(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Pilih pembayaran dahulu"); return; }
        String id = (String) model.getValueAt(row,0);
        int c = JOptionPane.showConfirmDialog(this,
                "Hapus pembayaran " + id + " ?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            Pembayaran p = new Pembayaran(id, null, null, 0, null, null, null);
            if (DataStore.removePembayaran(p)) {
                loadPaymentData(model);
                JOptionPane.showMessageDialog(this, "Pembayaran berhasil dihapus");
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus pembayaran","Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private long countPaymentByStatus(DefaultTableModel model, String status) {
        long count = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            if (status.equalsIgnoreCase(model.getValueAt(i, 6).toString())) count++;
        }
        return count;
    }

    private double calculateTotalPayment(DefaultTableModel model) {
        double total = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            String j = model.getValueAt(i, 3).toString()
                    .replace("Rp ", "").replace(".", "").replace(",", "");
            try { total += Double.parseDouble(j); } catch (NumberFormatException ignored) {}
        }
        return total;
    }

    // ======================================================
    // PANEL: KELAS (PAKET)
    // ======================================================
    private JPanel createClassPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel title = new JLabel("Data Kelas / Paket");
        title.setFont(new Font("Poppins", Font.BOLD, 18));
        title.setForeground(TEXT_DARK);
        top.add(title, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchPanel.setOpaque(false);
        JTextField txtSearch = new JTextField(18);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210,210,210)),
                BorderFactory.createEmptyBorder(4,8,4,8)
        ));
        JButton btnCari = createStyledButton("Cari", PRIMARY_MEDIUM, Color.WHITE);
        searchPanel.add(new JLabel("Cari Paket:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnCari);
        top.add(searchPanel, BorderLayout.EAST);

        String[] cols = {"ID Kelas", "Paket", "Tutor", "Batch", "Tahun Ajaran"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));

        loadClassData(model);

        btnCari.addActionListener(e -> {
            String kw = txtSearch.getText().trim();
            if (kw.isEmpty()) loadClassData(model);
            else searchClass(model, kw);
        });
        txtSearch.addActionListener(e -> btnCari.doClick());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        buttons.setBackground(Color.WHITE);
        buttons.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                BorderFactory.createEmptyBorder(8,10,8,10)
        ));
        JButton btnView   = createStyledButton("Lihat Siswa", PRIMARY_MEDIUM, Color.WHITE);
        JButton btnRefresh= createStyledButton("Refresh", PRIMARY_MEDIUM, Color.WHITE);
        makeActionButtonsWide(btnView, btnRefresh);
        btnView.addActionListener(e -> viewClassStudents(table));
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadClassData(model);
        });
        buttons.add(btnView);
        buttons.add(btnRefresh);

        JLabel info = new JLabel("Total Kelas: " + model.getRowCount() + " kelas");
        info.setFont(new Font("Poppins", Font.BOLD, 13));
        info.setForeground(TEXT_DARK);
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        infoPanel.setOpaque(false);
        infoPanel.add(info);
        model.addTableModelListener(e -> info.setText("Total Kelas: " + model.getRowCount() + " kelas"));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(buttons, BorderLayout.NORTH);
        bottom.add(infoPanel, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private void loadClassData(DefaultTableModel model) {
        model.setRowCount(0);
        List<Object[]> list = DataStore.getAllKelas();
        for (Object[] row : list) {
            String idKelas    = (String) row[0];
            String namaKelas  = (String) row[1];
            String waliKelas  = (String) row[4]; // id_user tutor
            String tahunAjar  = (String) row[5];

            String tutorDisplay = mapTutorDisplay(waliKelas);
            String batch = "-";
            if (tahunAjar != null && tahunAjar.length() >= 4) {
                batch = "November " + tahunAjar.substring(0, 4);  // contoh: Batch 2025
            }

            model.addRow(new Object[]{
                    idKelas,
                    namaKelas,
                    tutorDisplay,
                    batch,
                    tahunAjar
            });
        }
    }

    private void searchClass(DefaultTableModel model, String kw) {
        model.setRowCount(0);
        List<Object[]> list = DataStore.searchKelas(kw);
        for (Object[] row : list) {
            String idKelas    = (String) row[0];
            String namaKelas  = (String) row[1];
            String waliKelas  = (String) row[4];
            String tahunAjar  = (String) row[5];

            String tutorDisplay = mapTutorDisplay(waliKelas);
            String batch = "-";
            if (tahunAjar != null && tahunAjar.length() >= 4) {
                batch = "Batch " + tahunAjar.substring(0, 4);
            }

            model.addRow(new Object[]{
                    idKelas,
                    namaKelas,
                    tutorDisplay,
                    batch,
                    tahunAjar
            });
        }
    }

    private void viewClassStudents(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih kelas dahulu"); return; }

        String idKelas = (String) table.getModel().getValueAt(row, 0);
        String namaKelas = (String) table.getModel().getValueAt(row, 1);

        JDialog dialog = new JDialog(this, "Siswa di Kelas: " + namaKelas, true);
        dialog.setSize(720, 500);
        dialog.setLocationRelativeTo(this);

        JPanel main = new JPanel(new BorderLayout(10,10));
        main.setBorder(BorderFactory.createEmptyBorder(16,24,16,24));
        main.setBackground(BACKGROUND_LIGHT);

        JLabel title = new JLabel("Daftar Siswa - " + namaKelas);
        title.setFont(new Font("Poppins", Font.BOLD, 16));
        title.setForeground(TEXT_DARK);

        String[] cols = {"ID Siswa","Nama","Email","Telepon","Tanggal Masuk","Status"};
        DefaultTableModel model = new DefaultTableModel(cols,0){
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        JTable tbl = new JTable(model);
        styleTable(tbl);
        JScrollPane scroll = new JScrollPane(tbl);

        List<Object[]> siswa = DataStore.getSiswaByKelas(idKelas);
        for (Object[] s : siswa) model.addRow(s);

        JLabel info = new JLabel("Total Siswa: " + model.getRowCount() + " orang");
        info.setFont(new Font("Poppins", Font.BOLD, 13));
        info.setHorizontalAlignment(SwingConstants.CENTER);

        main.add(title, BorderLayout.NORTH);
        main.add(scroll, BorderLayout.CENTER);
        main.add(info, BorderLayout.SOUTH);

        dialog.add(main);
        dialog.setVisible(true);
    }

    // mapping wali_kelas (id_user) -> tampilan tutor + spesialisasi
    private String mapTutorDisplay(String idUser) {
        if (idUser == null) return "-";
        switch (idUser) {
            case "T001": return "Alsa - TOEFL";
            case "T002": return "Fista - Speaking";
            case "T003": return "Angel - Grammar";
            default:     return idUser;
        }
    }

    // ======================================================
    // PANEL: MANAJEMEN JADWAL TUTOR
    // ======================================================
    private JPanel createScheduleAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel title = new JLabel("Manajemen Jadwal Tutor");
        title.setFont(new Font("Poppins", Font.BOLD, 18));
        title.setForeground(TEXT_DARK);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.WEST);

        String[] cols = {"ID Jadwal","ID Tutor","Nama Tutor","Hari","Jam Mulai","Jam Selesai","Mapel","Kelas","Ruangan"};
        DefaultTableModel model = new DefaultTableModel(cols,0){
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        JTable table = new JTable(model);
        styleTable(table);

        // ID Jadwal disembunyikan dari tampilan, tapi tetap ada di model
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));

        loadScheduleAdminData(model);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT,10,6));
        buttons.setBackground(Color.WHITE);
        buttons.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                BorderFactory.createEmptyBorder(8,10,8,10)
        ));
        JButton add = createStyledButton("Tambah Jadwal", PRIMARY_MEDIUM, Color.WHITE);
        JButton edit = createStyledButton("Edit Jadwal", PRIMARY_MEDIUM, Color.WHITE);
        JButton del  = createStyledButton("Hapus Jadwal", PRIMARY_MEDIUM, Color.WHITE);
        JButton ref  = createStyledButton("Refresh", PRIMARY_MEDIUM, Color.WHITE);
        makeActionButtonsWide(add, edit, del, ref);

        add.addActionListener(e -> addSchedule(model));
        edit.addActionListener(e -> editSchedule(table, model));
        del.addActionListener(e -> deleteSchedule(table, model));
        ref.addActionListener(e -> loadScheduleAdminData(model));

        buttons.add(add); buttons.add(edit); buttons.add(del); buttons.add(ref);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private void loadScheduleAdminData(DefaultTableModel model) {
        model.setRowCount(0);
        List<Object[]> list = DataStore.getAllJadwalAdmin();
        for (Object[] row : list) model.addRow(row);
    }

    // FORM TAMBAH JADWAL – gunakan ComboBox Tutor agar ID tutor pasti benar
    private void addSchedule(DefaultTableModel model) {
        // ambil semua tutor dari database
        List<Tutor> tutors = DataStore.getAllTutors();
        if (tutors == null || tutors.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Belum ada tutor. Tambah tutor dulu di tab Tutor.",
                    "Informasi",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] opsiTutor = new String[tutors.size()];
        for (int i = 0; i < tutors.size(); i++) {
            Tutor t = tutors.get(i);
            opsiTutor[i] = t.getId_user() + " - " + t.getUsername();
        }
        JComboBox<String> cbTutor = new JComboBox<>(opsiTutor);

        JComboBox<String> hari = new JComboBox<>(new String[]{
                "Senin","Selasa","Rabu","Kamis","Jumat","Sabtu","Minggu"
        });
        JTextField jamMulai = new JTextField("08:00:00");
        JTextField jamSelesai = new JTextField("09:30:00");
        JTextField mapel = new JTextField();
        JTextField kelas = new JTextField();
        JTextField ruangan = new JTextField();

        Object[] msg = {
                "Tutor:", cbTutor,
                "Hari:", hari,
                "Jam Mulai (HH:MM:SS):", jamMulai,
                "Jam Selesai (HH:MM:SS):", jamSelesai,
                "Mata Pelajaran:", mapel,
                "Kelas:", kelas,
                "Ruangan:", ruangan
        };

        int opt = JOptionPane.showConfirmDialog(this, msg, "Tambah Jadwal Tutor", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            String selected = (String) cbTutor.getSelectedItem();
            String idTutor = selected.split(" - ")[0];

            boolean ok = DataStore.addJadwal(
                    idTutor,
                    (String) hari.getSelectedItem(),
                    jamMulai.getText(),
                    jamSelesai.getText(),
                    mapel.getText(),
                    kelas.getText(),
                    ruangan.getText()
            );
            if (ok) {
                loadScheduleAdminData(model);
                JOptionPane.showMessageDialog(this,"Jadwal berhasil ditambahkan");
            } else {
                JOptionPane.showMessageDialog(this,"Gagal menambah jadwal","Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editSchedule(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Pilih jadwal dahulu"); return; }

        int    idJadwal   = Integer.parseInt(model.getValueAt(row,0).toString());
        String idTutorL   = (String) model.getValueAt(row,1);
        String namaTutorL = (String) model.getValueAt(row,2);
        String hariL      = (String) model.getValueAt(row,3);
        String jmL        = (String) model.getValueAt(row,4);
        String jsL        = (String) model.getValueAt(row,5);
        String mapelL     = (String) model.getValueAt(row,6);
        String kelasL     = (String) model.getValueAt(row,7);
        String ruangL     = (String) model.getValueAt(row,8);

        JTextField idTutor = new JTextField(idTutorL);
        JTextField namaTutor = new JTextField(namaTutorL); // hanya tampilan
        JComboBox<String> hari = new JComboBox<>(new String[]{
                "Senin","Selasa","Rabu","Kamis","Jumat","Sabtu","Minggu"
        });
        hari.setSelectedItem(hariL);
        JTextField jamMulai = new JTextField(jmL);
        JTextField jamSelesai = new JTextField(jsL);
        JTextField mapel = new JTextField(mapelL);
        JTextField kelas = new JTextField(kelasL);
        JTextField ruangan = new JTextField(ruangL);

        Object[] msg = {
                "ID Jadwal (auto): " + idJadwal,
                "ID Tutor:", idTutor,
                "Nama Tutor (untuk tampilan saja):", namaTutor,
                "Hari:", hari,
                "Jam Mulai (HH:MM:SS):", jamMulai,
                "Jam Selesai (HH:MM:SS):", jamSelesai,
                "Mata Pelajaran:", mapel,
                "Kelas:", kelas,
                "Ruangan:", ruangan
        };

        int opt = JOptionPane.showConfirmDialog(this, msg, "Edit Jadwal Tutor", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            boolean ok = DataStore.updateJadwal(
                    idJadwal,
                    idTutor.getText(),
                    (String) hari.getSelectedItem(),
                    jamMulai.getText(),
                    jamSelesai.getText(),
                    mapel.getText(),
                    kelas.getText(),
                    ruangan.getText()
            );
            if (ok) {
                loadScheduleAdminData(model);
                JOptionPane.showMessageDialog(this,"Jadwal berhasil diupdate");
            } else {
                JOptionPane.showMessageDialog(this,"Gagal update jadwal","Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSchedule(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Pilih jadwal dahulu"); return; }

        int idJadwal = Integer.parseInt(model.getValueAt(row,0).toString());
        int c = JOptionPane.showConfirmDialog(this,
                "Hapus jadwal dengan ID " + idJadwal + " ?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            if (DataStore.deleteJadwal(idJadwal)) {
                loadScheduleAdminData(model);
                JOptionPane.showMessageDialog(this,"Jadwal berhasil dihapus");
            } else {
                JOptionPane.showMessageDialog(this,"Gagal menghapus jadwal","Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ======================================================
    // HELPER: GENERATOR ID OTOMATIS
    // ======================================================
    private int extractNumber(String str) {
        if (str == null) return 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isDigit(c)) sb.append(c);
        }
        if (sb.length() == 0) return 0;
        try {
            return Integer.parseInt(sb.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    private String generateStudentID() {
        List<Siswa> list = DataStore.getAllSiswa();
        int max = 0;
        for (Siswa s : list) {
            int val = extractNumber(s.getId_siswa());
            if (val > max) max = val;
        }
        max++;
        return String.format("S%03d", max);
    }

    private String generateTutorID() {
        List<Tutor> list = DataStore.getAllTutors();
        int max = 0;
        for (Tutor t : list) {
            int val = extractNumber(t.getId_user());
            if (val > max) max = val;
        }
        max++;
        return String.format("T%03d", max);
    }

    private String generatePembayaranID() {
        List<Pembayaran> list = DataStore.getAllPembayaran();
        int max = 0;
        for (Pembayaran p : list) {
            int val = extractNumber(p.getId_pembayaran());
            if (val > max) max = val;
        }
        max++;
        return String.format("P%03d", max);
    }

    // ======================================================
    // LOGOUT
    // ======================================================
    private void logout() {
        int c = JOptionPane.showConfirmDialog(this,
                "Yakin ingin logout?",
                "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            admin.logout();
            dispose();
            new Login().setVisible(true);
        }
    }
}
