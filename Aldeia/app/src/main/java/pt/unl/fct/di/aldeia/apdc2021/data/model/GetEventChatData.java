package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class GetEventChatData {

    private String token;
    private String email;
    private String event_id;
    private boolean latest_first;
    private int cursor;

    public GetEventChatData(String token, String email, String event_id, boolean latest_first, int cursor) {
        this.token = token;
        this.email = email;
        this.event_id = event_id;
        this.latest_first = latest_first;
        this.cursor = cursor;
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

    public boolean isLatest_first() {
        return latest_first;
    }

    public int getCursor() {
        return cursor;
    }
}
