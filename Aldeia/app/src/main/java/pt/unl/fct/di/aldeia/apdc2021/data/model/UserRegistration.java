package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class UserRegistration {
    String password;
    String username;
    String email;

    public UserRegistration(String username, String email, String password) {
        this.username =username;
        this.email=email;
        this.password=password;

    }

    public String getUsername() {
        return username;
    }

    public String getEmail(){ return email; }

    public String getPassword() {
        return password;
    }




}
