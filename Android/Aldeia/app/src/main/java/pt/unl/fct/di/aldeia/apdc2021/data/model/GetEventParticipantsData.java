package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class GetEventParticipantsData {
    String token;
    String email;
    String event_id;
    int cursor;

    public GetEventParticipantsData(String email, String token, String event_id,int cursor) {
        this.token = token;
        this.email = email;
        this.event_id = event_id;
        this.cursor=cursor;
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

    public int getCursor() {
        return cursor;
    }
}
