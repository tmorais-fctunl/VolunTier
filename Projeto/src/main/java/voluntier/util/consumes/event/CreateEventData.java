package voluntier.util.consumes.event;

import voluntier.util.consumes.generic.RequestData;
import voluntier.util.data.event.DB_Event;
import voluntier.util.data.event.EventData_Minimal;

public class CreateEventData extends RequestData {

	public String event_name;
	public double[] location;
	public String start_date;
	public String end_date;
	public String description;
	public String category;
	public String profile;

	public String contact;
	public Long capacity;
	
	public int difficulty;

	public CreateEventData() {
	}

	public CreateEventData(String user_email, String token, String event_name, double[] location, String profile,
			String start_date, String end_date, String description, String category, String contact, long capacity, int difficulty) {
		super(user_email, token);
		this.event_name = event_name;
		this.location = location;
		this.start_date = start_date;
		this.end_date = end_date;
		this.description = description;
		this.category = category;
		this.profile = profile;

		this.contact = contact;
		this.capacity = capacity;
		
		this.difficulty = difficulty;
	}

	public void contactDefault() {
		if (contact == null)
			contact = "";
	}

	public void capacityDefault() {
		if (capacity == null)
			capacity = DB_Event.DEFAULT_CAPACITY;
	}

	public boolean isValid() {
		setDefaultOptionals();
		return super.isValid() && EventData_Minimal.datesValid(start_date, end_date)
				&& EventData_Minimal.nameValid(event_name) && EventData_Minimal.locationValid(location)
				&& EventData_Minimal.descriptionValid(description) && EventData_Minimal.categoryValid(category)
				&& (contact == null || EventData_Minimal.contactValid(contact))
				&& (capacity == null || EventData_Minimal.capacityValid(capacity))
				&& EventData_Minimal.difficultyValid(difficulty)
				&& EventData_Minimal.profileValid(profile);
	}

	public void setDefaultOptionals() {
		contactDefault();
		capacityDefault();
	}

}
