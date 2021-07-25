package pt.unl.fct.di.aldeia.apdc2021.data.model;

import java.util.List;

public class CreateRouteData {

    private String email ;
    private String token;
    private List<String> event_ids ;
    private String route_name;
    private String description;


    public CreateRouteData(String email, String token, List<String> event_ids, String route_name, String description) {
        this.email = email;
        this.token = token;
        this.event_ids = event_ids;
        this.route_name = route_name;
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public List<String> getEvent_ids() {
        return event_ids;
    }

    public String getRoute_name() {
        return route_name;
    }

    public String getDescription() {
        return description;
    }
}
