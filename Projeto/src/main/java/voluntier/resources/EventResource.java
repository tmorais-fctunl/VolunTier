package voluntier.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.search.GeoPoint;
import com.google.cloud.datastore.LatLng;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Transaction;
import com.google.cloud.datastore.Value;
import com.google.gson.Gson;

//import static com.google.datastore.v1.client.DatastoreHelper.makeValue;

import voluntier.util.userdata.*;
import voluntier.util.EventData;
import voluntier.util.RequestData;

@Path ("/event")
@Produces (MediaType.APPLICATION_JSON + ";charset=utf-8")
public class EventResource {
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());
	
	private final Gson g = new Gson();
	
	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory usersFactory = datastore.newKeyFactory().setKind("User");
	private static KeyFactory sessionFactory = datastore.newKeyFactory().setKind("Session");
	private static KeyFactory eventFactory = datastore.newKeyFactory().setKind("Event");
	//private static KeyFactory confirmationFactory = datastore.newKeyFactory().setKind("Confirmation");
	
	
	public EventResource() {
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addEvent(EventData data) {
		LOG.fine("Trying to add event to user: " + data.user_id);

		//returns error if there is a bad request
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).entity("").build();

		Transaction txn = datastore.newTransaction ();

		try {
			
			Key tokenKey = sessionFactory.newKey(data.token);
			Entity token = txn.get(tokenKey);

			// check if the token corresponds to the user received and hasn't expired yet
			if(!TokensResource.isValidAccess(token, data.user_id)) {
				txn.rollback();
				LOG.warning("Failed logout attempt by user: " + data.user_id);
				return Response.status(Status.FORBIDDEN).entity("Token expired or invalid: " + data.user_id).build();
			}
			
			Key userKey = usersFactory.newKey(data.user_id);
			Entity user = txn.get(userKey);
			
			if(user == null || user.getString("user_account").equals(Account.REMOVED.toString())) {
				txn.rollback();
				LOG.warning("User:" + user.getString("user_id") + " is trying to remove an inexistent or already removed user: " + data.user_id);
				return Response.status(Status.FORBIDDEN).entity("").build();
			} else {
				Key eventKey = eventFactory.newKey(data.event_id);
				
				LatLng latlng = LatLng.of(data.point[0], data.point[1]);
				
				Entity event = Entity.newBuilder(eventKey)
						.set("event_id", data.event_id)
						.set("user_id", data.user_id)
						.set("event_name", data.event_name)
						.set("geo_point", latlng)
						.set("date", data.getTimestamp())
						.build();
				
				txn.put(event);
				txn.commit();
				
				LOG.fine("User: " + data.user_id + " inserted correctly.");
				return Response.ok(g.toJson(data)).build();
			}

		}  catch (Exception e) {
			txn.rollback();
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}

		//return Response.status(Status.OK).build();
	}
	
	/*private static Value gValue(LatLng k) {
	    return makeValue(k).setExcludeFromIndexes(true).build();
	}*/
	
}
