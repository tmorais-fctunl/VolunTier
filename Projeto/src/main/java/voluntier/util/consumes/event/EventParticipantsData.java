package voluntier.util.consumes.event;

import voluntier.util.consumes.RequestData;

public class EventParticipantsData extends RequestData{

	public String event_id;
	public Integer cursor;
	
	public EventParticipantsData () {
	}
	
	public EventParticipantsData (String email, String token, String event_id, Integer cursor) {
		super(email, token);
		this.event_id = event_id;
		this.cursor = cursor;
	}
	
	public boolean isValid () {
		return super.isValid() && event_id != null;
	}
}
