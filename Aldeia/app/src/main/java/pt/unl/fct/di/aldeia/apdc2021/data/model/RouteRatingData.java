package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class RouteRatingData {
    private String email;
    private String token;
    private String route_id;
    private float rating;

    public RouteRatingData(String email, String token, String route_id, float rating) {
        this.email = email;
        this.token = token;
        this.route_id = route_id;
        this.rating=rating;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public String getRoute_id() {
        return route_id;
    }

    public float getRating() {
        return rating;
    }
}
