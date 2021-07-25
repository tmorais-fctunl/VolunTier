package pt.unl.fct.di.aldeia.apdc2021.data.model;

import java.util.List;

public class GetParticipantsReply {
    private List<ParticipantInfoUnit> participants;
    private String results;
    private int cursor;

    public GetParticipantsReply(List<ParticipantInfoUnit> participants, String results, int cursor) {
        this.participants = participants;
        this.results = results;
        this.cursor = cursor;
    }

    public List<ParticipantInfoUnit> getParticipants() {
        return participants;
    }

    public String getResults() {
        return results;
    }

    public int getCursor() {
        return cursor;
    }
}
