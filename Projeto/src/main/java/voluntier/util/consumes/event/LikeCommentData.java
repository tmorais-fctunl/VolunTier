package voluntier.util.consumes.event;

import voluntier.util.consumes.RequestData;

public class LikeCommentData extends RequestData {

	public Integer comment_id;
	public String event_id;
	public String route_id;
	
	public LikeCommentData() {
	}
	
	public LikeCommentData(String email, String token, String event_id, String route_id, Integer commment_id) {
		super(email, token);
		this.event_id = event_id;
		this.route_id = route_id;
		this.comment_id = commment_id;
	}
	
	public boolean isValid () {
		return super.isValid() && (event_id != null || route_id != null) && comment_id != null;
	}
	
}
