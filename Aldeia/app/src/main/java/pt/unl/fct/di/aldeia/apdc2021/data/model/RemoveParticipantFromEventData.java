package pt.unl.fct.di.aldeia.apdc2021.data.model;

public class RemoveParticipantFromEventData {
    private String token;
    private String email;
    private String participant ;
    private String event_id;

    public RemoveParticipantFromEventData(String email, String token, String participant, String event_id) {
        this.token = token;
        this.email = email;
        this.participant = participant;
        this.event_id = event_id;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public String getParticipant() {
        return participant;
    }

    public String getEvent_id() {
        return event_id;
    }
}
