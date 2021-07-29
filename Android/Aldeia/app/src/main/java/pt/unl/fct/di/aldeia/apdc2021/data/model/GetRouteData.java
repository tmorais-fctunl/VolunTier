package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class GetRouteData {
    private String email;
    private String token;
    private String route_id;

    public GetRouteData(String email, String token, String route_id) {
        this.email = email;
        this.token = token;
        this.route_id = route_id;
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
}
