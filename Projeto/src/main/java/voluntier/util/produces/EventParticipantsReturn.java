package voluntier.util.produces;

import java.util.LinkedList;
import java.util.List;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Value;

import voluntier.util.eventdata.DB_Event;

public class EventParticipantsReturn {

	public List<String> participants;

	public EventParticipantsReturn() {
	}

	public EventParticipantsReturn(Entity event) {
		participants = new LinkedList<String>();
		List<Value<?>> event_participants = event.getList(DB_Event.PARTICIPANTS);
		event_participants.forEach(participant -> {
			String participant_email = (String) participant.get();
			participants.add(participant_email);
		});
	}
}
