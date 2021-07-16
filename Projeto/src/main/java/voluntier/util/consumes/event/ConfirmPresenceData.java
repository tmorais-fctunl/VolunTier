package voluntier.util.consumes.event;

public class ConfirmPresenceData extends EventData {
	public String code;
	
	public ConfirmPresenceData () {
	}
	
	public ConfirmPresenceData(String token, String email, String event_id, String code) {
		super(token, email, event_id);
		this.code = code;
	}
	
	public boolean isValid() {
		return super.isValid() && code != null;
	}
}
