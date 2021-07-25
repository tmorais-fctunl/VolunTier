package voluntier.util.consumes.event;

import voluntier.util.consumes.generic.RequestData;

public class ParticipantsData extends RequestData {

	public String route_id;
	public String event_id;
	public Integer cursor;
	
	public ParticipantsData () {
	}
	
	public ParticipantsData (String email, String token, String event_id, String route_id, Integer cursor) {
		super(email, token);
		this.route_id = route_id;
		this.event_id = event_id;
		this.cursor = cursor;
	}
	
	public boolean isValid () {
		return super.isValid() && (event_id != null || route_id != null);
	}
}
