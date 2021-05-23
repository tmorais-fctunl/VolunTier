package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class RecoverPwCredentials {
    String email;
    String user_id;

    public RecoverPwCredentials(String username, String email) {
        this.user_id=username;
        this.email=email;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return user_id;
    }
}