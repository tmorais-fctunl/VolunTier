package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class TokenCredentials {
    String token;
    String user_id;

    public TokenCredentials(String username, String token) {
        this.user_id=username;
        this.token=token;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return user_id;
    }
}

