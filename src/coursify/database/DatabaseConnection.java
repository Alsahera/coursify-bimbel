package coursify.database;

import coursify.model.User;
import coursify.model.Pembayaran;
import coursify.model.Siswa;
import coursify.model.Tutor;
import coursify.model.Admin;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {

    // === SESUAIKAN JIKA NAMA DB / USER / PASSWORD BERBEDA ===
    private static final String URL      = "jdbc:mysql://localhost:3306/db_coursify";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private static Connection conn;

    static {
        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("[DB] Koneksi ke database berhasil.");
        } catch (SQLException e) {
            System.err.println("[DB] Gagal konek: " + e.getMessage());
        }
    }

    public static Connection getConnection() {
        return conn;
    }

    // ========================================================
    // LOGIN USER -> kembalikan Admin / Tutor (bukan User abstrak)
    // ========================================================
    public static User getUser(String username, String password) {
        String sql =
                "SELECT u.id_user, u.username, u.password, u.role, " +
                "       a.department, t.specialization, t.phone_number " +
                "FROM users u " +
                "LEFT JOIN admin a ON u.id_user = a.id_user " +
                "LEFT JOIN tutor t ON u.id_user = t.id_user " +
                "WHERE u.username = ? AND u.password = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String idUser  = rs.getString("id_user");
                String uname   = rs.getString("username");
                String pass    = rs.getString("password");
                String role    = rs.getString("role");

                if ("Admin".equalsIgnoreCase(role)) {
                    String dept = rs.getString("department");
                    if (dept == null) dept = "Manajemen";
                    return new Admin(idUser, uname, pass, dept);
                } else if ("Tutor".equalsIgnoreCase(role)) {
                    String spes = rs.getString("specialization");
                    String phone = rs.getString("phone_number");

                    Tutor tutor = new Tutor(idUser, uname, pass,
                            spes != null ? spes : "");
                    tutor.setPhoneNumber(phone);
                    return tutor;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ========================================================
    // SISWA
    // ========================================================

    public static List<Siswa> getAllSiswa() {
        List<Siswa> list = new ArrayList<>();
        // urutkan berdasarkan ID supaya ID baru tetap rapi
        String sql = "SELECT * FROM siswa ORDER BY id_siswa";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Siswa s = new Siswa(
                        rs.getString("id_siswa"),
                        rs.getString("nama"),
                        rs.getString("alamat"),
                        rs.getString("telepon"),
                        rs.getString("email"),
                        rs.getString("tanggal_lahir")  // disimpan sebagai String di model
                );
                s.setKelas(rs.getString("kelas"));
                list.add(s);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean addSiswa(Siswa s) {
        String sqlSiswa = "INSERT INTO siswa " +
                "(id_siswa, nama, alamat, telepon, email, tanggal_lahir, kelas) " +
                "VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sqlSiswa)) {
            ps.setString(1, s.getId_siswa());
            ps.setString(2, s.getNama());
            ps.setString(3, s.getAlamat());
            ps.setString(4, s.getTelepon());
            ps.setString(5, s.getEmail());
            ps.setString(6, s.getTanggalLahir());
            ps.setString(7, s.getKelas());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                // kalau kolom 'kelas' diisi, coba hubungkan ke tabel siswa_kelas
                if (s.getKelas() != null && !s.getKelas().isEmpty()) {
                    // cari id_kelas berdasarkan nama_kelas
                    String sqlCariKelas = "SELECT id_kelas FROM kelas WHERE nama_kelas = ?";
                    try (PreparedStatement psKelas = conn.prepareStatement(sqlCariKelas)) {
                        psKelas.setString(1, s.getKelas());
                        ResultSet rs = psKelas.executeQuery();
                        if (rs.next()) {
                            String idKelas = rs.getString("id_kelas");

                            // buat relasi di siswa_kelas
                            String sqlRelasi = "INSERT INTO siswa_kelas " +
                                    "(id_siswa, id_kelas, tanggal_masuk, status) " +
                                    "VALUES (?,?,CURDATE(),'Aktif')";
                            try (PreparedStatement psRel = conn.prepareStatement(sqlRelasi)) {
                                psRel.setString(1, s.getId_siswa());
                                psRel.setString(2, idKelas);
                                psRel.executeUpdate();
                            }
                        }
                    } catch (SQLException e) {
                        // kalau gagal insert ke siswa_kelas, tetap anggap siswa sudah tersimpan
                        e.printStackTrace();
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateSiswa(Siswa s) {
        // Bisa juga pakai stored procedure sp_update_siswa, tapi ini versi plain SQL
        String sql = "UPDATE siswa SET " +
                "nama=?, alamat=?, telepon=?, email=?, tanggal_lahir=?, kelas=? " +
                "WHERE id_siswa=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getNama());
            ps.setString(2, s.getAlamat());
            ps.setString(3, s.getTelepon());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getTanggalLahir());
            ps.setString(6, s.getKelas());
            ps.setString(7, s.getId_siswa());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removeSiswa(String id) {
        String sql = "DELETE FROM siswa WHERE id_siswa=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Siswa> searchSiswa(String keyword) {
        List<Siswa> list = new ArrayList<>();
        String sql = "SELECT * FROM siswa " +
                     "WHERE id_siswa LIKE ? OR nama LIKE ? OR email LIKE ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Siswa s = new Siswa(
                        rs.getString("id_siswa"),
                        rs.getString("nama"),
                        rs.getString("alamat"),
                        rs.getString("telepon"),
                        rs.getString("email"),
                        rs.getString("tanggal_lahir")
                );
                s.setKelas(rs.getString("kelas"));
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ========================================================
    // PEMBAYARAN
    // ========================================================

    public static List<Pembayaran> getAllPembayaran() {
        List<Pembayaran> list = new ArrayList<>();
        String sql =
                "SELECT p.*, s.nama AS nama_siswa " +
                "FROM pembayaran p " +
                "LEFT JOIN siswa s ON p.id_siswa = s.id_siswa " +
                "ORDER BY p.tanggal_bayar DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pembayaran p = new Pembayaran(
                        rs.getString("id_pembayaran"),
                        rs.getString("id_siswa"),
                        rs.getString("nama_siswa") != null ? rs.getString("nama_siswa") : "-",
                        rs.getDouble("jumlah"),
                        rs.getString("tanggal_bayar"),
                        rs.getString("metode_pembayaran"),
                        rs.getString("status")
                );
                list.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean addPembayaran(Pembayaran p) {
        String sql = "INSERT INTO pembayaran " +
                "(id_pembayaran, id_siswa, jumlah, tanggal_bayar, metode_pembayaran, status) " +
                "VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getId_pembayaran());
            ps.setString(2, p.getId_siswa());
            ps.setDouble(3, p.getJumlah());
            ps.setString(4, p.getTanggalBayar());
            ps.setString(5, p.getMetodePembayaran());
            ps.setString(6, p.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updatePembayaran(Pembayaran p) {
        String sql = "UPDATE pembayaran SET " +
                "id_siswa=?, jumlah=?, tanggal_bayar=?, metode_pembayaran=?, status=? " +
                "WHERE id_pembayaran=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getId_siswa());
            ps.setDouble(2, p.getJumlah());
            ps.setString(3, p.getTanggalBayar());
            ps.setString(4, p.getMetodePembayaran());
            ps.setString(5, p.getStatus());
            ps.setString(6, p.getId_pembayaran());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removePembayaran(String id) {
        String sql = "DELETE FROM pembayaran WHERE id_pembayaran=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Pembayaran> getPembayaranByMonth(int bulan, int tahun) {
        List<Pembayaran> list = new ArrayList<>();
        String sql =
                "SELECT p.*, s.nama AS nama_siswa " +
                "FROM pembayaran p " +
                "LEFT JOIN siswa s ON p.id_siswa = s.id_siswa " +
                "WHERE MONTH(p.tanggal_bayar) = ? AND YEAR(p.tanggal_bayar) = ? " +
                "ORDER BY p.tanggal_bayar";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bulan);
            ps.setInt(2, tahun);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Pembayaran p = new Pembayaran(
                        rs.getString("id_pembayaran"),
                        rs.getString("id_siswa"),
                        rs.getString("nama_siswa") != null ? rs.getString("nama_siswa") : "-",
                        rs.getDouble("jumlah"),
                        rs.getString("tanggal_bayar"),
                        rs.getString("metode_pembayaran"),
                        rs.getString("status")
                );
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Object[] getStatistikPembayaran(int bulan, int tahun) {
        String sql =
                "SELECT COUNT(*) AS total_transaksi, COALESCE(SUM(jumlah),0) AS total_jumlah " +
                "FROM pembayaran " +
                "WHERE MONTH(tanggal_bayar) = ? AND YEAR(tanggal_bayar) = ?";
        long totalTransaksi = 0;
        double totalJumlah = 0;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bulan);
            ps.setInt(2, tahun);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                totalTransaksi = rs.getLong("total_transaksi");
                totalJumlah = rs.getDouble("total_jumlah");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Object[]{totalTransaksi, totalJumlah};
    }

    // ========================================================
    // TUTOR
    // ========================================================

    public static List<Tutor> getAllTutors() {
        List<Tutor> list = new ArrayList<>();
        String sql =
                "SELECT u.id_user, u.username, u.password, t.specialization, t.phone_number " +
                "FROM users u " +
                "JOIN tutor t ON u.id_user = t.id_user " +
                "WHERE u.role = 'Tutor' " +
                "ORDER BY u.username";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Tutor t = new Tutor(
                        rs.getString("id_user"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("specialization")
                );
                t.setPhoneNumber(rs.getString("phone_number"));
                list.add(t);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean addTutor(Tutor t) {
        // gunakan stored procedure sp_tambah_tutor jika mau, di sini pakai SQL biasa
        try {
            conn.setAutoCommit(false);

            // insert ke tabel users
            String sqlUsers = "INSERT INTO users (id_user, username, password, role) VALUES (?,?,?, 'Tutor')";
            try (PreparedStatement ps = conn.prepareStatement(sqlUsers)) {
                ps.setString(1, t.getId_user());
                ps.setString(2, t.getUsername());
                ps.setString(3, t.getPassword());
                ps.executeUpdate();
            }

            // insert ke tabel tutor
            String sqlTutor = "INSERT INTO tutor (id_user, specialization, phone_number) VALUES (?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlTutor)) {
                ps.setString(1, t.getId_user());
                ps.setString(2, t.getSpecialization());
                ps.setString(3, t.getPhoneNumber());
                ps.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try { conn.rollback(); } catch (SQLException ignored) {}
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            return false;
        }
    }

    public static boolean updateTutor(Tutor t) {
        try {
            conn.setAutoCommit(false);

            String sqlUser = "UPDATE users SET username=?, password=? WHERE id_user=?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUser)) {
                ps.setString(1, t.getUsername());
                ps.setString(2, t.getPassword());
                ps.setString(3, t.getId_user());
                ps.executeUpdate();
            }

            String sqlTutor = "UPDATE tutor SET specialization=?, phone_number=? WHERE id_user=?";
            try (PreparedStatement ps = conn.prepareStatement(sqlTutor)) {
                ps.setString(1, t.getSpecialization());
                ps.setString(2, t.getPhoneNumber());
                ps.setString(3, t.getId_user());
                ps.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try { conn.rollback(); } catch (SQLException ignored) {}
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            return false;
        }
    }

    public static boolean removeTutor(String idUser) {
        // constraint ON DELETE CASCADE di tabel tutor/users akan mengurus sisanya
        String sql = "DELETE FROM users WHERE id_user=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idUser);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ========================================================
    // KELAS
    // ========================================================

    public static List<Object[]> getAllKelas() {
        List<Object[]> list = new ArrayList<>();
        String sql =
                "SELECT id_kelas, nama_kelas, tingkat, jurusan, wali_kelas, tahun_ajaran " +
                "FROM kelas ORDER BY tingkat, nama_kelas";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Object[]{
                        rs.getString("id_kelas"),
                        rs.getString("nama_kelas"),
                        rs.getString("tingkat"),
                        rs.getString("jurusan"),
                        rs.getString("wali_kelas"),
                        rs.getString("tahun_ajaran")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Digunakan di TutorMainFrame -> manajemen kelas tutor
    public static List<Object[]> getKelasByTutor(String idTutor) {
        List<Object[]> list = new ArrayList<>();
        String sql =
                "SELECT k.id_kelas, k.nama_kelas, k.tingkat, k.tahun_ajaran, " +
                "       COUNT(DISTINCT sk.id_siswa) AS jumlah_siswa " +
                "FROM jadwal_mengajar j " +
                "JOIN kelas k ON j.id_kelas = k.id_kelas " +
                "LEFT JOIN siswa_kelas sk ON k.id_kelas = sk.id_kelas AND sk.status = 'Aktif' " +
                "WHERE j.id_tutor = ? " +
                "GROUP BY k.id_kelas, k.nama_kelas, k.tingkat, k.tahun_ajaran " +
                "ORDER BY k.tingkat, k.nama_kelas";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idTutor);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Object[]{
                        rs.getString("id_kelas"),
                        rs.getString("nama_kelas"),
                        rs.getString("tingkat"),
                        rs.getString("tahun_ajaran"),
                        rs.getInt("jumlah_siswa")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Object[]> getSiswaByKelas(String idKelas) {
        List<Object[]> list = new ArrayList<>();
        String sql =
                "SELECT s.id_siswa, s.nama, s.email, s.telepon, sk.tanggal_masuk, sk.status " +
                "FROM siswa_kelas sk " +
                "JOIN siswa s ON sk.id_siswa = s.id_siswa " +
                "WHERE sk.id_kelas = ? " +
                "ORDER BY s.nama";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idKelas);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Object[]{
                        rs.getString("id_siswa"),
                        rs.getString("nama"),
                        rs.getString("email"),
                        rs.getString("telepon"),
                        rs.getString("tanggal_masuk"),
                        rs.getString("status")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Object[]> searchKelas(String keyword) {
        List<Object[]> list = new ArrayList<>();
        String sql =
                "SELECT id_kelas, nama_kelas, tingkat, jurusan, wali_kelas, tahun_ajaran " +
                "FROM kelas " +
                "WHERE id_kelas LIKE ? OR nama_kelas LIKE ? OR tingkat LIKE ? OR jurusan LIKE ? " +
                "ORDER BY tingkat, nama_kelas";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                        rs.getString("id_kelas"),
                        rs.getString("nama_kelas"),
                        rs.getString("tingkat"),
                        rs.getString("jurusan"),
                        rs.getString("wali_kelas"),
                        rs.getString("tahun_ajaran")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ========================================================
    // NILAI
    // ========================================================

    public static boolean inputNilaiKelas(String idSiswa,
                                          String idTutor,
                                          String idKelas,
                                          String mataPelajaran,
                                          int nilai,
                                          String keterangan,
                                          String semester,
                                          String tahunAjaran) {

        String sql = "CALL sp_input_nilai_kelas(?,?,?,?,?,?,?,?)";

        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, idSiswa);
            cs.setString(2, idTutor);
            cs.setString(3, idKelas);
            cs.setString(4, mataPelajaran);
            cs.setInt(5, nilai);
            cs.setString(6, keterangan);
            cs.setString(7, semester);
            cs.setString(8, tahunAjaran);
            cs.executeUpdate();
            return true;
        } catch (SQLException e) {
            // kalau prosedur belum ada, fallback ke INSERT biasa
            e.printStackTrace();
            String insert =
                    "INSERT INTO nilai " +
                    "(id_siswa, id_tutor, id_kelas, mata_pelajaran, nilai, keterangan, semester, tahun_ajaran) " +
                    "VALUES (?,?,?,?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                ps.setString(1, idSiswa);
                ps.setString(2, idTutor);
                ps.setString(3, idKelas);
                ps.setString(4, mataPelajaran);
                ps.setInt(5, nilai);
                ps.setString(6, keterangan);
                ps.setString(7, semester);
                ps.setString(8, tahunAjaran);
                return ps.executeUpdate() > 0;
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    public static List<Object[]> getNilaiByKelas(String idKelas, String mataPelajaran) {
        List<Object[]> list = new ArrayList<>();
        String sql =
                "SELECT n.id_siswa, s.nama, n.nilai, n.keterangan, n.semester, n.tanggal_input " +
                "FROM nilai n " +
                "JOIN siswa s ON n.id_siswa = s.id_siswa " +
                "WHERE n.id_kelas = ? AND n.mata_pelajaran = ? " +
                "ORDER BY s.nama";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idKelas);
            ps.setString(2, mataPelajaran);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Object[]{
                        rs.getString("id_siswa"),
                        rs.getString("nama"),
                        rs.getInt("nilai"),
                        rs.getString("keterangan"),
                        rs.getString("semester"),
                        rs.getString("tanggal_input")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ========================================================
    // JADWAL TUTOR (untuk halaman Tutor)
    // ========================================================

    public static List<Object[]> getJadwalByTutor(String idTutor) {
        List<Object[]> list = new ArrayList<>();
        String sql =
                "SELECT hari, jam_mulai, jam_selesai, mata_pelajaran, kelas, ruangan " +
                "FROM jadwal_mengajar " +
                "WHERE id_tutor = ? " +
                "ORDER BY FIELD(hari, 'Senin','Selasa','Rabu','Kamis','Jumat','Sabtu','Minggu'), jam_mulai";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idTutor);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String jam = rs.getString("jam_mulai") + " - " + rs.getString("jam_selesai");
                list.add(new Object[]{
                        rs.getString("hari"),
                        jam,
                        rs.getString("mata_pelajaran"),
                        rs.getString("kelas"),
                        rs.getString("ruangan")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ========================================================
    // JADWAL TUTOR (untuk halaman Admin)
    // ========================================================

    public static List<Object[]> getAllJadwalAdmin() {
        List<Object[]> list = new ArrayList<>();
        String sql =
                "SELECT j.id_jadwal, j.id_tutor, u.username AS nama_tutor, " +
                "       j.hari, j.jam_mulai, j.jam_selesai, " +
                "       j.mata_pelajaran, j.kelas, j.ruangan " +
                "FROM jadwal_mengajar j " +
                "JOIN users u ON j.id_tutor = u.id_user " +
                "ORDER BY FIELD(j.hari, 'Senin','Selasa','Rabu','Kamis','Jumat','Sabtu','Minggu'), j.jam_mulai";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Object[]{
                        rs.getInt("id_jadwal"),
                        rs.getString("id_tutor"),
                        rs.getString("nama_tutor"),
                        rs.getString("hari"),
                        rs.getString("jam_mulai"),
                        rs.getString("jam_selesai"),
                        rs.getString("mata_pelajaran"),
                        rs.getString("kelas"),
                        rs.getString("ruangan")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean addJadwal(String idTutor,
                                    String hari,
                                    String jamMulai,
                                    String jamSelesai,
                                    String mataPelajaran,
                                    String kelas,
                                    String ruangan) {

        String sql =
                "INSERT INTO jadwal_mengajar " +
                "(id_tutor, hari, jam_mulai, jam_selesai, mata_pelajaran, kelas, ruangan) " +
                "VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idTutor);
            ps.setString(2, hari);
            ps.setString(3, jamMulai);
            ps.setString(4, jamSelesai);
            ps.setString(5, mataPelajaran);
            ps.setString(6, kelas);
            ps.setString(7, ruangan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateJadwal(int idJadwal,
                                       String idTutor,
                                       String hari,
                                       String jamMulai,
                                       String jamSelesai,
                                       String mataPelajaran,
                                       String kelas,
                                       String ruangan) {

        String sql =
                "UPDATE jadwal_mengajar SET " +
                "id_tutor=?, hari=?, jam_mulai=?, jam_selesai=?, " +
                "mata_pelajaran=?, kelas=?, ruangan=? " +
                "WHERE id_jadwal=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idTutor);
            ps.setString(2, hari);
            ps.setString(3, jamMulai);
            ps.setString(4, jamSelesai);
            ps.setString(5, mataPelajaran);
            ps.setString(6, kelas);
            ps.setString(7, ruangan);
            ps.setInt(8, idJadwal);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteJadwal(int idJadwal) {
        String sql = "DELETE FROM jadwal_mengajar WHERE id_jadwal=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idJadwal);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ========================================================
    // UTILITY
    // ========================================================

    public static boolean testConnection() {
        try {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
