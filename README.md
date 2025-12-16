# 📚 Coursify - Sistem Manajemen Bimbingan Belajar

Aplikasi desktop berbasis Java untuk mengelola bimbingan belajar dengan fitur lengkap manajemen siswa, tutor, jadwal, nilai, dan pembayaran.

> **📚 Project**: UAS Semester 3 - Mata Kuliah Praktik Pemrograman Berorientasi Objek

## 🎯 Fitur Utama

### Admin Dashboard
- ✅ **Manajemen Siswa** - Tambah, edit, hapus data siswa
- ✅ **Manajemen Tutor** - Kelola data tutor dan spesialisasinya
- ✅ **Manajemen Pembayaran** - Lacak status pembayaran siswa
- ✅ **Manajemen Kelas/Paket** - Kelola kelas (Speaking, Grammar, TOEFL)
- ✅ **Jadwal Tutor** - Input dan atur jadwal mengajar

### Tutor Dashboard
- ✅ **Manajemen Kelas** - Lihat kelas yang diajar
- ✅ **Input & Rekap Nilai** - Input nilai mid-test dan final-test per mata pelajaran
- ✅ **Daftar Siswa** - Lihat siswa di setiap paket
- ✅ **Jadwal Mengajar** - Cek jadwal personal

### Fitur Umum
- 🔐 **Authentication** - Login dengan role Admin & Tutor
- 📊 **Database MySQL** - Penyimpanan data terpusat
- 🎨 **Modern UI** - Interface yang user-friendly dengan Poppins font

---

## 💻 Tech Stack

- **Language**: Java (Swing GUI)
- **Database**: MySQL
- **IDE**: NetBeans / IntelliJ IDEA
- **Driver**: MySQL Connector Java
- **Design Pattern**: MVC (Model-View-Controller)
- **Paradigm**: Pemrograman Berorientasi Objek (OOP) - Inheritance, Polymorphism, Encapsulation

---

## 📋 Requirements

### Software yang Diperlukan
- Java JDK 8 atau lebih baru
- MySQL Server 5.7+
- Git

### Library
- MySQL Connector Java (sudah included di `lib/`)

---

## 🚀 Quick Start

### 1. Setup Database

```bash
# Buka MySQL
mysql -u root -p

# Buat database
CREATE DATABASE db_coursify;

# Import SQL file
mysql -u root -p db_coursify < database/db_coursify.sql
```

Atau import langsung dari MySQL Workbench:
- Buka `database/db_coursify.sql`
- Execute

### 2. Konfigurasi Database Connection

Edit file `src/coursify/database/DatabaseConnection.java`:

```java
private static final String URL      = "jdbc:mysql://localhost:3306/db_coursify";
private static final String USERNAME = "root";
private static final String PASSWORD = ""; // Sesuaikan password MySQL Anda
```

### 3. Jalankan Aplikasi

Di IDE (NetBeans/IntelliJ):
- Klik tombol **Run**
- Atau buka Terminal:

```bash
cd src
javac coursify/Main.java
java coursify.Main
```

---

## 👥 Default Login Credentials

| Role  | Username | Password |
|-------|----------|----------|
| Admin | admin    | admin123 |
| Tutor | Alsa     | 123      |
| Tutor | Fista    | 123      |
| Tutor | Angel    | 123      |

---

## 📁 Project Structure

```
coursify-bimbel/
├── src/
│   └── coursify/
│       ├── Main.java                      # Entry point aplikasi
│       ├── view/
│       │   ├── Login.java                 # Login form
│       │   ├── admin/
│       │   │   └── AdminMainFrame.java    # Admin dashboard
│       │   └── tutor/
│       │       └── TutorMainFrame.java    # Tutor dashboard
│       ├── model/
│       │   ├── User.java                  # Base class User
│       │   ├── Admin.java                 # Admin model
│       │   ├── Tutor.java                 # Tutor model
│       │   ├── Siswa.java                 # Siswa model
│       │   └── Pembayaran.java            # Pembayaran model
│       └── database/
│           ├── DatabaseConnection.java    # JDBC connection & queries
│           └── DataStore.java             # Data access layer
├── database/
│   └── db_coursify.sql                    # Database schema
├── build/                                 # Compiled classes (auto-generated)
├── lib/                                   # External libraries
└── README.md                              # Dokumentasi ini
```

---

## 🗂️ Database Schema

### Tabel Utama

**users** - Informasi login
```
id_user | username | password | role
```

**admin** - Data admin
```
id_user | username | password | department
```

**tutor** - Data tutor
```
id_user | username | password | specialization | phone_number
```

**siswa** - Data siswa
```
id_siswa | nama | alamat | telepon | email | tanggal_lahir | kelas
```

**kelas** - Kelas/Paket
```
id_kelas | nama_kelas | tingkat | jurusan | wali_kelas | tahun_ajaran
```

**nilai** - Nilai siswa
```
id_nilai | id_siswa | id_tutor | id_kelas | mata_pelajaran | nilai | keterangan | semester | tahun_ajaran
```

**pembayaran** - Transaksi pembayaran
```
id_pembayaran | id_siswa | jumlah | tanggal_bayar | metode_pembayaran | status
```

**jadwal_mengajar** - Jadwal tutor
```
id_jadwal | id_tutor | hari | jam_mulai | jam_selesai | mata_pelajaran | kelas | ruangan
```

---

## 🔧 Troubleshooting

### Error: "Database connection FAILED"
- Pastikan MySQL sudah berjalan
- Cek username & password di `DatabaseConnection.java`
- Pastikan database `db_coursify` sudah dibuat

### Error: "Class not found: com.mysql.jdbc.Driver"
- Pastikan MySQL Connector JAR sudah di classpath
- Di NetBeans: klik kanan project → Properties → Libraries

