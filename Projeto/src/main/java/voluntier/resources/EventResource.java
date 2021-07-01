package voluntier.resources;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.LatLng;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Transaction;
import com.google.cloud.datastore.Value;
import com.google.gson.Gson;

import voluntier.util.consumes.event.DeleteCommentData;
import voluntier.util.consumes.event.EventData;
import voluntier.util.consumes.event.GetChatData;
import voluntier.util.consumes.event.PostCommentData;

//import static com.google.datastore.v1.client.DatastoreHelper.makeValue;

import voluntier.util.userdata.*;

@Path("/event")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class EventResource {
	private static final int OFFSET = 3;
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());

	private final Gson g = new Gson();

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory usersFactory = datastore.newKeyFactory().setKind("User");
	private static KeyFactory sessionFactory = datastore.newKeyFactory().setKind("Session");
	private static KeyFactory eventFactory = datastore.newKeyFactory().setKind("Event");

	//private static KeyFactory chatFactory = datastore.newKeyFactory().setKind("Chat");
	// private static KeyFactory confirmationFactory =
	// datastore.newKeyFactory().setKind("Confirmation");

	public EventResource() {
	}

	@POST
	@Path("/addEvent")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createEvent(EventData data) {
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
				LOG.warning("Failed event creation attempt by user: " + data.email);
				return Response.status(Status.FORBIDDEN).entity("Token expired or invalid: " + data.email).build();
			}

			Key userKey = usersFactory.newKey(data.email);
			Entity user = txn.get(userKey);

			if (user == null || /*user.getString(DB_User.ACCOUNT).equals(Account.REMOVED.toString())*/
					ActionsResource.isRemovedOrBannedUser(user)) {
				txn.rollback();
				LOG.warning("User:" + user.getString(DB_User.EMAIL) + " cannot do this operation.");
				return Response.status(Status.FORBIDDEN).build();
			} else {
				Key eventKey = eventFactory.newKey(data.event_name);
				Entity event = txn.get(eventKey);

				if (event != null) {
					txn.rollback();
					LOG.warning("There is already an event with the name " + data.event_name);
					return Response.status(Status.FORBIDDEN).build();
				}

				LatLng latlng = LatLng.of(data.point[0], data.point[1]);
				//String[] chat = new String[2];
				//List<String> chat = new ArrayList<String>();
				ListValue.Builder list = ListValue.newBuilder();
				list.addValue(data.email, "novo comentario, ver como fica", data.timestamp);

				event = Entity.newBuilder(eventKey).set("event_id", data.event_id).set("user_email", data.email)
						.set("event_name", data.event_name).set("geo_point", latlng).set("date", data.getTimestamp())
						.set("chat", list.build())
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

	@POST
	@Path("/postMessage")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addMessage (PostCommentData data) {
		LOG.fine("Trying to add comment to event: " + data.event_name);

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
				LOG.warning("Failed comment attempt by user: " + data.email);
				return Response.status(Status.FORBIDDEN).entity("Token expired or invalid: " + data.email).build();
			}

			Key userKey = usersFactory.newKey(data.email);
			Entity user = txn.get(userKey);

			if (user == null || ActionsResource.isRemovedOrBannedUser(user)) {
				txn.rollback();
				LOG.warning("Failed comment attempt by user: " + data.email);
				return Response.status(Status.FORBIDDEN).entity("Session expired or invalid: " + data.email).build();
			}

			Key eventKey = eventFactory.newKey(data.event_name);
			Entity event = txn.get(eventKey);

			if (event == null) {
				txn.rollback();
				LOG.warning("There is no event with the name " + data.event_name);
				return Response.status(Status.BAD_REQUEST).build();
			}

			List<Value<?>> chat = event.getList("chat");

			ListValue.Builder newChat = ListValue.newBuilder();
			//String[] comment = new String[] {data.username, data.comment, data.timestamp};

			Iterator<Value<?>> it = chat.iterator();
			while (it.hasNext())
				newChat.addValue(it.next());

			newChat.addValue(data.username, data.comment, data.timestamp);

			event = Entity.newBuilder(eventKey)
					.set("event_name", event.getString("event_name"))
					.set("user_email", event.getString("user_email"))
					.set("event_id", event.getString("event_name"))
					.set("geo_point", event.getLatLng("geo_point"))
					.set("date", event.getTimestamp("date"))
					.set("chat", newChat.build())
					.build();

			txn.put(event);
			txn.commit();

			LOG.fine("Comment inserted correctly.");
			return Response.ok(Status.NO_CONTENT).build();

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

	@POST
	@Path("/deleteMessage")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteMessage (DeleteCommentData data) {
		LOG.fine("Trying to delete comment from event: " + data.event_name);

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
				LOG.warning("Failed delete comment attempt by user: " + data.email);
				return Response.status(Status.FORBIDDEN).entity("Token expired or invalid: " + data.email).build();
			}
			
			if (data.isValid())
				return Response.status(Status.GONE).build();

			Key eventKey = eventFactory.newKey(data.event_name);
			Entity event = txn.get(eventKey);

			if (event == null) {
				txn.rollback();
				LOG.warning("There is no event with the name " + data.event_name);
				return Response.status(Status.BAD_REQUEST).build();
			}
			
			List<Value<?>> chat = event.getList("chat");

			int firstIndex = data.comment_number*OFFSET;
			
			/*if (event != null)
				Response.status(Status.GONE).build();*/

			//removes the three strings from the one given in parameter
			for (int i = firstIndex; i < firstIndex + 3; i++)
				chat.remove(i);

			if (event != null)
				Response.status(Status.CONFLICT).build();
			
			ListValue.Builder newChat = ListValue.newBuilder();

			Iterator<Value<?>> it = chat.iterator();
			while (it.hasNext())
				newChat.addValue(it.next());

			event = Entity.newBuilder(eventKey)
					.set("event_name", event.getString("event_name"))
					.set("user_email", event.getString("user_email"))
					.set("event_id", event.getString("event_name"))
					.set("geo_point", event.getLatLng("geo_point"))
					.set("date", event.getTimestamp("date"))
					.set("chat", newChat.build())
					.build();
			
			if (event != null)
				Response.status(Status.EXPECTATION_FAILED).build();

			txn.put(event);
			txn.commit();

			LOG.fine("Comment deleted correctly.");
			return Response.ok(Status.NO_CONTENT).build();

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

	@POST
	@Path("/chat")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getChat (GetChatData data) {
		LOG.fine("Trying to get chat from event: " + data.event_name);

		// returns error if there is a bad request
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		Key tokenKey = sessionFactory.newKey(data.token);
		Entity token = txn.get(tokenKey);

		// check if the token corresponds to the user received and hasn't expired yet
		if (!TokensResource.isValidAccess(token, data.email)) {
			txn.rollback();
			LOG.warning("Failed retrieve chat attempt by user: " + data.email);
			return Response.status(Status.FORBIDDEN).entity("Token expired or invalid: " + data.email).build();
		}

		Key eventKey = eventFactory.newKey(data.event_name);
		Entity event = txn.get(eventKey);

		if (event == null) {
			txn.rollback();
			LOG.warning("There is no event with the name " + data.event_name);
			return Response.status(Status.BAD_REQUEST).build();
		}

		List<Value<?>> chat = event.getList("chat");
		
		return Response.ok(g.toJson(chat)).build();
	}

}
