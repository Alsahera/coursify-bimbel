package coursify.model;

public class Pembayaran {
    // Encapsulation: Atribut private
    private String id_pembayaran;
    private String id_siswa;
    private String namaSiswa;
    private double jumlah;
    private String tanggalBayar;
    private String metodePembayaran;
    private String status;
    
    // Constructor
    public Pembayaran(String id_pembayaran, String id_siswa, String namaSiswa, 
                      double jumlah, String tanggalBayar, String metodePembayaran, String status) {
        this.id_pembayaran = id_pembayaran;
        this.id_siswa = id_siswa;
        this.namaSiswa = namaSiswa;
        this.jumlah = jumlah;
        this.tanggalBayar = tanggalBayar;
        this.metodePembayaran = metodePembayaran;
        this.status = status;
    }
    
    // Getter dan Setter untuk Encapsulation
    public String getId_pembayaran() {
        return id_pembayaran;
    }
    
    public void setId_pembayaran(String id_pembayaran) {
        this.id_pembayaran = id_pembayaran;
    }
    
    public String getId_siswa() {
        return id_siswa;
    }
    
    public void setId_siswa(String id_siswa) {
        this.id_siswa = id_siswa;
    }
    
    public String getNamaSiswa() {
        return namaSiswa;
    }
    
    public void setNamaSiswa(String namaSiswa) {
        this.namaSiswa = namaSiswa;
    }
    
    public double getJumlah() {
        return jumlah;
    }
    
    public void setJumlah(double jumlah) {
        this.jumlah = jumlah;
    }
    
    public String getTanggalBayar() {
        return tanggalBayar;
    }
    
    public void setTanggalBayar(String tanggalBayar) {
        this.tanggalBayar = tanggalBayar;
    }
    
    public String getMetodePembayaran() {
        return metodePembayaran;
    }
    
    public void setMetodePembayaran(String metodePembayaran) {
        this.metodePembayaran = metodePembayaran;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "ID: " + id_pembayaran + ", Siswa: " + namaSiswa + ", Jumlah: Rp " + jumlah + ", Status: " + status;
    }
}