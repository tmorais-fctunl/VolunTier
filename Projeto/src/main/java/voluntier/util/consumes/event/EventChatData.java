package voluntier.util.consumes.event;

import voluntier.util.consumes.RequestData;

public class EventChatData extends RequestData{

	public String event_id;
	public Integer cursor;
	public boolean latest_first = true;
	
	public EventChatData () {
	}
	
	public EventChatData (String email, String token, String event_id, Integer cursor, boolean latest_first) {
		super(email, token);
		this.event_id = event_id;
		this.cursor = cursor;
		this.latest_first = latest_first;
	}
	
	public boolean isValid () {
		return super.isValid() && event_id != null;
	}
}
