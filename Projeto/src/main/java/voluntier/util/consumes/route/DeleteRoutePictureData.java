package voluntier.util.consumes.route;

public class DeleteRoutePictureData extends RouteData {
	public String pic_id;
	
	public DeleteRoutePictureData () {
	}
	
	public DeleteRoutePictureData (String email, String token, String route_id, String pic_id) {
		super(email, token, route_id);
		this.pic_id = pic_id;
	}
	
	public boolean isValid () {
		return super.isValid() && pic_id != null;
	}
}
