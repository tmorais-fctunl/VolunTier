package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class UserCredentials {
    String password;
    String email;

    public UserCredentials(String email, String password) {
        this.email=email;
        this.password=password;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
