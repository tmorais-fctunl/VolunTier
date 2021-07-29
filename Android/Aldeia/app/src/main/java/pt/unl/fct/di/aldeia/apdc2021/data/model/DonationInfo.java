package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class DonationInfo {

    private String token;
    private String email;
    private String cause_id;
    private float amount;

    public DonationInfo(String token, String email, String cause_id, float amount) {
        this.token = token;
        this.email = email;
        this.cause_id = cause_id;
        this.amount = amount;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getCause_id() {
        return cause_id;
    }

    public float getAmount() {
        return amount;
    }
}