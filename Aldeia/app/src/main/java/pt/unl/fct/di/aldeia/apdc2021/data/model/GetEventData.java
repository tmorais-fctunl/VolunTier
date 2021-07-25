package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class GetEventData {

    String token;
    String email;
    String event_id;

    public GetEventData(String email, String token, String event_id) {
        this.token = token;
        this.email = email;
        this.event_id = event_id;
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

}
