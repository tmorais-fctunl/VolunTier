package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class UserCredentials {
    String password;
    String user_id;

    public UserCredentials(String username, String password) {
        this.user_id=username;
        this.password=password;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return user_id;
    }
}
