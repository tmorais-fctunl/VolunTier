package voluntier.resources;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.LatLng;
import com.google.cloud.datastore.LatLngValue;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.Transaction;
import com.google.cloud.datastore.StructuredQuery.Builder;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

import voluntier.exceptions.InvalidTokenException;
import voluntier.util.JsonUtil;
import voluntier.util.consumes.RequestData;
import voluntier.util.consumes.event.EventData;
import voluntier.util.consumes.event.SearchByRange;
import voluntier.util.eventdata.DB_Event;
import voluntier.util.produces.CreateEventReturn;
import voluntier.util.produces.EventCoordinatesReturn;
import voluntier.util.produces.UserEventsReturn;
import voluntier.util.userdata.DB_User;
import voluntier.util.userdata.Profile;
import voluntier.util.userdata.State;

@Path("/")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class SearchEventResource {
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	//private static KeyFactory usersFactory = datastore.newKeyFactory().setKind("User");
	private static KeyFactory eventFactory = datastore.newKeyFactory().setKind("Event");

	public SearchEventResource() {
	}

	@POST
	@Path("/searchAllEvents")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createEvent(RequestData data) {
		//LOG.fine("Trying to find events near event : " + data.event_id);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
		TokensResource.checkIsValidAccess(data.token, data.email);
		} catch (InvalidTokenException e) {
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();
		}

		/*Key eventKey = eventFactory.newKey(data.event_id);
		Entity event = datastore.get(eventKey);

		if (event == null) {
			LOG.warning("There is no event with the following id " + data.event_id);
			return Response.status(Status.FORBIDDEN).build();
		}*/

		List<Entity> events = getAllEvents();
		
		List<EventCoordinatesReturn> events_return = new LinkedList<EventCoordinatesReturn>();
		
		for (Entity e : events)
			events_return.add(new EventCoordinatesReturn(e));

		LOG.fine("All events were searched and returned.");
		return Response.ok(JsonUtil.json.toJson(events_return)).build();
	}
	
	@POST
	@Path("/searchEventsByRange")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createEvent(SearchByRange data) {
		LOG.fine("Trying to find events near some location");

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
		TokensResource.checkIsValidAccess(data.token, data.email);
		} catch (InvalidTokenException e) {
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();
		}

		List<Entity> events = getEventsByArea(data.left_limit, data.right_limit);
		
		List<EventCoordinatesReturn> events_return = new LinkedList<EventCoordinatesReturn>();
		
		for (Entity e : events)
			events_return.add(new EventCoordinatesReturn(e));

		LOG.fine("All events were searched and returned.");
		return Response.ok(JsonUtil.json.toJson(events_return)).build();
	}
	

	private List<Entity> getAllEvents(){
		Builder<Entity> b = Query.newEntityQueryBuilder().setKind("Event");

		Query<Entity> query = b.build();

		QueryResults<Entity> res = datastore.run(query);

		List<Entity> events = new LinkedList<>();
		res.forEachRemaining(event -> {
			events.add(event);
		});

		return events;
	}
	
	private List<Entity> getEventsByArea (double[] left_limit, double[] right_limit) {
		Builder<Entity> b = Query.newEntityQueryBuilder().setKind("Event").
				setFilter(CompositeFilter.and(PropertyFilter.lt(DB_Event.LOCATION, LatLngValue.of(LatLng.of(left_limit[0], left_limit[1]))),
						PropertyFilter.gt(DB_Event.LOCATION, LatLngValue.of(LatLng.of(right_limit[0], right_limit[1]))),
						PropertyFilter.eq(DB_Event.PROFILE, Profile.PUBLIC.toString()),
						PropertyFilter.eq(DB_Event.STATE, State.ENABLED.toString()) )
				);

		Query<Entity> query = b.build();

		QueryResults<Entity> res = datastore.run(query);

		List<Entity> events = new LinkedList<>();
		res.forEachRemaining(event -> {
			events.add(event);
		});

		return events;
	}
}
