package voluntier.util.consumes.route;

public class RateData extends RouteData {

	//private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());

	public Float rating;

	public RateData() {
	}

	public RateData (String user_email, String token, String route_id, float rating){
		super(user_email, token, route_id);
		this.rating = rating;
	}
	
	public boolean isValid () {
			return super.isValid() && rating != null;
	}

}
