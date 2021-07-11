package voluntier.util.produces;

import java.util.List;

import voluntier.util.eventdata.EventParticipantData;

public class EventParticipantsReturn {

	public List<EventParticipantData> participants;

	public EventParticipantsReturn() {
	}

	public EventParticipantsReturn(List<EventParticipantData> participants) {
		this.participants = participants;
	}
}
