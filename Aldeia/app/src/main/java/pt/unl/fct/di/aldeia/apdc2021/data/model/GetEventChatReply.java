package pt.unl.fct.di.aldeia.apdc2021.data.model;

import java.util.List;

public class GetEventChatReply {
    private List<GetChatCommentUnit> comments;
    private String results;
    private int cursor;

    public GetEventChatReply(List<GetChatCommentUnit> comments, String results, int cursor) {
        this.comments = comments;
        this.results = results;
        this.cursor = cursor;
    }

    public List<GetChatCommentUnit> getComments() {
        return comments;
    }

    public String getResults() {
        return results;
    }

    public int getCursor() {
        return cursor;
    }


}
