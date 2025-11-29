package coursify.model;

public class Siswa {
    // Encapsulation: Atribut private
    private String id_siswa;
    private String nama;
    private String alamat;
    private String telepon;
    private String email;
    private String tanggalLahir;
    private String kelas; // Tambahan field kelas
    
    // Constructor
    public Siswa(String id_siswa, String nama, String alamat, String telepon, String email, String tanggalLahir) {
        this.id_siswa = id_siswa;
        this.nama = nama;
        this.alamat = alamat;
        this.telepon = telepon;
        this.email = email;
        this.tanggalLahir = tanggalLahir;
        this.kelas = "";
    }
    
    // Getter dan Setter untuk Encapsulation
    public String getId_siswa() {
        return id_siswa;
    }
    
    public void setId_siswa(String id_siswa) {
        this.id_siswa = id_siswa;
    }
    
    public String getNama() {
        return nama;
    }
    
    public void setNama(String nama) {
        this.nama = nama;
    }
    
    public String getAlamat() {
        return alamat;
    }
    
    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
    
    public String getTelepon() {
        return telepon;
    }
    
    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getTanggalLahir() {
        return tanggalLahir;
    }
    
    public void setTanggalLahir(String tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }
    
    public String getKelas() {
        return kelas;
    }
    
    public void setKelas(String kelas) {
        this.kelas = kelas;
    }
    
    @Override
    public String toString() {
        return "ID: " + id_siswa + ", Nama: " + nama + ", Kelas: " + kelas + ", Telepon: " + telepon;
    }
}