package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class LikeCommentData {
    private String email;
    private String token;
    private String event_id;
    private int comment_id;

    public LikeCommentData(String email, String token, String event_id, int comment_id) {
        this.email = email;
        this.token = token;
        this.event_id = event_id;
        this.comment_id = comment_id;
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

    public int getComment_id() {
        return comment_id;
    }
}
