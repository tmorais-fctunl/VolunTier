package voluntier.util.consumes.event;

public class DeleteEventPictureData extends EventData {
	public String pic_id;
	
	public DeleteEventPictureData () {
	}
	
	public DeleteEventPictureData (String email, String token, String event_id, String pic_id) {
		super(email, token, event_id);
		this.pic_id = pic_id;
	}
	
	public boolean isValid () {
		return super.isValid() && pic_id != null;
	}
}
