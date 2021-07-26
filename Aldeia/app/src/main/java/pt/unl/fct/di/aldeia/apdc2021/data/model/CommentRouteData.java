package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class CommentRouteData {
    private String email;
    private String token;
    private String route_id;
    private String comment;

    public CommentRouteData(String email, String token, String route_id, String comment) {
        this.email = email;
        this.token = token;
        this.route_id = route_id;
        this.comment = comment;
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

    public String getComment() {
        return comment;
    }
}