package voluntier.util;

import java.util.UUID;

import com.google.cloud.Timestamp;

public class Event {

	public String user_id;
	public String event_name;
	public double[] point;
	public String event_id;
	
	/*public String description;
	public String[] participants;*/
	public String timestamp;
	
	public Event (){	
		event_id = UUID.randomUUID().toString();
	}
	
	public Event (String user_id, String event_name, double[] point, String timestamp) {
		this.user_id = user_id;
		this.event_name = event_name;
		this.point = point;
		event_id = UUID.randomUUID().toString();
		this.timestamp = timestamp;
	}
	
	public boolean isValid() {
		return user_id != null && point!=null;
	}
	
	public Timestamp getTimestamp () {
		return Timestamp.parseTimestamp(timestamp);
	}
	
}
