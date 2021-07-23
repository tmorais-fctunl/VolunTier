package voluntier.util.consumes.route;

import voluntier.util.consumes.RequestData;

public class RouteData extends RequestData{

	public String route_id;
	
	public RouteData () {
	}
	
	public RouteData (String email, String token, String route_id) {
		super(email, token);
		this.route_id = route_id;
	}
	
	public boolean isValid () {
		return super.isValid() && route_id != null && !route_id.equals("");
	}
}
