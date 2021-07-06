package voluntier.util.produces;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.LatLng;

import voluntier.util.eventdata.DB_Event;

public class EventDataReturn {
	public String profile;
	
	public String name;
	public String event_id;
	public LatLng location;
	public String start_date;
	public String end_date;
	public String creation_date;
	
	public String owner_email;
	public String contact;

	public int num_participants;
	
	public String description;
	public String category;
	public long capacity;
	
	public String website;
	public String facebook;
	public String instagram;
	public String twitter;
	
	public EventDataReturn() {
	}
	
	public EventDataReturn (Entity event) {
		
		this.profile = event.getString(DB_Event.PROFILE);
		this.name = event.getString(DB_Event.NAME);
		this.event_id = event.getString(DB_Event.ID);
		this.location = event.getLatLng(DB_Event.LOCATION);
		this.start_date = event.getString(DB_Event.START_DATE);
		this.end_date = event.getString(DB_Event.END_DATE);
		this.creation_date = event.getString(DB_Event.CREATION_DATE);
		
		this.owner_email = event.getString(DB_Event.OWNER_EMAIL);
		this.contact = event.getString(DB_Event.CONTACT);

		this.num_participants = (int) event.getLong(DB_Event.N_PARTICIPANTS);
		
		this.description = event.getString(DB_Event.DESCRIPTION);
		this.category = event.getString(DB_Event.CATEGORY);
		this.capacity = event.getLong(DB_Event.CAPACITY);
		
		this.website = event.getString(DB_Event.WEBSITE);
		this.facebook = event.getString(DB_Event.FACEBOOK);
		this.instagram = event.getString(DB_Event.INSTAGRAM);
		this.twitter = event.getString(DB_Event.TWITTER);
	}
}
