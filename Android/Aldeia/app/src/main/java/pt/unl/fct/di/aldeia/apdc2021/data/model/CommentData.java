package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class CommentData {
    private String email;
    private String token;
    private String event_id;
    private String comment;

    public CommentData(String email, String token, String event_id, String comment) {
        this.email = email;
        this.token = token;
        this.event_id = event_id;
        this.comment = comment;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public String getEvent_id() {
        return event_id;
    }

    public String getComment() {
        return comment;
    }
}
