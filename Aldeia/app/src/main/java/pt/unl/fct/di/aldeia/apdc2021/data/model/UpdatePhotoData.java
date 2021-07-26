package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class UpdatePhotoData {
    private String email;
    private String token;
    private String data;

    public UpdatePhotoData(String email, String token, String data) {
        this.email = email;
        this.token = token;
        this.data = data;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public String getData() {
        return data;
    }

}
