package voluntier.util.produces;

import java.util.List;

import com.google.datastore.v1.QueryResultBatch.MoreResultsType;

import voluntier.util.eventdata.ParticipantDataReturn;

public class EventParticipantsReturn {

	public List<ParticipantDataReturn> participants;
	public Integer cursor;
	public String results;

	public EventParticipantsReturn() {
	}

	public EventParticipantsReturn(List<ParticipantDataReturn> participants, Integer cursor, MoreResultsType results) {
		this.participants = participants;
		this.cursor = cursor;
		this.results = results.toString();
	}
}
