package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class ParticipantInfoUnit {
    private String email;
    private String username;
    private String pic;
    private String role;

    public ParticipantInfoUnit(String email, String username, String pic, String role) {
        this.email = email;
        this.username = username;
        this.pic = pic;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPic() {
        return pic;
    }

    public String getRole() {
        return role;
    }
}
