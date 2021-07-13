package voluntier.util.produces;

import java.util.LinkedList;
import java.util.List;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.LatLng;

import voluntier.util.eventdata.DB_Event;

public class SearchEventsReturn {
	
	List<SearchEventReturn> events;
	public String region_hash;
	
	public SearchEventsReturn(List<Entity> ents, String geohash) {
		List<SearchEventReturn> events_return = new LinkedList<>();
		ents.forEach(event -> events_return.add(new SearchEventReturn(event)));
		this.region_hash = geohash;
	}
}
