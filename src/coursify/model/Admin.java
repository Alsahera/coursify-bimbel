package coursify.model;

/**
 * Representasi Admin aplikasi.
 * Admin mewarisi User dan menambahkan field 'department'.
 */
public class Admin extends User {

    private String department;

    // Constructor dipakai di DatabaseConnection.getUser(...)
    public Admin(String id_user, String username, String password, String department) {
        super(id_user, username, password, "Admin");
        this.department = department;
    }

    // =======================
    // Getter & Setter
    // =======================

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    // =======================
    // Override dari User
    // =======================

    @Override
    public void logout() {
        // opsional: bisa kasih log tambahan
        System.out.println("Admin " + getUsername() + " telah logout dari sistem.");
    }

    @Override
    public void displayRole() {
        System.out.println("Role: Admin - Department: " + department);
    }
}
