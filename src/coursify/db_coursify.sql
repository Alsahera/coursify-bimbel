-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 23, 2025 at 09:41 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_coursify`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_input_nilai_kelas` (IN `p_id_siswa` VARCHAR(10), IN `p_id_tutor` VARCHAR(10), IN `p_id_kelas` VARCHAR(10), IN `p_mata_pelajaran` VARCHAR(100), IN `p_nilai` INT, IN `p_keterangan` VARCHAR(255), IN `p_semester` VARCHAR(50), IN `p_tahun_ajaran` VARCHAR(20))   BEGIN
    INSERT INTO nilai (
        id_siswa,
        id_tutor,
        id_kelas,
        mata_pelajaran,
        nilai,
        keterangan,
        semester,
        tahun_ajaran
    ) VALUES (
        p_id_siswa,
        p_id_tutor,
        p_id_kelas,
        p_mata_pelajaran,
        p_nilai,
        p_keterangan,
        p_semester,
        p_tahun_ajaran
    );
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `id_user` varchar(10) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `department` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`id_user`, `username`, `password`, `department`) VALUES
('A01', 'admin', 'admin123', 'Academic');

-- --------------------------------------------------------

--
-- Table structure for table `jadwal_mengajar`
--

CREATE TABLE `jadwal_mengajar` (
  `id_jadwal` int(11) NOT NULL,
  `id_tutor` varchar(10) NOT NULL,
  `hari` varchar(20) NOT NULL,
  `jam_mulai` time NOT NULL,
  `jam_selesai` time NOT NULL,
  `mata_pelajaran` varchar(100) NOT NULL,
  `kelas` varchar(50) NOT NULL,
  `ruangan` varchar(50) NOT NULL,
  `id_kelas` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `jadwal_mengajar`
--

INSERT INTO `jadwal_mengajar` (`id_jadwal`, `id_tutor`, `hari`, `jam_mulai`, `jam_selesai`, `mata_pelajaran`, `kelas`, `ruangan`, `id_kelas`) VALUES
(1, 'T002', 'Senin', '10:00:00', '11:30:00', 'Pronunciation & Intonation', 'Speaking', 'Ruang 101', 'KSP01'),
(2, 'T001', 'Senin', '18:30:00', '20:30:00', 'TOEFL Reading Strategies', 'TOEFL', 'Ruang 301', 'KTF01'),
(3, 'T003', 'Selasa', '13:00:00', '14:30:00', 'Tenses (Basic & Advanced)', 'Grammar', 'Ruang 201', 'KGR01'),
(4, 'T002', 'Rabu', '10:00:00', '11:30:00', 'Vocabulary for Fluency', 'Speaking', 'Ruang 101', 'KSP01'),
(5, 'T001', 'Rabu', '18:30:00', '20:30:00', 'TOEFL Listening Strategies', 'TOEFL', 'Ruang 301', 'KTF01'),
(6, 'T003', 'Kamis', '13:00:00', '14:30:00', 'Sentence Structure', 'Grammar', 'Ruang 201', 'KGR01'),
(7, 'T002', 'Jumat', '10:00:00', '11:30:00', 'Daily Conversations & Role-Play', 'Speaking', 'Ruang 101', 'KSP01'),
(8, 'T001', 'Jumat', '18:30:00', '20:30:00', 'TOEFL Speaking Tasks Practice', 'TOEFL', 'Ruang 301', 'KTF01'),
(9, 'T003', 'Sabtu', '10:00:00', '11:30:00', 'Parts of Speech & Punctuation', 'Grammar', 'Ruang 201', 'KGR01'),
(10, 'T001', 'Minggu', '09:00:00', '11:00:00', 'TOEFL Writing Tasks Practice', 'TOEFL', 'Ruang 301', 'KTF01');

-- --------------------------------------------------------

--
-- Table structure for table `kelas`
--

CREATE TABLE `kelas` (
  `id_kelas` varchar(10) NOT NULL,
  `nama_kelas` varchar(100) NOT NULL,
  `tingkat` varchar(50) DEFAULT NULL,
  `jurusan` varchar(50) DEFAULT NULL,
  `wali_kelas` varchar(10) DEFAULT NULL,
  `tahun_ajaran` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `kelas`
--

INSERT INTO `kelas` (`id_kelas`, `nama_kelas`, `tingkat`, `jurusan`, `wali_kelas`, `tahun_ajaran`) VALUES
('KGR01', 'Grammar', NULL, NULL, 'T003', '2025/2026'),
('KSP01', 'Speaking', NULL, NULL, 'T002', '2025/2026'),
('KTF01', 'TOEFL', NULL, NULL, 'T001', '2025/2026');

-- --------------------------------------------------------

--
-- Table structure for table `nilai`
--

CREATE TABLE `nilai` (
  `id_nilai` int(11) NOT NULL,
  `id_siswa` varchar(10) NOT NULL,
  `id_tutor` varchar(10) NOT NULL,
  `id_kelas` varchar(10) NOT NULL,
  `mata_pelajaran` varchar(100) NOT NULL,
  `nilai` int(11) NOT NULL,
  `keterangan` varchar(255) DEFAULT NULL,
  `semester` varchar(50) DEFAULT NULL,
  `tahun_ajaran` varchar(20) DEFAULT NULL,
  `tanggal_input` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `nilai`
--

INSERT INTO `nilai` (`id_nilai`, `id_siswa`, `id_tutor`, `id_kelas`, `mata_pelajaran`, `nilai`, `keterangan`, `semester`, `tahun_ajaran`, `tanggal_input`) VALUES
(1, 'S003', 'T003', 'KGR01', 'Tenses (Basic & Advanced)', 90, 'Mid-test | Tenses (Basic & Advanced) | Sangat Baik', '2025/2026', '2025/2026', '2025-11-19 18:30:48'),
(2, 'S003', 'T003', 'KGR01', 'Sentence Structure', 80, 'Mid-test | Sentence Structure | Baik', 'November', '2025/2026', '2025-11-19 18:42:08'),
(3, 'S012', 'T003', 'KGR01', 'Sentence Structure', 80, 'Mid-test | Sentence Structure | Baik', '2025/2026', '2025/2026', '2025-11-20 09:47:38'),
(4, 'S012', 'T003', 'KGR01', 'Sentence Structure', 80, 'Final-test | Sentence Structure | Baik', 'November', '2025/2026', '2025-11-20 12:53:16'),
(5, 'S003', 'T003', 'KGR01', 'Sentence Structure', 70, 'Final-test | Sentence Structure | Baik | mantap', '2025/2026', '2025/2026', '2025-11-20 12:54:42'),
(6, 'S003', 'T003', 'KGR01', 'Tenses (Basic & Advanced)', 100, 'Final-test | Tenses (Basic & Advanced) | Sangat Baik', '2025/2026', '2025/2026', '2025-11-20 13:26:57');

-- --------------------------------------------------------

--
-- Table structure for table `pembayaran`
--

CREATE TABLE `pembayaran` (
  `id_pembayaran` varchar(10) NOT NULL,
  `id_siswa` varchar(10) NOT NULL,
  `jumlah` double NOT NULL,
  `tanggal_bayar` date NOT NULL,
  `metode_pembayaran` varchar(50) NOT NULL,
  `status` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `pembayaran`
--

INSERT INTO `pembayaran` (`id_pembayaran`, `id_siswa`, `jumlah`, `tanggal_bayar`, `metode_pembayaran`, `status`) VALUES
('P001', 'S001', 1000000, '2025-10-15', 'Transfer Bank', 'Lunas'),
('P002', 'S002', 750000, '2025-10-16', 'Cash', 'Belum Lunas'),
('P004', 'S005', 1000000, '2025-10-17', 'Transfer Bank', 'Lunas'),
('P005', 'S004', 800000, '2025-11-19', 'Transfer Bank', 'Belum Lunas'),
('P006', 'S008', 700000, '2025-11-19', 'Transfer Bank', 'Lunas');

-- --------------------------------------------------------

--
-- Table structure for table `siswa`
--

CREATE TABLE `siswa` (
  `id_siswa` varchar(10) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `alamat` text DEFAULT NULL,
  `telepon` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `tanggal_lahir` date DEFAULT NULL,
  `kelas` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `siswa`
--

INSERT INTO `siswa` (`id_siswa`, `nama`, `alamat`, `telepon`, `email`, `tanggal_lahir`, `kelas`) VALUES
('S001', 'Andi', 'Jl. Mawar 1', '0811111111', 'andi@gmail.com', '2008-01-01', 'Speaking'),
('S002', 'Budi', 'Jl. Mawar 2', '0811111112', 'budi@gmail.com', '2008-02-02', 'Speaking'),
('S003', 'Citra', 'Jl. Melati 1', '0811111113', 'citra@gmail.com', '2008-03-03', 'Grammar'),
('S004', 'Dewi', 'Jl. Melati 2', '0811111114', 'dewi@gmail.com', '2008-04-04', 'Grammar'),
('S005', 'Eka', 'Jl. Kenanga 1', '0811111115', 'eka@gmail.com', '2008-05-05', 'TOEFL'),
('S006', 'Farhan', 'Jl. Kenanga 2', '0811111116', 'farhan@gmail.com', '2008-06-06', 'TOEFL'),
('S007', 'Angel', 'JL. Lamongan', '0811111117', 'angel@gmail.com', '2008-07-20', 'TOEFL'),
('S008', 'Nabila', 'Jl. Lamongan', '0811111118', 'nabila@gmail.com', '2008-08-08', 'Speaking'),
('S009', 'Brian', 'Jl. Bougenville', '0811111119', 'brian@gmail.com', '2008-09-09', 'Speaking'),
('S010', 'Safa', 'Jl. Anggrek', '0811111110', 'safa@gmail.com', '2008-10-10', 'Speaking'),
('S011', 'Fara', 'Jl. Tulip', '0811111120', 'fara@gmail.com', '2008-11-11', 'Grammar'),
('S012', 'Regina', 'Jl. Lavender', '0811111121', 'regina@gmail.com', '2008-12-12', 'Grammar'),
('S013', 'Rio', 'Jl. Kamboja', '0811111122', 'rio@gmail.com', '2008-12-01', 'Grammar'),
('S014', 'Vero', 'Jl. Lily', '0811111123', 'vero@gmail.com', '2008-05-07', 'TOEFL'),
('S015', 'Jeffri', 'Jl. Alamanda', '0811111124', 'jeffri@gmail.com', '2008-03-05', 'TOEFL');

-- --------------------------------------------------------

--
-- Table structure for table `siswa_kelas`
--

CREATE TABLE `siswa_kelas` (
  `id` int(11) NOT NULL,
  `id_siswa` varchar(10) NOT NULL,
  `id_kelas` varchar(10) NOT NULL,
  `tanggal_masuk` date DEFAULT NULL,
  `status` varchar(20) DEFAULT 'Aktif'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `siswa_kelas`
--

INSERT INTO `siswa_kelas` (`id`, `id_siswa`, `id_kelas`, `tanggal_masuk`, `status`) VALUES
(1, 'S001', 'KSP01', '2025-01-10', 'Aktif'),
(2, 'S002', 'KSP01', '2025-01-10', 'Aktif'),
(3, 'S003', 'KGR01', '2025-01-10', 'Aktif'),
(4, 'S004', 'KGR01', '2025-01-10', 'Aktif'),
(5, 'S005', 'KTF01', '2025-01-10', 'Aktif'),
(6, 'S006', 'KTF01', '2025-01-10', 'Aktif'),
(8, 'S007', 'KTF01', '2025-11-19', 'Aktif'),
(9, 'S008', 'KSP01', '2025-11-19', 'Aktif'),
(10, 'S009', 'KSP01', '2025-11-19', 'Aktif'),
(11, 'S010', 'KSP01', '2025-11-19', 'Aktif'),
(12, 'S011', 'KGR01', '2025-11-19', 'Aktif'),
(13, 'S012', 'KGR01', '2025-11-19', 'Aktif'),
(14, 'S013', 'KGR01', '2025-11-19', 'Aktif'),
(15, 'S014', 'KTF01', '2025-11-19', 'Aktif'),
(16, 'S015', 'KTF01', '2025-11-19', 'Aktif');

-- --------------------------------------------------------

--
-- Table structure for table `tutor`
--

CREATE TABLE `tutor` (
  `id_user` varchar(10) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `specialization` varchar(100) DEFAULT NULL,
  `phone_number` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tutor`
--

INSERT INTO `tutor` (`id_user`, `username`, `password`, `specialization`, `phone_number`) VALUES
('T001', 'Alsa', '123', 'TOEFL', '081234000001'),
('T002', 'Fista', '123', 'Speaking', '081234000002'),
('T003', 'Angel', '123', 'Grammar', '081234000003');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id_user` varchar(10) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `role` enum('admin','tutor') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id_user`, `username`, `password`, `role`) VALUES
('A01', 'admin', 'admin123', 'admin'),
('T001', 'Alsa', '123', 'tutor'),
('T002', 'Fista', '123', 'tutor'),
('T003', 'Angel', '123', 'tutor');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`id_user`);

--
-- Indexes for table `jadwal_mengajar`
--
ALTER TABLE `jadwal_mengajar`
  ADD PRIMARY KEY (`id_jadwal`),
  ADD KEY `fk_jadwal_tutor` (`id_tutor`),
  ADD KEY `fk_jadwal_kelas` (`id_kelas`);

--
-- Indexes for table `kelas`
--
ALTER TABLE `kelas`
  ADD PRIMARY KEY (`id_kelas`),
  ADD KEY `fk_kelas_tutor` (`wali_kelas`);

--
-- Indexes for table `nilai`
--
ALTER TABLE `nilai`
  ADD PRIMARY KEY (`id_nilai`),
  ADD KEY `fk_nilai_siswa` (`id_siswa`),
  ADD KEY `fk_nilai_tutor` (`id_tutor`),
  ADD KEY `fk_nilai_kelas` (`id_kelas`);

--
-- Indexes for table `pembayaran`
--
ALTER TABLE `pembayaran`
  ADD PRIMARY KEY (`id_pembayaran`),
  ADD KEY `fk_pembayaran_siswa` (`id_siswa`);

--
-- Indexes for table `siswa`
--
ALTER TABLE `siswa`
  ADD PRIMARY KEY (`id_siswa`);

--
-- Indexes for table `siswa_kelas`
--
ALTER TABLE `siswa_kelas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_siswa_kelas_siswa` (`id_siswa`),
  ADD KEY `fk_siswa_kelas_kelas` (`id_kelas`);

--
-- Indexes for table `tutor`
--
ALTER TABLE `tutor`
  ADD PRIMARY KEY (`id_user`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id_user`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `jadwal_mengajar`
--
ALTER TABLE `jadwal_mengajar`
  MODIFY `id_jadwal` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `nilai`
--
ALTER TABLE `nilai`
  MODIFY `id_nilai` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `siswa_kelas`
--
ALTER TABLE `siswa_kelas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `admin`
--
ALTER TABLE `admin`
  ADD CONSTRAINT `fk_admin_user` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `jadwal_mengajar`
--
ALTER TABLE `jadwal_mengajar`
  ADD CONSTRAINT `fk_jadwal_kelas` FOREIGN KEY (`id_kelas`) REFERENCES `kelas` (`id_kelas`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_jadwal_tutor` FOREIGN KEY (`id_tutor`) REFERENCES `tutor` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `kelas`
--
ALTER TABLE `kelas`
  ADD CONSTRAINT `fk_kelas_tutor` FOREIGN KEY (`wali_kelas`) REFERENCES `tutor` (`id_user`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `nilai`
--
ALTER TABLE `nilai`
  ADD CONSTRAINT `fk_nilai_kelas` FOREIGN KEY (`id_kelas`) REFERENCES `kelas` (`id_kelas`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_nilai_siswa` FOREIGN KEY (`id_siswa`) REFERENCES `siswa` (`id_siswa`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_nilai_tutor` FOREIGN KEY (`id_tutor`) REFERENCES `tutor` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `pembayaran`
--
ALTER TABLE `pembayaran`
  ADD CONSTRAINT `fk_pembayaran_siswa` FOREIGN KEY (`id_siswa`) REFERENCES `siswa` (`id_siswa`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `siswa_kelas`
--
ALTER TABLE `siswa_kelas`
  ADD CONSTRAINT `fk_siswa_kelas_kelas` FOREIGN KEY (`id_kelas`) REFERENCES `kelas` (`id_kelas`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_siswa_kelas_siswa` FOREIGN KEY (`id_siswa`) REFERENCES `siswa` (`id_siswa`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `tutor`
--
ALTER TABLE `tutor`
  ADD CONSTRAINT `fk_tutor_user` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
