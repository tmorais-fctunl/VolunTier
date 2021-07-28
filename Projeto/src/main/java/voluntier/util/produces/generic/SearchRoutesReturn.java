package voluntier.util.produces.generic;

import java.util.LinkedList;
import java.util.List;

import com.google.cloud.datastore.Entity;

import voluntier.util.data.route.RouteDataReturn;

public class SearchRoutesReturn { 
	
	List<RouteDataReturn> routes;
	public String region_hash;
	
	public SearchRoutesReturn(List<Entity> ents, String geohash) {
		routes = new LinkedList<>();
		ents.forEach(route -> routes.add(new RouteDataReturn(route)));
		this.region_hash = geohash;
	}
}
