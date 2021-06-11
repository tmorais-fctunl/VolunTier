package voluntier.util.consumes;

import voluntier.util.Event;

public class EventData extends Event {
	
	public String token;
	
	public EventData() {}
	
	public EventData (String user_email, String token, String event_name, double[] latlng, String timestamp) {
		super(user_email, event_name, latlng, timestamp);
		this.token = token;
	}
	
	public boolean isValid () {
		return token!= null && super.isValid();
	}
}
