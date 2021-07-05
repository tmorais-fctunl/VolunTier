package voluntier.util.produces;

public class EventReturn {
	
	public String username;
	public String comment;
	public String timestamp;
	
	public EventReturn () {
	}
	
	public EventReturn (String username, String comment, String timestamp) {
		this.username = username;
		this.comment = comment;
		this.timestamp = timestamp;
	}

}
