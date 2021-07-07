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

import org.javatuples.Pair;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Transaction;
import com.google.cloud.datastore.Value;

import voluntier.util.consumes.event.DeleteCommentData;
import voluntier.exceptions.ImpossibleActionException;
import voluntier.exceptions.InexistentCommentIdException;
import voluntier.util.JsonUtil;
import voluntier.util.consumes.event.CreateEventData;
import voluntier.util.consumes.event.EventData;
import voluntier.util.consumes.event.PostCommentData;
import voluntier.util.consumes.event.UpdateCommentData;
import voluntier.util.eventdata.DB_Event;
import voluntier.util.produces.ChatReturn;
import voluntier.util.produces.CreateEventReturn;
import voluntier.util.produces.EventDataReturn;
import voluntier.util.produces.EventParticipantsReturn;
import voluntier.util.produces.PostCommentReturn;
import voluntier.util.userdata.*;

@Path("/")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class EventResource {
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory usersFactory = datastore.newKeyFactory().setKind("User");
	private static KeyFactory sessionFactory = datastore.newKeyFactory().setKind("Session");
	private static KeyFactory eventFactory = datastore.newKeyFactory().setKind("Event");

	public EventResource() {
	}

	@POST
	@Path("/addEvent")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createEvent(CreateEventData data) {
		LOG.fine("Trying to add event to user: " + data.email);

		if (!data.isValid())
			return Response.status(Status.BAD_GATEWAY).build();

		Transaction txn = datastore.newTransaction();

		try {

			Key tokenKey = sessionFactory.newKey(data.token);
			Entity token = txn.get(tokenKey);

			if (!TokensResource.isValidAccess(token, data.email)) {
				txn.rollback();
				LOG.warning("Failed event creation attempt by user: " + data.email);
				return Response.status(Status.FORBIDDEN).entity("Token expired or invalid: " + data.email).build();
			}

			Key userKey = usersFactory.newKey(data.email);
			Entity user = txn.get(userKey);

			if (user == null || ActionsResource.isRemovedOrBannedUser(user)) {
				txn.rollback();
				LOG.warning("User:" + user.getString(DB_User.EMAIL) + " cannot do this operation.");
				return Response.status(Status.FORBIDDEN).build();
			} else {
				data.generateID();
				Key eventKey = eventFactory.newKey(data.event_id);
				Entity event = txn.get(eventKey);

				if (event != null) {
					txn.rollback();
					LOG.warning("There is already an event with the name " + data.event_name);
					return Response.status(Status.FORBIDDEN).build();
				}

				event = DB_Event.createNew(data, eventKey, data.email);

				txn.put(event);
				txn.commit();

				LOG.fine("Event: " + data.event_name + " inserted correctly.");
				return Response.ok(JsonUtil.json.toJson(new CreateEventReturn(data.event_id))).build();
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
	public Response participateEvent(EventData data) {
		LOG.fine("User " + data.email + "trying to participate in event " + data.event_id);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {

			Key tokenKey = sessionFactory.newKey(data.token);
			Entity token = txn.get(tokenKey);

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

			Key eventKey = eventFactory.newKey(data.event_id);
			Entity event = txn.get(eventKey);

			if (event == null || !UpdateEventResource.isActive(event.getString(DB_Event.STATE))
					|| !UpdateEventResource.isPublic(event.getString(DB_Event.PROFILE)) || !UpdateEventResource
							.isFull(event.getLong(DB_Event.CAPACITY), event.getLong(DB_Event.N_PARTICIPANTS) + 1)) {
				txn.rollback();
				LOG.warning("There is no event with the name " + data.event_id
						+ " or event is already full or it is a private event.");
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
	@Produces(MediaType.APPLICATION_JSON)
	public Response addMessage(PostCommentData data) {
		LOG.fine("Trying to add comment to event: " + data.event_id);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {

			Key tokenKey = sessionFactory.newKey(data.token);
			Entity token = txn.get(tokenKey);

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

			Key eventKey = eventFactory.newKey(data.event_id);
			Entity event = txn.get(eventKey);

			if (event == null || UpdateEventResource.isActive(event.getString(DB_Event.PROFILE))) {
				txn.rollback();
				LOG.warning("There is no event with the name " + data.event_id);
				return Response.status(Status.BAD_REQUEST).build();
			}

			Pair<Entity, String> recieved_data = DB_Event.postComment(eventKey, event, data.email, data.comment);

			txn.put(recieved_data.getValue0());
			txn.commit();

			LOG.fine("Comment inserted correctly.");
			return Response.ok(JsonUtil.json.toJson(new PostCommentReturn(recieved_data.getValue1()))).build();

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
	public Response deleteMessage(DeleteCommentData data) {
		LOG.fine("Trying to delete comment from event: " + data.event_id);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {

			Key tokenKey = sessionFactory.newKey(data.token);
			Entity token = txn.get(tokenKey);

			if (!TokensResource.isValidAccess(token, data.email)) {
				txn.rollback();
				LOG.warning("Failed delete comment attempt by user: " + data.email);
				return Response.status(Status.FORBIDDEN).entity("Token expired or invalid: " + data.email).build();
			}

			Key eventKey = eventFactory.newKey(data.event_id);
			Entity event = txn.get(eventKey);

			if (event == null) {
				txn.rollback();
				LOG.warning("There is no event with the name " + data.event_id);
				return Response.status(Status.FORBIDDEN).build();
			}

			try {
				event = DB_Event.deleteComment(eventKey, event, data.comment_id);
			} catch (InexistentCommentIdException e) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();
			}
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
	@Path("/updateComment")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateMessage(UpdateCommentData data) {
		LOG.fine("Trying to update comment from event: " + data.event_id);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {

			Key tokenKey = sessionFactory.newKey(data.token);
			Entity token = txn.get(tokenKey);

			if (!TokensResource.isValidAccess(token, data.email)) {
				txn.rollback();
				LOG.warning("Failed update comment attempt by user: " + data.email);
				return Response.status(Status.FORBIDDEN).entity("Token expired or invalid: " + data.email).build();
			}

			Key eventKey = eventFactory.newKey(data.event_id);
			Entity event = txn.get(eventKey);

			if (event == null) {
				txn.rollback();
				LOG.warning("There is no event with the name " + data.event_id);
				return Response.status(Status.BAD_REQUEST).build();
			}

			try {
				event = DB_Event.updateComment(eventKey, event, data.comment_id, data.email, data.comment);
			} catch (ImpossibleActionException | InexistentCommentIdException e) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();
			}

			txn.put(event);
			txn.commit();

			LOG.fine("Comment updated correctly.");
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
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getChat(EventData data) {
		LOG.fine("Trying to get chat from event: " + data.event_id);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			Key tokenKey = sessionFactory.newKey(data.token);
			Entity token = datastore.get(tokenKey);

			if (!TokensResource.isValidAccess(token, data.email)) {
				LOG.warning("Failed retrieve chat attempt by user: " + data.email);
				return Response.status(Status.FORBIDDEN).entity("Token expired or invalid: " + data.email).build();
			}

			Key eventKey = eventFactory.newKey(data.event_id);
			Entity event = datastore.get(eventKey);

			if (event == null) {
				LOG.warning("There is no event with the name " + data.event_id);
				return Response.status(Status.BAD_REQUEST).build();
			}

			List<Value<?>> chat = event.getList("chat");

			return Response.ok(JsonUtil.json.toJson(new ChatReturn(chat))).build();
		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path("/getEvent")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getEvent(EventData data) {
		LOG.fine("Trying to get event: " + data.event_id);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			Key tokenKey = sessionFactory.newKey(data.token);
			Entity token = datastore.get(tokenKey);

			if (!TokensResource.isValidAccess(token, data.email)) {
				LOG.warning("Failed retrieve event attempt by user: " + data.email);
				return Response.status(Status.FORBIDDEN).entity("Token expired or invalid: " + data.email).build();
			}

			Key eventKey = eventFactory.newKey(data.event_id);
			Entity event = datastore.get(eventKey);

			if (event == null) {
				LOG.warning("There is no event with the name " + data.event_id);
				return Response.status(Status.BAD_REQUEST).build();
			}

			return Response.ok(JsonUtil.json.toJson(new EventDataReturn(event))).build();
		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@POST
	@Path("/getParticipants")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getParticipants(EventData data) {
		LOG.fine("Trying to get event participants: " + data.event_id);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			if (!TokensResource.isValidAccess(data.token, data.email)) {
				LOG.warning("Failed retrieve event participants attempt by user: " + data.email);
				return Response.status(Status.FORBIDDEN).entity("Token expired or invalid: " + data.email).build();
			}

			Key eventKey = eventFactory.newKey(data.event_id);
			Entity event = datastore.get(eventKey);

			if (event == null) {
				LOG.warning("There is no event with the name " + data.event_id);
				return Response.status(Status.BAD_REQUEST).build();
			}

			return Response.ok(JsonUtil.json.toJson(new EventParticipantsReturn(event))).build();
		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

}
