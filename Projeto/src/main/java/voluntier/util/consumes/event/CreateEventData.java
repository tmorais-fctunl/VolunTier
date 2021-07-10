package voluntier.util.consumes.event;

import java.time.format.DateTimeParseException;
import java.util.Random;

import com.google.cloud.Timestamp;

import voluntier.util.consumes.RequestData;
import voluntier.util.eventdata.DB_Event;
import voluntier.util.eventdata.EventData_Minimal;

public class CreateEventData extends RequestData {

	//private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());

	public String event_name;
	public String event_id;
	public double[] location;
	public String start_date;
	public String end_date;
	public String description;
	public String category;

	public String contact;
	public long capacity;


	public CreateEventData() {
	}

	/*public CreateEventData (String user_email, String token, String event_name, double[] location, 
			String start_date, String end_date, String description, String category) {
		super(user_email, token);
		this.event_name = event_name;
		this.location = location;
		this.start_date = start_date;
		this.end_date = end_date;
		this.description = description;
		this.category = category;

		this.contact = "";
		this.capacity = DB_Event.DEFAULT_CAPACITY;
	}*/

	public CreateEventData (String user_email, String token, String event_name, double[] location, 
			String start_date, String end_date, String description, String category, String contact, long capacity){
		super(user_email, token);
		this.event_name = event_name;
		this.location = location;
		this.start_date = start_date;
		this.end_date = end_date;
		this.description = description;
		this.category = category;

		this.contact = contact;
		this.capacity = capacity;
	}

	public boolean event_nameValid () {
		return event_name != null && event_name != "";
	}

	public boolean categoryValid () {
		return EventData_Minimal.categoryValid(category);
	}

	public boolean descriptionValid () {
		return description != null && description != "" && description.length() <= DB_Event.DEFALUT_COMMENT_SIZE;
	}

	public boolean locationValid () {
		return location != null && location[0] != -1 && location[1] != -1;
	}

	public boolean contactValid () {
		if (contact == null) {
			contact = "";
			return true;
		} 
		else if (contact.equals(""))
			return true;
		else return EventData_Minimal.contactValid(contact);
	}

	public boolean capacityValid () {
		if (capacity < 1) {
			capacity = DB_Event.DEFAULT_CAPACITY;
			return true;
		}
		else return EventData_Minimal.capacityValid(capacity);
	}

	public boolean isValid () {
		try {
			Timestamp.parseTimestamp(start_date);
			Timestamp.parseTimestamp(end_date);
			return super.isValid() && event_nameValid() && locationValid() && descriptionValid() 
					&& categoryValid() && contactValid() && capacityValid();
		} catch (DateTimeParseException e) {
			return false;
		}
	}

	public boolean optionalsValid () {
		return contactValid() && capacityValid();
	}

	public void generateID () {
		Random rand = new Random();
		this.event_id = "Event" + event_name.toLowerCase().replace(" ",  "") + Math.abs(rand.nextInt());
	}

	/*public LatLng getLocatoinCoord () {
		return LatLng.of(location[0], location[1]);
	}*/

}
