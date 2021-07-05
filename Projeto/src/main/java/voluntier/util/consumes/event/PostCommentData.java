package voluntier.util.consumes.event;

import java.util.Random;

public class PostCommentData extends EventData {
	
	private static final int DEFALUT_COMMENT_SIZE = 500;

	public String comment;
	public String comment_id;
	Random rand = new Random();
	
	public PostCommentData() {
	}
	
	public PostCommentData (String email, String token, String event_id, String comment) {
		super(email, token, event_id);
		this.comment = comment;
		this.comment_id = String.valueOf(rand.nextInt(1000));
	}
	
	public String setId(int n_comment) {
		this.comment_id = "Comment" + n_comment + rand.nextInt(10000);
		return comment_id;
	}
	
	public boolean isValid () {
		return super.isValid() && comment != null && comment.length() < DEFALUT_COMMENT_SIZE;
	}
}
