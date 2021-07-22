package voluntier.util.consumes.event;

public class UpdatePictureData extends EventData {

	public Integer pic_id;
	
	public UpdatePictureData () {
	}
	
	public UpdatePictureData (String email, String token, String event_id, Integer pic_id) {
		super(email, token, event_id);
		this.pic_id = pic_id;
	}
	
	public boolean isValid () {
		return super.isValid() && pic_id != null;
	}
}
