package voluntier.util.produces.user;

import java.util.List;

import voluntier.util.data.route.RouteDataReturn;

public class UserRoutesReturn {
	
	public List<RouteDataReturn> routes;

	public UserRoutesReturn () {
	}
	
	public UserRoutesReturn (List<RouteDataReturn> routes) {
		this.routes = routes;
	}
}
