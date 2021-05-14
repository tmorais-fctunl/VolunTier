package voluntier.util;

public class EventData extends Event {
	
	public String token;
	
	public EventData() {}
	
	public EventData (String user_id, String token, String event_name, double[] latlng, String timestamp) {
		super(user_id, event_name, latlng, timestamp);
		this.token = token;
	}
	
	public boolean isValid () {
		return token!= null && super.isValid();
	}
}
