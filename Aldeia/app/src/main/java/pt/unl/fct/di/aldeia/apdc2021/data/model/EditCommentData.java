package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class EditCommentData {
    private String token;
    private String email;
    private String event_id;
    private int comment_id;
    private String comment;

    public EditCommentData(String email, String token,  String event_id, int comment_id, String comment) {
        this.token = token;
        this.email = email;
        this.event_id = event_id;
        this.comment_id = comment_id;
        this.comment = comment;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getEvent_id() {
        return event_id;
    }

    public int getComment_id() {
        return comment_id;
    }

    public String getComment() {
        return comment;
    }
}
