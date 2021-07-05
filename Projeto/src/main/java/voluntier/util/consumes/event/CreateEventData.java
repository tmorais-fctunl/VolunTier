package voluntier.util.consumes.event;

import java.time.format.DateTimeParseException;
import java.util.Random;
import java.util.logging.Logger;

import com.google.cloud.Timestamp;

import voluntier.resources.RegisterResource;
import voluntier.util.consumes.RequestData;

public class CreateEventData extends RequestData {
	
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());
	
	public String event_name;
	public String event_id;
	public double[] location;
	public String start_date;
	public String end_date;
	
	
	public CreateEventData() {
	}
	
	public CreateEventData (String user_email, String token, String event_name, double[] location, String start_date, String end_date) {
		super(user_email, token);
		LOG.warning("construtor outro");
		this.event_name = event_name;
		this.location = location;
		this.start_date = start_date;
		this.end_date = end_date;
	}
	
	public boolean isValid () {
		try {
			Timestamp.parseTimestamp(start_date);
			Timestamp.parseTimestamp(end_date);
			return super.isValid() && event_name != null && location != null;
		} catch (DateTimeParseException e) {
			return false;
		}
	}
	
	public void generateID () {
		Random rand = new Random();
		this.event_id = "Event" + event_name.toLowerCase() + rand.nextInt(100000);
	}
	
	/*public LatLng getLocatoinCoord () {
		return LatLng.of(location[0], location[1]);
	}*/
	
}
