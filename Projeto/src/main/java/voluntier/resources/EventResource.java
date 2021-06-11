package voluntier.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.LatLng;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

import voluntier.util.consumes.EventData;

//import static com.google.datastore.v1.client.DatastoreHelper.makeValue;

import voluntier.util.userdata.*;

@Path("/event")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class EventResource {
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());

	private final Gson g = new Gson();

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory usersFactory = datastore.newKeyFactory().setKind("User");
	private static KeyFactory sessionFactory = datastore.newKeyFactory().setKind("Session");
	private static KeyFactory eventFactory = datastore.newKeyFactory().setKind("Event");
	// private static KeyFactory confirmationFactory =
	// datastore.newKeyFactory().setKind("Confirmation");

	public EventResource() {
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addEvent(EventData data) {
		LOG.fine("Trying to add event to user: " + data.email);

		// returns error if there is a bad request
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {

			Key tokenKey = sessionFactory.newKey(data.token);
			Entity token = txn.get(tokenKey);

			// check if the token corresponds to the user received and hasn't expired yet
			if (!TokensResource.isValidAccess(token, data.email)) {
				txn.rollback();
				LOG.warning("Failed logout attempt by user: " + data.email);
				return Response.status(Status.FORBIDDEN).entity("Token expired or invalid: " + data.email).build();
			}

			Key userKey = usersFactory.newKey(data.email);
			Entity user = txn.get(userKey);

			if (user == null || user.getString(DB_User.ACCOUNT).equals(Account.REMOVED.toString())) {
				txn.rollback();
				LOG.warning("User:" + user.getString(DB_User.EMAIL)
						+ " is trying to remove an inexistent or already removed user: " + data.email);
				return Response.status(Status.FORBIDDEN).build();
			} else {
				Key eventKey = eventFactory.newKey(data.event_id);

				LatLng latlng = LatLng.of(data.point[0], data.point[1]);

				Entity event = Entity.newBuilder(eventKey).set("event_id", data.event_id).set("user_email", data.email)
						.set("event_name", data.event_name).set("geo_point", latlng).set("date", data.getTimestamp())
						.build();

				txn.put(event);
				txn.commit();

				LOG.fine("User: " + data.email + " inserted correctly.");
				return Response.ok(g.toJson(data)).build();
			}

		} catch (Exception e) {
			txn.rollback();
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
	}

}
