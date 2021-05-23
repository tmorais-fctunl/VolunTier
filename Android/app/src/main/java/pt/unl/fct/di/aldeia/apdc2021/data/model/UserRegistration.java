package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class UserRegistration {
    String password;
    String user_id;
    String email;

    public UserRegistration(String username, String email, String password) {
        this.user_id=username;
        this.email=email;
        this.password=password;

    }

    public String getUsername() {
        return user_id;
    }

    public String getEmail(){ return email; }

    public String getPassword() {
        return password;
    }




}
