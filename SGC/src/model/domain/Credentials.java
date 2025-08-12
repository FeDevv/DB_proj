package model.domain;

public class Credentials {
    private final String username;
    private final String password;
    private final Role role;
    private final int ID;


    public Credentials(String username, String password, Role role, int ID){
        this.username = username;
        this.ID = ID;
        this.password = password;
        this.role = role;
    }

    public String getUsername(){ return username; }
    public int getID(){ return ID; }
    public String getPassword(){ return password; }
    public Role getRole() { return role;}

}
