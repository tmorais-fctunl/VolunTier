package voluntier.util.consumes.event;

import java.util.UUID;

import com.google.cloud.Timestamp;

public class Event {

	public String email;
	public String event_name;
	public double[] point;
	public String event_id;
	
	/*public String description;
	public String[] participants;*/
	public String timestamp;
	
	public Event (){	
		event_id = UUID.randomUUID().toString();
	}
	
	public Event (String user_email, String event_name, double[] point, String timestamp) {
		this.email = user_email;
		this.event_name = event_name;
		this.point = point;
		event_id = UUID.randomUUID().toString();
		this.timestamp = timestamp;
	}
	
	public boolean isValid() {
		return email != null && point!=null;
	}
	
	public Timestamp getTimestamp () {
		return Timestamp.parseTimestamp(timestamp);
	}
	
}
