package voluntier.util.produces;

import java.util.List;

import com.google.datastore.v1.QueryResultBatch.MoreResultsType;

import voluntier.util.eventdata.EventParticipantData;

public class EventParticipantsReturn {

	public List<EventParticipantData> participants;
	public Integer cursor;
	public String results;

	public EventParticipantsReturn() {
	}

	public EventParticipantsReturn(List<EventParticipantData> participants, Integer cursor, MoreResultsType results) {
		this.participants = participants;
		this.cursor = cursor;
		this.results = results.toString();
	}
}
