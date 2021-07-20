package voluntier.resources;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.javatuples.Pair;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.Builder;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Transaction;

import voluntier.exceptions.IllegalCoordinatesException;
import voluntier.exceptions.InvalidTokenException;
import voluntier.util.GeoHashUtil;
import voluntier.util.JsonUtil;
import voluntier.util.consumes.event.SearchByRange;
import voluntier.util.eventdata.DB_Event;
import voluntier.util.produces.SearchEventsReturn;
import voluntier.util.routedata.DB_Route;
import voluntier.util.userdata.State;

@Path("/")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class SearchByLocationResource {
	private static final Logger LOG = Logger.getLogger(SearchByLocationResource.class.getName());

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	//private static KeyFactory usersFactory = datastore.newKeyFactory().setKind("User");

	public SearchByLocationResource() {
	}
	
	@POST
	@Path("/searchEventsByRange")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEventsInSameRegion(SearchByRange data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			Pair<List<Entity>, String> events = queryEventsByArea(data.location);
			
			return Response.ok(JsonUtil.json.toJson(new SearchEventsReturn(events.getValue0(), events.getValue1()))).build();
			
		} catch (InvalidTokenException | IllegalCoordinatesException e) {
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();

		}
	}
	
	@POST
	@Path("/searchRoutesByRange")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRoutesInSameRegion(SearchByRange data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			Pair<List<Entity>, String> events = queryEventsByArea(data.location);
			
			return Response.ok(JsonUtil.json.toJson(new SearchEventsReturn(events.getValue0(), events.getValue1()))).build();
			
		} catch (InvalidTokenException | IllegalCoordinatesException e) {
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();

		}
	}
	
	private Pair<List<Entity>, String> queryEventsByArea (double[] location) throws IllegalCoordinatesException {
		String geohash = GeoHashUtil.convertCoordsToGeoHashLowPrecision(location[0], location[1]);
		
		Builder<Entity> b = Query.newEntityQueryBuilder().setKind("Event").
				setFilter(CompositeFilter.and(PropertyFilter.ge(DB_Event.GEOHASH, geohash),
						PropertyFilter.lt(DB_Event.GEOHASH, geohash + "z"),
						PropertyFilter.eq(DB_Event.STATE, State.ENABLED.toString()))
						);

		Query<Entity> query = b.build();

		QueryResults<Entity> res = datastore.run(query);

		List<Entity> events = new LinkedList<>();
		res.forEachRemaining(event -> {
			events.add(event);
		});

		return new Pair<>(events, geohash);
	}
	

	// FOR DEBUG ONLY, USE THIS TO ADD/CHANGE PROPERTIES TO ALL EXISTENT USERS
	@POST
	@Path("/events/rewrite")
	public Response rewriteEvents() {
	
		Transaction txn = datastore.newTransaction();
		List<Entity> events = getAllEvents();
		
		events.forEach(event -> {
			List<Entity> ents = DB_Event.REWRITE(event);
			ents.forEach(e -> txn.put(e));
		});
		
		txn.commit();
		
		return Response.ok().build();
	}

	// FOR DEBUG ONLY, USE THIS TO ADD/CHANGE PROPERTIES TO ALL EXISTENT USERS
		@POST
		@Path("/routes/rewrite")
		public Response rewriteRoutes() {
		
			Transaction txn = datastore.newTransaction();
			List<Entity> routes = getAllRoutes();
			
			routes.forEach(route -> {
				List<Entity> ents = DB_Route.REWRITE(route);
				ents.forEach(e -> txn.put(e));
			});
			
			txn.commit();
			
			return Response.ok().build();
		}
	
	private List<Entity> getAllEvents(){
		return getAll("Event");
	}
	
	private List<Entity> getAllRoutes(){
		return getAll("Routes");
	}
	
	private List<Entity> getAll(String type){
		Builder<Entity> b = Query.newEntityQueryBuilder().setKind(type);

		Query<Entity> query = b.build();

		QueryResults<Entity> res = datastore.run(query);

		List<Entity> events = new LinkedList<>();
		res.forEachRemaining(event -> {
			events.add(event);
		});

		return events;
	}
}
