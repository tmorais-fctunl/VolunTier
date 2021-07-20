package voluntier.util.produces;

import java.util.LinkedList;
import java.util.List;

import com.google.cloud.datastore.Entity;

import voluntier.util.routedata.RouteDataReturn;

public class SearchRoutesReturn { 
	
	List<RouteDataReturn> routes;
	public String region_hash;
	
	public SearchRoutesReturn(List<Entity> ents, String geohash, String user_email) {
		routes = new LinkedList<>();
		ents.forEach(route -> routes.add(new RouteDataReturn(route, user_email)));
		this.region_hash = geohash;
	}
}
