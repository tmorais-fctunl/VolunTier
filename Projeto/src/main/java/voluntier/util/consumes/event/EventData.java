package voluntier.util.consumes.event;

import voluntier.util.consumes.RequestData;

public class EventData extends RequestData{

	//public String username;
	public String event_name;
	
	public EventData () {
	}
	
	public EventData (String email, String token, String event_name) {
		super(email, token);
		this.event_name = event_name;
	}
	
	public boolean isValid () {
		return super.isValid() && event_name != null;
	}
}