### Aplikasi crash saat login
- Cek console error message
- Pastikan database sudah import schema lengkap

---

## 🎓 Konsep OOP yang Diterapkan

### 1. **Inheritance (Pewarisan)**
```java
public class Admin extends User { ... }
public class Tutor extends User { ... }
```
- `Admin` dan `Tutor` mewarisi dari class `User`
- Inherit method `login()`, `logout()`, dan atribut umum

### 2. **Polymorphism (Polimorfisme)**
```java
User user = DataStore.getUser(username, password);
// bisa return Admin atau Tutor

if (user instanceof Admin) {
    AdminMainFrame frame = new AdminMainFrame((Admin) user);
} else if (user instanceof Tutor) {
    TutorMainFrame frame = new TutorMainFrame((Tutor) user);
}
```
- Satu object `User` bisa menjadi `Admin` atau `Tutor`
- Behavior berbeda sesuai tipe object

### 3. **Encapsulation (Pembungkusan)**
```java
private String id_siswa;  // Private - tidak bisa diakses langsung

public String getId_siswa() { return id_siswa; }  // Getter
public void setId_siswa(String id) { this.id_siswa = id; }  // Setter
```
- Data disembunyikan (private)
- Akses melalui getter/setter

### 4. **Abstraction (Abstraksi)**
```java
public abstract class User {
    public abstract void displayRole();  // Method abstrak
}
```
- Method abstrak yang harus diimplementasi subclass
- Mendefinisikan kontrak (interface) untuk semua user

---

## 📊 Class Diagram

```
              ┌─────────────────┐
              │     User        │
              │  (Abstract)     │
              ├─────────────────┤
              │ - id_user       │
              │ - username      │
              │ - password      │
              │ - role          │
              ├─────────────────┤
              │ + login()       │
              │ + logout()      │
              │ + displayRole() │
              └────────┬────────┘
                   △   △
           ┌───────┘   └───────┐
           │                   │
      ┌────┴────┐        ┌─────┴────┐
      │  Admin  │        │  Tutor   │
      ├─────────┤        ├──────────┤
      │-dept    │        │-spec     │
      ├─────────┤        ├──────────┤
      │+getDept │        │+getSpec()│
      └─────────┘        └──────────┘

┌──────────┐  ┌─────────────┐  ┌──────────┐
│  Siswa   │  │ Pembayaran  │  │ Tutor    │
├──────────┤  ├─────────────┤  ├──────────┤
│-id_siswa │  │-id_pembayan │  │-id_user  │
│-nama     │  │-id_siswa    │  │-username │
│-email    │  │-jumlah      │  │-password │
│-telepon  │  │-status      │  │-spec     │
└──────────┘  └─────────────┘  └──────────┘
```

---

## 📚 Konsep yang Dipelajari

- ✅ Class dan Object
- ✅ Encapsulation (Getter/Setter)
- ✅ Inheritance (extends, super)
- ✅ Polymorphism (instanceof, casting)
- ✅ Abstraction (abstract class, interface)
- ✅ Collections (List, ArrayList)
- ✅ JDBC & Database Connection
- ✅ GUI dengan Swing
- ✅ Exception Handling
- ✅ Design Pattern (MVC)

---

| No | Nama | NRP | Role | Bagian |
|----|------|-----|------|--------|
| 1  | [Alsahera Ramadhan Nesa] | [3124521023] | Backend Developer | Database, Authentication, DataStore |
| 2  | [Angelina Safara] | [3124521004] | Admin UI Developer | AdminMainFrame, Admin Features |
| 3  | [Nabillatun Nafista] | [3124521027] | Tutor UI Developer | TutorMainFrame, Tutor Features |

---

## 👨‍🏫 Dosen Pembimbing

| Nama Dosen | Matakuliah |
|-----------|-----------|
| [Yunia Ikawati S.ST, M.Tr.Kom.] | Praktik Pemrograman Berorientasi Objek |

---

## 📅 Informasi UAS

- **Semester**: 3
- **Tahun Akademik**: [2025]
- **Tanggal Submission**: [Minggu, 23 November 2025 - 20:41]
- **Deadline**: [Kamis, 27 November 2025 - 20:00]

---

## 📝 Development Workflow

### Setup Lokal (Pertama Kali)
```bash
git clone https://github.com/Alsahera/coursify-bimbel.git
cd coursify-bimbel
```

### Development (Setiap Hari)
```bash
# Update dari remote
git pull origin main

# Buat branch baru
git checkout -b feature/nama-fitur

# Edit file & commit
git add .
git commit -m "Deskripsi perubahan"

# Push ke GitHub
git push origin feature/nama-fitur

# Buat Pull Request di GitHub
```

---

## 📅 Paket Belajar

Aplikasi mendukung 3 paket utama:

1. **Speaking** - Pronunciation, Vocabulary, Conversations
2. **Grammar** - Tenses, Sentence Structure, Parts of Speech
3. **TOEFL** - Reading, Listening, Speaking, Writing Strategies

Setiap paket memiliki tutor khusus dan jadwal tersendiri.

---

## 📧 Support & Contact

Jika ada pertanyaan atau bug, bisa:
- Buka issue di GitHub
- Diskusi dengan team member
- Check documentation di folder `docs/`

---

## 📄 License

Private Project - Coursify Bimbel Management System

---

## 🎓 Notes

- Semester diisi dengan format: "Batch 1", "Batch 2", dll
- Tahun ajaran: "2025/2026"
- Nilai berkisar 0-100
- Status pembayaran: "Lunas" atau "Belum Lunas"

---

**Last Updated**: November 2025  
**Version**: 1.0.0
