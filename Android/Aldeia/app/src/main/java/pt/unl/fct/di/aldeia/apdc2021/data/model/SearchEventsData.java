package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class SearchEventsData {

    private String email;
    private String token;
    private double[] location;


    public SearchEventsData(String email, String token, double[] location) {
        this.email = email;
        this.token = token;
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public double[] getLocation() {
        return location;
    }


}
