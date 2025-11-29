package coursify.database;

import coursify.database.DatabaseConnection;
import coursify.model.User;
import coursify.model.Pembayaran;
import coursify.model.Siswa;
import coursify.model.Tutor;
import java.util.List;

public class DataStore {

    // ========================================
    // OPERASI USER (LOGIN)
    // ========================================

    public static User getUser(String username, String password) {
        return DatabaseConnection.getUser(username, password);
    }

    // ========================================
    // OPERASI SISWA
    // ========================================

    public static List<Siswa> getAllSiswa() {
        return DatabaseConnection.getAllSiswa();
    }

    public static boolean addSiswa(Siswa siswa) {
        boolean success = DatabaseConnection.addSiswa(siswa);
        if (success) {
            System.out.println("Siswa berhasil ditambahkan: " + siswa.getNama());
        }
        return success;
    }

    public static boolean updateSiswa(Siswa siswa) {
        boolean success = DatabaseConnection.updateSiswa(siswa);
        if (success) {
            System.out.println("Siswa berhasil diupdate: " + siswa.getNama());
        }
        return success;
    }

    public static boolean removeSiswa(Siswa siswa) {
        boolean success = DatabaseConnection.removeSiswa(siswa.getId_siswa());
        if (success) {
            System.out.println("Siswa berhasil dihapus: " + siswa.getNama());
        }
        return success;
    }

    public static List<Siswa> searchSiswa(String keyword) {
        return DatabaseConnection.searchSiswa(keyword);
    }

    // ========================================
    // OPERASI PEMBAYARAN
    // ========================================

    public static List<Pembayaran> getAllPembayaran() {
        return DatabaseConnection.getAllPembayaran();
    }

    public static boolean addPembayaran(Pembayaran pembayaran) {
        boolean success = DatabaseConnection.addPembayaran(pembayaran);
        if (success) {
            System.out.println("Pembayaran berhasil ditambahkan: " + pembayaran.getId_pembayaran());
        }
        return success;
    }

    public static boolean updatePembayaran(Pembayaran pembayaran) {
        boolean success = DatabaseConnection.updatePembayaran(pembayaran);
        if (success) {
            System.out.println("Pembayaran berhasil diupdate: " + pembayaran.getId_pembayaran());
        }
        return success;
    }

    public static boolean removePembayaran(Pembayaran pembayaran) {
        boolean success = DatabaseConnection.removePembayaran(pembayaran.getId_pembayaran());
        if (success) {
            System.out.println("Pembayaran berhasil dihapus: " + pembayaran.getId_pembayaran());
        }
        return success;
    }

    /**
     * Opsional – kalau sudah kamu buat di DatabaseConnection.
     * Jika belum ada, boleh dihapus dua method di bawah.
     */
    public static List<Pembayaran> getPembayaranByMonth(int bulan, int tahun) {
        return DatabaseConnection.getPembayaranByMonth(bulan, tahun);
    }

    public static Object[] getStatistikPembayaran(int bulan, int tahun) {
        return DatabaseConnection.getStatistikPembayaran(bulan, tahun);
    }

    // ========================================
    // OPERASI TUTOR
    // ========================================

    public static List<Tutor> getAllTutors() {
        return DatabaseConnection.getAllTutors();
    }

    public static boolean addTutor(Tutor tutor) {
        boolean success = DatabaseConnection.addTutor(tutor);
        if (success) {
            System.out.println("Tutor berhasil ditambahkan: " + tutor.getUsername());
        }
        return success;
    }

    public static boolean updateTutor(Tutor tutor) {
        boolean success = DatabaseConnection.updateTutor(tutor);
        if (success) {
            System.out.println("Tutor berhasil diupdate: " + tutor.getUsername());
        }
        return success;
    }

    public static boolean removeTutor(Tutor tutor) {
        boolean success = DatabaseConnection.removeTutor(tutor.getId_user());
        if (success) {
            System.out.println("Tutor berhasil dihapus: " + tutor.getUsername());
        }
        return success;
    }

    // ========================================
    // OPERASI KELAS
    // ========================================

    public static List<Object[]> getAllKelas() {
        return DatabaseConnection.getAllKelas();
    }

    /**
     * Opsional – tergantung implementasi di DatabaseConnection.
     * Jika di sana belum ada, kamu bisa tambahkan juga.
     */
    public static List<Object[]> getKelasByTutor(String id_tutor) {
        return DatabaseConnection.getKelasByTutor(id_tutor);
    }

