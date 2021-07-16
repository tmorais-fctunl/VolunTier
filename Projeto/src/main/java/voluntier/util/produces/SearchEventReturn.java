package voluntier.util.produces;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.LatLng;

import voluntier.util.eventdata.DB_Event;

public class SearchEventReturn {
	public String name;
	public String event_id;
	public double[] location;
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
		this.location = new double[] { ll.getLatitude(), ll.getLongitude() };
		this.start_date = event.getString(DB_Event.START_DATE);
		this.end_date = event.getString(DB_Event.END_DATE);
		this.category = event.getString(DB_Event.CATEGORY);
		this.visibility = event.getString(DB_Event.PROFILE);

		this.num_participants = (int) event.getLong(DB_Event.N_PARTICIPANTS);
	}
}
