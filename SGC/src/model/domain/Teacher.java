package model.domain;

public class Teacher {
    private int teacherID;
    private String name;
    private String lastName;
    private String nation;
    private boolean active;
    private String city;
    private String cap;  // Codice di Avviamento Postale
    private String street;
    private int streetNumber;

    public Teacher(int teacherID, String name, String lastName, String nation,
                   boolean active, String city, String cap, String street, int streetNumber) {
        this.teacherID = teacherID;
        this.name = name;
        this.lastName = lastName;
        this.nation = nation;
        this.active = active;
        this.city = city;
        this.cap = cap;
        this.street = street;
        this.streetNumber = streetNumber;
    }

    // Getters
    public int getTeacherID() { return teacherID; }
    public String getName() { return name; }
    public String getLastName() { return lastName; }
    public String getNation() { return nation; }
    public boolean isActive() { return active; }
    public String getCity() { return city; }
    public String getCap() { return cap; }
    public String getStreet() { return street; }
    public int getStreetNumber() { return streetNumber; }

    @Override
    public String toString() {
        String status = active ? "Attivo" : "Non attivo";
        return lastName + " " + name + " (" + nation + ") - " + status;
    }
}
