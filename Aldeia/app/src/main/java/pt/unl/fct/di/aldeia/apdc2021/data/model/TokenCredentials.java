package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class TokenCredentials {
    String token;
    String email;

    public TokenCredentials(String email, String token) {
        this.email=email;
        this.token=token;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }
}

