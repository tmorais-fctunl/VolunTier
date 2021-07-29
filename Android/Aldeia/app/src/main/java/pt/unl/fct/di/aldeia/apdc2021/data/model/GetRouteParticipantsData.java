package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class GetRouteParticipantsData {
    String token;
    String email;
    String route_id;
    int cursor;

    public GetRouteParticipantsData(String email, String token, String route_id,int cursor) {
        this.token = token;
        this.email = email;
        this.route_id = route_id;
        this.cursor=cursor;
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

    public int getCursor() {
        return cursor;
    }
}
