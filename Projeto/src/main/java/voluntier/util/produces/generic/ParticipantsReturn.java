package voluntier.util.produces.generic;

import java.util.List;

import com.google.datastore.v1.QueryResultBatch.MoreResultsType;

import voluntier.util.data.event.ParticipantDataReturn;

public class ParticipantsReturn {

	public List<ParticipantDataReturn> participants;
	public Integer cursor;
	public String results;

	public ParticipantsReturn() {
	}

	public ParticipantsReturn(List<ParticipantDataReturn> participants, Integer cursor, MoreResultsType results) {
		this.participants = participants;
		this.cursor = cursor;
		this.results = results.toString();
	}
}
