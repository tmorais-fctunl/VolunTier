package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class UserAuthenticated {

    String email;
    String accessToken;
    String refreshToken;
    long creationDate;
    long expirationDate;
    long refresh_expirationDate;

    public UserAuthenticated (String email, String accessToken, String refreshToken, long creationDate, long expirationDate, long refresh_expirationDate) {
        this.email = email;
        this.accessToken=accessToken;
        this.refreshToken=refreshToken;
        this.creationDate=creationDate;
        this.expirationDate=expirationDate;
        this.refresh_expirationDate = refresh_expirationDate;
    }

    public String getEmail() {
        return email;
    }

    public String getTokenID() {
        return accessToken;
    }

    public String getRefreshToken () {
        return refreshToken;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public long getExpirationDate() {
        return expirationDate;
    }

    public long getRefresh_expirationDate() {
        return refresh_expirationDate;
    }
}
