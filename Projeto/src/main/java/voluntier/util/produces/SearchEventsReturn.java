package voluntier.util.produces;

import java.util.LinkedList;
import java.util.List;

import com.google.cloud.datastore.Entity;

public class SearchEventsReturn { 
	
	List<SearchEventReturn> events;
	public String region_hash;
	
	public SearchEventsReturn(List<Entity> ents, String geohash) {
		events = new LinkedList<>();
		ents.forEach(event -> events.add(new SearchEventReturn(event)));
		this.region_hash = geohash;
	}
}
