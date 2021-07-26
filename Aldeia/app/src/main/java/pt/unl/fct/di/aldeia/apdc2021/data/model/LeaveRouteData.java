package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class LeaveRouteData {
    private String token;
    private String email;
    private String route_id;
    private String participant;

    public LeaveRouteData(String email, String token, String route_id, String participant) {
        this.token = token;
        this.email = email;
        this.route_id = route_id;
        this.participant = participant;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getRoute_id() {
        return route_id;
    }

    public String getParticipant() {
        return participant;
    }
}
