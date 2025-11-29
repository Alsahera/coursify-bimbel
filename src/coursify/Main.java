// Main.java - Entry point aplikasi Coursify
package coursify;

import coursify.view.Login;
import coursify.database.DatabaseConnection;
import javax.swing.*;
import java.util.logging.Level;

public class Main {
       public static void main(String[] args) {
        // Test koneksi database terlebih dahulu
        System.out.println("=================================");
        System.out.println("Testing Database Connection...");
        System.out.println("=================================");
        
        if (DatabaseConnection.testConnection()) {
            System.out.println("✓ Database connected successfully!");
            System.out.println("=================================\n");
        } else {
            System.err.println("✗ Database connection FAILED!");
            System.err.println("Periksa konfigurasi database Anda");
            System.err.println("=================================\n");
            
            JOptionPane.showMessageDialog(null, 
                "Koneksi database gagal!\n" +
                "Pastikan MySQL sudah running dan database sudah dibuat.",
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            return; // Stop aplikasi jika database tidak connect
        }
        
        // Set Look and Feel
        try {// Gunakan System Look and Feel untuk konsistensi
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        
        // ATAU gunakan Nimbus untuk tampilan lebih modern
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                javax.swing.UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }
        
    } catch (Exception e) {
        e.printStackTrace();
    }
        
        // Jalankan aplikasi
        SwingUtilities.invokeLater(() -> {
            Login loginFrame = new Login();
            loginFrame.setVisible(true);
        });
    }
}