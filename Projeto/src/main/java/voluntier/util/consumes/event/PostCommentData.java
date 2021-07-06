package voluntier.util.consumes.event;

import voluntier.util.eventdata.DB_Event;

public class PostCommentData extends EventData {

	public String comment;
	
	public PostCommentData() {
	}
	
	public PostCommentData (String email, String token, String event_id, String comment) {
		super(email, token, event_id);
		this.comment = comment;
	}
	
	public boolean isValid () {
		return super.isValid() && comment != null && comment.length() < DB_Event.DEFALUT_COMMENT_SIZE;
	}
}
