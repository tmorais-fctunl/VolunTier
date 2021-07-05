package voluntier.util.consumes.event;

import java.time.format.DateTimeParseException;
import java.util.UUID;

import com.google.cloud.Timestamp;

import voluntier.util.consumes.RequestData;

public class CreateEventData extends RequestData {
	
	public String event_name;
	public String event_id;
	public double[] location;
	public String date;
	
	public CreateEventData() {
		event_id = UUID.randomUUID().toString();
	}
	
	public CreateEventData (String user_email, String token, String event_name, double[] location, String date) {
		super(user_email, token);
		this.event_name = event_name;
		this.location = location;
		event_id = UUID.randomUUID().toString();
		this.date = date;
	}
	
	public boolean isValid () {
		try {
			Timestamp.parseTimestamp(date);
			return super.isValid() && event_name != null && location != null && date != null;
		} catch (DateTimeParseException e) {
			return false;
		}
	}
	
	public Timestamp getDateTimestamp () {
		return Timestamp.parseTimestamp(date);
	}
	
	
	
	/*public LatLng getLocatoinCoord () {
		return LatLng.of(location[0], location[1]);
	}*/
	
}
