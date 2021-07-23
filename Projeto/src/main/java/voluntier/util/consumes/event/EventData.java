package voluntier.util.consumes.event;

import voluntier.util.consumes.RequestData;

public class EventData extends RequestData {

	public String event_id;
	
	public EventData () {
	}
	
	public EventData (String email, String token, String event_id) {
		super(email, token);
		this.event_id = event_id;
	}
	
	public boolean isValid () {
		return super.isValid() && event_id != null && !event_id.equals("");
	}
}
