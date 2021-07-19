package voluntier.util.consumes.event;

import voluntier.util.consumes.RequestData;

public class ChatModeratorData extends RequestData {

	public String mod;
	public String event_id;
	public String route_id;

	public ChatModeratorData() {
	}

	public ChatModeratorData (String email, String token, String mod, String event_id, String route_id) {
		super(email, token);
		this.mod = mod;
		this.event_id = event_id;
		this.route_id = route_id;
	}

	public boolean isValid () {
		return super.isValid() && (event_id != null || route_id != null) && mod != null;
	}
}
