package coursify.model;

public class Tutor extends User {
    private String specialization;
    private String phoneNumber;
    
    // Constructor
    public Tutor(String id_user, String username, String password, String specialization) {
        super(id_user, username, password, "Tutor");
        this.specialization = specialization;
        this.phoneNumber = "";
    }
    
    // Getter dan Setter untuk specialization
    public String getSpecialization() {
        return specialization;
    }
    
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    
    // Getter dan Setter untuk phoneNumber
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    // Implementasi metode abstrak dari User
    @Override
    public void displayRole() {
        System.out.println("Role: Tutor - Specialization: " + specialization);
    }
    
    // Metode khusus Tutor
    public void inputGrades() {
        System.out.println("Tutor " + getUsername() + " menginput nilai siswa");
    }
    
    public void viewSchedule() {
        System.out.println("Tutor " + getUsername() + " melihat jadwal mengajar");
    }
}