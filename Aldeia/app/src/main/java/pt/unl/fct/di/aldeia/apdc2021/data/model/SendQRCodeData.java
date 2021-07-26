package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class SendQRCodeData {
    private String email;
    private String token;
    private String code;

    public SendQRCodeData(String email, String token, String code) {
        this.email = email;
        this.token = token;
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public String getCode() {
        return code;
    }
}
