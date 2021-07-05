package voluntier.resources;

import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Transaction;
import com.google.cloud.datastore.Value;
import com.google.gson.Gson;

import voluntier.util.consumes.event.DeleteCommentData;
import voluntier.util.consumes.event.CreateEventData;
import voluntier.util.consumes.event.EventData;
import voluntier.util.consumes.event.PostCommentData;
import voluntier.util.eventdata.DB_Event;
import voluntier.util.produces.EventReturn;

//import static com.google.datastore.v1.client.DatastoreHelper.makeValue;

import voluntier.util.userdata.*;

@Path("/")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class EventResource {
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
	public Response createEvent(CreateEventData data) {
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
				
				event = DB_Event.createNew(data, eventKey);

				txn.put(event);
				txn.commit();

				LOG.fine("Event: " + data.event_name + " inserted correctly.");
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
	@Path("/participateEvent")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response participateEvent (EventData data) {
		LOG.fine("User " + data.email + "trying to participate in event " + data.event_name);
		
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

			if ( event == null || !UpdateEventResource.isActive(event.getString(DB_Event.STATE)) 
					|| !UpdateEventResource.isPublic(event.getString(DB_Event.PROFILE))) {
				txn.rollback();
				LOG.warning("There is no event with the name " + data.event_name);
				return Response.status(Status.FORBIDDEN).build();
			}
			
			event = DB_Event.addParticipant(eventKey, event, data.email);
			
			txn.put(event);
			txn.commit();
			
			LOG.fine("Participant inserted correctly.");
			return Response.status(Status.NO_CONTENT).build();
			
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
	@Path("/postComment")
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

			if (event == null || UpdateEventResource.isActive(event.getString(DB_Event.PROFILE)) ) {
				txn.rollback();
				LOG.warning("There is no event with the name " + data.event_name);
				return Response.status(Status.BAD_REQUEST).build();
			}
			
			EventReturn comment = new EventReturn (data.email, data.comment, data.timestamp);
			
			event = DB_Event.postComment(eventKey, event, comment);
			
			txn.put(event);
			txn.commit();

			LOG.fine("Comment inserted correctly.");
			return Response.status(Status.NO_CONTENT).build();

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
	@Path("/deleteComment")
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

			Key eventKey = eventFactory.newKey(data.event_name);
			Entity event = txn.get(eventKey);

			if (event == null) {
				txn.rollback();
				LOG.warning("There is no event with the name " + data.event_name);
				return Response.status(Status.BAD_REQUEST).build();
			}

			event = DB_Event.deleteComment(eventKey, event, data.comment_number-1);
			
			txn.put(event);
			txn.commit();

			LOG.fine("Comment deleted correctly.");
			return Response.status(Status.NO_CONTENT).build();

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
	@Path("/getEventChat")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getChat (EventData data) {
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
