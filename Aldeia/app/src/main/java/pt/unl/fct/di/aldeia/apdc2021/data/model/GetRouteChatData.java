package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class GetRouteChatData {

    private String token;
    private String email;
    private String route_id;
    private boolean latest_first;
    private int cursor;

    public GetRouteChatData(String token, String email, String route_id, boolean latest_first, int cursor) {
        this.token = token;
        this.email = email;
        this.route_id = route_id;
        this.latest_first = latest_first;
        this.cursor = cursor;
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

    public boolean isLatest_first() {
        return latest_first;
    }

    public int getCursor() {
        return cursor;
    }
}