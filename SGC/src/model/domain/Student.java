package model.domain;

import java.time.LocalDate;

public class Student {
    private int studentID;
    private String name;
    private String lastName;
    private String cf;  // Codice Fiscale
    private String birthPlace;
    private String telephoneNumber;  // Cambiato a String per gestire prefissi
    private LocalDate birthDate;  // Usiamo LocalDate per gestire la data
    private String city;
    private String cap;  // Codice di Avviamento Postale
    private String street;
    private int streetNumber;


    public Student(String name, String lastName, String cf,
                   String birthPlace, String telephoneNumber, LocalDate birthDate,
                   String city, String cap, String street, int streetNumber) {
        this.studentID = Integer.parseInt(null);
        this.name = name;
        this.lastName = lastName;
        this.cf = cf;
        this.birthPlace = birthPlace;
        this.telephoneNumber = telephoneNumber;
        this.birthDate = birthDate;
        this.city = city;
        this.cap = cap;
        this.street = street;
        this.streetNumber = streetNumber;
    }

    // Getters
    public int getStudentID() { return studentID; }
    public String getName() { return name; }
    public String getLastName() { return lastName; }
    public String getCf() { return cf; }
    public String getBirthPlace() { return birthPlace; }
    public String getTelephoneNumber() { return telephoneNumber; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getCity() { return city; }
    public String getCap() { return cap; }
    public String getStreet() { return street; }
    public int getStreetNumber() { return streetNumber; }
    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    @Override
    public String toString() {
        return lastName + " " + name + " (" + cf + ") - " + studentID;
    }
}
