package voluntier.util.consumes.chat;

import voluntier.util.consumes.generic.RequestData;

public class ChatData extends RequestData {

	public String event_id;
	public String route_id;
	public Integer cursor;
	public boolean latest_first = true;
	
	public ChatData () {
	}
	
	public ChatData (String email, String token, String event_id, String route_id, Integer cursor, boolean latest_first) {
		super(email, token);
		this.event_id = event_id;
		this.route_id = route_id;
		this.cursor = cursor;
		this.latest_first = latest_first;
	}
	
	public boolean isValid () {
		return super.isValid() && (event_id != null || route_id != null);
	}
}
