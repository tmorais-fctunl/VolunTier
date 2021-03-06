package voluntier.util.produces.event;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.LatLng;

import voluntier.util.data.event.DB_Event;

public class SearchEventReturn {
	public String name;
	public String event_id;
	public Double[] location;
	int num_participants;
	public String start_date;
	public String end_date;
	public String category;
	public String visibility;

	public SearchEventReturn() {
	}

	public SearchEventReturn(Entity event) {
		this.name = event.getString(DB_Event.NAME);
		this.event_id = event.getString(DB_Event.ID);
		LatLng ll = event.getLatLng(DB_Event.LOCATION);
		this.location = new Double[] { ll.getLatitude(), ll.getLongitude() };
		this.start_date = event.getString(DB_Event.START_DATE);
		this.end_date = event.getString(DB_Event.END_DATE);
		this.category = event.getString(DB_Event.CATEGORY);
		this.visibility = event.getString(DB_Event.PROFILE);

		this.num_participants = (int) event.getLong(DB_Event.N_PARTICIPANTS);
	}
	
	public SearchEventReturn(String id) {
		this.event_id = id;
	}
	
	public SearchEventReturn (String name, String event_id, String start_date, 
			String end_date, int num_participants, String visibility) {
		this.name = name;
		this.event_id = event_id;
		this.start_date = start_date;
		this.end_date = end_date;
		this.num_participants = num_participants;
		this.visibility = visibility;
	}
}
