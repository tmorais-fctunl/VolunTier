package voluntier.util.consumes.event;

import voluntier.util.consumes.RequestData;

public class DeleteCommentData extends RequestData{

	public String username;
	public String event_name;
	public int comment_number;
	
	public DeleteCommentData() {
	}
	
	public DeleteCommentData(String email, String token, String username, String event_name, int commment_number) {
		super(email, token);
		this.username = username;
		this.event_name = event_name;
		this.comment_number = commment_number;
	}
	
	public boolean isValid () {
		return super.isValid() && username != null && event_name != null && comment_number > 0;
	}
	
}
