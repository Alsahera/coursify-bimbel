package coursify.model;

public abstract class User {
    // Encapsulation: Atribut private
    private String id_user;
    private String username;
    private String password;
    private String role;
    
    // Constructor
    public User(String id_user, String username, String password, String role) {
        this.id_user = id_user;
        this.username = username;
        this.password = password;
        this.role = role;
    }
    
    // Getter dan Setter untuk Encapsulation
    public String getId_user() {
        return id_user;
    }
    
    public void setId_user(String id_user) {
        this.id_user = id_user;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    // Metode untuk login
    public boolean login(String inputUsername, String inputPassword) {
        return this.username.equals(inputUsername) && this.password.equals(inputPassword);
    }
    
    // Metode untuk logout
    public void logout() {
        System.out.println(username + " telah logout dari sistem.");
    }
    
    // Metode abstrak yang harus diimplementasikan oleh subclass
    public abstract void displayRole();
}