package voluntier.util.consumes.event;

public class DeleteCommentData extends EventData {

	public Integer comment_id;
	
	public DeleteCommentData() {
	}
	
	public DeleteCommentData(String email, String token, String event_id, Integer commment_id) {
		super(email, token, event_id);
		this.comment_id = commment_id;
	}
	
	public boolean isValid () {
		return super.isValid() && comment_id != null;
	}
	
}
