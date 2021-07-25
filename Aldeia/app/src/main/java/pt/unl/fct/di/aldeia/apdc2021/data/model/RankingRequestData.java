package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class RankingRequestData {

    String token;
    String email;
    String cursor;

    public RankingRequestData(String email, String token, String cursor) {
        this.token = token;
        this.email = email;
        this.cursor = cursor;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getCursor() {
        return cursor;
    }

}