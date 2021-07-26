package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class ProfileTokenCredentials {
    String token;
    String email;
    String target;

    public ProfileTokenCredentials(String email, String token, String target) {
        this.email=email;
        this.token=token;
        this.target = target;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getTarget() {
        return target;
    }
}
