package voluntier.util.consumes.chat;

import voluntier.util.consumes.generic.RequestData;
import voluntier.util.data.event.DB_Event;

public class UpdateCommentData extends RequestData {

	public String comment;
	public Integer comment_id;

	public String event_id;
	public String route_id;

	public UpdateCommentData() {
	}

	public UpdateCommentData(String email, String token, String event_id, String route_id, String comment,
			Integer comment_id) {
		super(email, token);
		this.comment = comment;
		this.comment_id = comment_id;
		this.event_id = event_id;
		this.route_id = route_id;
	}

	public boolean isValid() {
		return super.isValid() && (event_id != null || route_id != null) && comment != null
				&& comment.length() < DB_Event.MAX_COMMENT_SIZE && comment_id != null;
	}

}
