package voluntier.util.data.route;

import java.util.LinkedList;
import java.util.List;

import com.google.cloud.datastore.Entity;

import voluntier.exceptions.InexistentChatIdException;
import voluntier.exceptions.InexistentEventException;
import voluntier.exceptions.InexistentRatingException;
import voluntier.util.DB_Util;
import voluntier.util.data.event.DB_Event;
import voluntier.util.data.rating.DB_Rating;
import voluntier.util.data.rating.UserRatingData;
import voluntier.util.produces.event.SearchEventReturn;
import voluntier.util.produces.pictures.PicturesReturn;

public class RouteDataReturn extends PicturesReturn {
	public String route_name;
	public String description;
	public String route_id;
	public List<SearchEventReturn> events;
	public String creation_date;
	public String creator;
	public String status;
	public double avg_rating;
	public double my_rating;
	public int num_participants;
	
	public RouteDataReturn(Entity route, String user_email) {
		super(DB_Route.getPicturesDownloadURLs(route));
		this.route_name = route.getString(DB_Route.NAME);
		this.description = route.getString(DB_Route.DESCRIPTION);
		this.route_id = route.getString(DB_Route.ID);
		this.creator = route.getString(DB_Route.CREATOR);
		this.creation_date = route.getTimestamp(DB_Route.CREATION_DATE).toString();
		
		try {
			this.avg_rating = DB_Route.getAverageRating(route);
			this.status = DB_Route.getStatus(route, user_email).toString();
			
			UserRatingData rating = DB_Rating.getUserRating(route.getString(DB_Route.RATING_ID), user_email);
			this.my_rating = rating == null ? 0 : rating.rating;
			
		} catch (InexistentChatIdException | InexistentRatingException e) {}
		
		this.num_participants = (int) route.getLong(DB_Route.NUM_PARTICIPANTS);
		
		List<String> event_ids = DB_Util.getStringList(route, DB_Route.EVENTS);
		events = new LinkedList<>();
		event_ids.forEach(id-> {
			try {
				events.add(new SearchEventReturn(DB_Event.getEvent(id)));
			} catch (InexistentEventException e) {}
		});
	}
}