    public static List<Object[]> getSiswaByKelas(String id_kelas) {
        return DatabaseConnection.getSiswaByKelas(id_kelas);
    }

    public static List<Object[]> searchKelas(String keyword) {
        return DatabaseConnection.searchKelas(keyword);
    }

    // ========================================
    // OPERASI NILAI
    // ========================================
    /**
     * CATATAN PENYESUAIAN DENGAN UI REKAP NILAI:
     *
     * Di layar "Rekap Nilai", kolom yang tampil:
     * - MidTest
     * - FinalTest
     * - Batch
     *
     * Dengan skema lama di database:
     * - nilai      -> dipakai sebagai MIDTEST
     * - keterangan -> dipakai sebagai FINALTEST (disimpan sebagai teks, misal "80")
     * - semester   -> dipakai sebagai BATCH (misal "Batch 1" atau "2025-1")
     *
     * Jadi:
     *  - Saat input nilai, kirim:
     *      nilai      = nilai MidTest (int)
     *      keterangan = nilai FinalTest (String, bisa angka)
     *      semester   = Batch
     *  - Saat ambil dengan getNilaiByKelas():
     *      row[2] = nilai  (MidTest)
     *      row[3] = keterangan (FinalTest)
     *      row[4] = semester (Batch)
     */
    public static boolean inputNilaiKelas(String id_siswa,
                                          String id_tutor,
                                          String id_kelas,
                                          String mata_pelajaran,
                                          int nilai,
                                          String keterangan,
                                          String semester,
                                          String tahun_ajaran) {

        boolean success = DatabaseConnection.inputNilaiKelas(
                id_siswa, id_tutor, id_kelas,
                mata_pelajaran, nilai, keterangan,
                semester, tahun_ajaran
        );

        if (success) {
            System.out.println("Nilai berhasil diinput untuk siswa: " + id_siswa);
        }
        return success;
    }

    /**
     * Return format (per baris Object[]):
     * [0] = id_siswa
     * [1] = nama_siswa
     * [2] = nilai (di UI dipakai sebagai MidTest)
     * [3] = keterangan (di UI dipakai sebagai FinalTest)
     * [4] = semester (di UI dipakai sebagai Batch)
     * [5] = tanggal_input
     */
    public static List<Object[]> getNilaiByKelas(String id_kelas, String mata_pelajaran) {
        return DatabaseConnection.getNilaiByKelas(id_kelas, mata_pelajaran);
    }

    // ========================================
    // OPERASI JADWAL (TUTOR VIEW)
    // ========================================

    public static List<Object[]> getJadwalByTutor(String id_tutor) {
        return DatabaseConnection.getJadwalByTutor(id_tutor);
    }

    // ========================================
    // OPERASI JADWAL (ADMIN VIEW)
    // ========================================

    public static List<Object[]> getAllJadwalAdmin() {
        return DatabaseConnection.getAllJadwalAdmin();
    }

    public static boolean addJadwal(String idTutor,
                                    String hari,
                                    String jamMulai,
                                    String jamSelesai,
                                    String mataPelajaran,
                                    String kelas,
                                    String ruangan) {

        boolean success = DatabaseConnection.addJadwal(
                idTutor, hari, jamMulai, jamSelesai,
                mataPelajaran, kelas, ruangan
        );

        if (success) {
            System.out.println("Jadwal baru berhasil ditambahkan untuk tutor: " + idTutor);
        }
        return success;
    }

    public static boolean updateJadwal(int idJadwal,
                                       String idTutor,
                                       String hari,
                                       String jamMulai,
                                       String jamSelesai,
                                       String mataPelajaran,
                                       String kelas,
                                       String ruangan) {

        boolean success = DatabaseConnection.updateJadwal(
                idJadwal, idTutor, hari, jamMulai, jamSelesai,
                mataPelajaran, kelas, ruangan
        );

        if (success) {
            System.out.println("Jadwal berhasil diupdate. ID: " + idJadwal);
        }
        return success;
    }

    public static boolean deleteJadwal(int idJadwal) {
        boolean success = DatabaseConnection.deleteJadwal(idJadwal);
        if (success) {
            System.out.println("Jadwal berhasil dihapus. ID: " + idJadwal);
        }
        return success;
    }

    // ========================================
    // UTILITY METHODS
    // ========================================

    public static boolean testConnection() {
        return DatabaseConnection.testConnection();
    }

    public static void closeConnection() {
        DatabaseConnection.closeConnection();
    }
}
