package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class GetChatCommentUnit {

    private String email;
    private String username;
    private String comment;
    private String timestamp;
    private int comment_id;
    private int likes;
    private boolean like_status;

    public GetChatCommentUnit(String email, String username, String comment, String timestamp, int comment_id, int likes, boolean like_status) {
        this.email = email;
        this.username = username;
        this.comment = comment;
        this.timestamp = timestamp;
        this.comment_id = comment_id;
        this.likes = likes;
        this.like_status = like_status;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getComment() {
        return comment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getComment_id() {
        return comment_id;
    }

    public int getLikes() {
        return likes;
    }

    public boolean isLike_status() {
        return like_status;
    }

    public void setLike_status(boolean like_status) {
        this.like_status = like_status;
    }
}
