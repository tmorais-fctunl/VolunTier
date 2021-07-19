package voluntier.util.routedata;

import java.util.List;

import com.google.cloud.datastore.Entity;

import voluntier.exceptions.InexistentChatIdException;
import voluntier.exceptions.InexistentEventException;
import voluntier.exceptions.InexistentRatingException;
import voluntier.util.DB_Util;
import voluntier.util.eventdata.DB_Event;
import voluntier.util.produces.PicturesReturn;
import voluntier.util.produces.SearchEventReturn;

public class RouteDataReturn extends PicturesReturn {
	public String route_id;
	public List<SearchEventReturn> events;
	public String creation_date;
	public String creator;
	public String status;
	public double avg_rating;
	public int num_participants;
	
	public RouteDataReturn(Entity route, String user_email) throws InexistentRatingException, InexistentChatIdException {
		super(DB_Route.getPicturesURLs(route));
		this.route_id = route.getString(DB_Route.ID);
		this.creator = route.getString(DB_Route.CREATOR);
		this.creation_date = route.getString(DB_Route.CREATION_DATE);
		this.avg_rating = DB_Route.getAverageRating(route);
		this.num_participants = (int) route.getLong(DB_Route.NUM_PARTICIPANTS);
		this.status = DB_Route.getStatus(route, user_email).toString();
		
		List<String> event_ids = DB_Util.getStringList(route, DB_Route.EVENT_IDS);
		event_ids.forEach(id -> {
			Entity event;
			try {
				event = DB_Event.getEvent(id);
				SearchEventReturn data = new SearchEventReturn(event);
				events.add(data);
			} catch (InexistentEventException e) {}
		});
	}
}
