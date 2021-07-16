package voluntier.util.produces;

import java.util.List;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.LatLng;

import voluntier.util.eventdata.DB_Event;
import voluntier.util.userdata.StatusEvent;

public class EventDataReturn extends EventPicturesReturn {
	public String profile;
	
	public String name;
	public String event_id;
	public double[] location;
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
	
	public String status;
	public String owner_name;
		
	public EventDataReturn() {
	}
	
	public EventDataReturn (Entity event, List<DownloadEventPictureReturn> download_urls, StatusEvent status, String owner_name) {
		super(download_urls);
		
		this.profile = event.getString(DB_Event.PROFILE);
		this.name = event.getString(DB_Event.NAME);
		this.event_id = event.getString(DB_Event.ID);
		LatLng ll = event.getLatLng(DB_Event.LOCATION);
		this.location = new double[] {ll.getLatitude(), ll.getLongitude()};
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
		
		this.status = status.toString();
		this.owner_name = owner_name;
	}
}
