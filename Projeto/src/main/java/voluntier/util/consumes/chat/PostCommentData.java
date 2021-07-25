package voluntier.util.consumes.chat;

import voluntier.util.consumes.generic.RequestData;
import voluntier.util.data.event.DB_Event;

public class PostCommentData extends RequestData {

	public String comment;
	public String event_id;
	public String route_id;
	
	public PostCommentData() {
	}
	
	public PostCommentData (String email, String token, String event_id, String route_id, String comment) {
		super(email, token);
		this.event_id = event_id;
		this.route_id = route_id;
		this.comment = comment;
	}
	
	public boolean isValid () {
		return super.isValid() && (event_id != null || route_id != null) && comment != null && comment.length() < DB_Event.MAX_COMMENT_SIZE;
	}
}
