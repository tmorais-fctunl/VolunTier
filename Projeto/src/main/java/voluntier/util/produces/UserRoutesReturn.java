package voluntier.util.produces;

import java.util.List;

import voluntier.util.routedata.RouteDataReturn;

public class UserRoutesReturn {
	
	public List<RouteDataReturn> routes;

	public UserRoutesReturn () {
	}
	
	public UserRoutesReturn (List<RouteDataReturn> routes) {
		this.routes = routes;
	}
}
