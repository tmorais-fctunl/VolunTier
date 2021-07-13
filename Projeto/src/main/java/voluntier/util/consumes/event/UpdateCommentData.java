package voluntier.util.consumes.event;

import voluntier.util.eventdata.DB_Event;

public class UpdateCommentData extends EventData {
	
	public String comment;
	public Integer comment_id;
	
	public UpdateCommentData () {
	}
	
	public UpdateCommentData (String email, String token, String event_id, String comment, Integer comment_id) {
		super (email, token, event_id);
		this.comment = comment;
		this.comment_id = comment_id;
	}
	
	public boolean isValid () {
		return super.isValid() && comment != null && comment.length() < DB_Event.MAX_COMMENT_SIZE && comment_id != null;
	}

}
