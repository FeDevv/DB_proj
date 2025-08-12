package model.domain;

public class Administrator {
    private int administratorID;
    private String email;
    private String name;
    private String lastName;
    private String role;
    private boolean active;

    public Administrator(int administratorID, String email, String name,
                         String lastName, String role, boolean active) {
        this.administratorID = administratorID;
        this.email = email;
        this.name = name;
        this.lastName = lastName;
        this.role = role;
        this.active = active;
    }

    // Getters
    public int getAdministratorID() { return administratorID; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getLastName() { return lastName; }
    public String getRole() { return role; }
    public boolean isActive() { return active; }

    @Override
    public String toString() {
        String status = active ? "Attivo" : "Non attivo";
        return lastName + " " + name + " - " + role + " (" + email + ") - " + status;
    }
}
