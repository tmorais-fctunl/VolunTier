package voluntier.util.consumes.route;

import java.util.List;

import voluntier.util.consumes.RequestData;
import voluntier.util.routedata.DB_Route;

public class CreateRouteData extends RequestData {

	//private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());

	public String route_name;
	public List<String> event_ids;
	public String description;

	public CreateRouteData() {
	}

	public CreateRouteData (String user_email, String token, String route_name, String description, List<String> event_ids){
		super(user_email, token);
		this.description = description;
		this.route_name = route_name;
		this.event_ids = event_ids;
	}

	public boolean nameValid () {
		return route_name != null && route_name != "" && route_name.length() <= DB_Route.MAX_NAME_SIZE;
	}

	public boolean descriptionValid () {
		return description != null && description != "" && description.length() <= DB_Route.MAX_DESCRIPTION_SIZE;
	}
	
	public boolean isValid () {
			return super.isValid() && nameValid() && descriptionValid() && event_ids != null && event_ids.size() >= 2;
	}

}
