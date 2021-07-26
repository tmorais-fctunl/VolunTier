package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class UserCredentials {
    String password;
    String user;

    public UserCredentials(String user, String password) {
        this.user=user;
        this.password=password;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return user;
    }
}
