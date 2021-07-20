package voluntier.util.routedata;

import java.util.List;

import com.google.cloud.datastore.Entity;

import voluntier.exceptions.InexistentChatIdException;
import voluntier.exceptions.InexistentRatingException;
import voluntier.util.DB_Util;
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
	
	public RouteDataReturn(Entity route, String user_email) {
		super(DB_Route.getPicturesDownloadURLs(route));
		this.route_id = route.getString(DB_Route.ID);
		this.creator = route.getString(DB_Route.CREATOR);
		this.creation_date = route.getTimestamp(DB_Route.CREATION_DATE).toString();
		
		try {
			this.avg_rating = DB_Route.getAverageRating(route);
			this.status = DB_Route.getStatus(route, user_email).toString();
		} catch (InexistentRatingException | InexistentChatIdException e) {}
		
		this.num_participants = (int) route.getLong(DB_Route.NUM_PARTICIPANTS);

		
		events = DB_Util.getJsonList(route, DB_Route.EVENTS, SearchEventReturn.class);
	}
}
