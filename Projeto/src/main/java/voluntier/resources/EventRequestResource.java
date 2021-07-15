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

import org.javatuples.Triplet;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Transaction;
import com.google.datastore.v1.QueryResultBatch.MoreResultsType;

import voluntier.exceptions.InexistentChatIdException;
import voluntier.exceptions.InexistentEventException;
import voluntier.exceptions.InexistentUserException;
import voluntier.exceptions.InvalidTokenException;
import voluntier.util.JsonUtil;
import voluntier.util.consumes.event.EventParticipantsData;
import voluntier.util.consumes.event.ParticipantData;
import voluntier.util.eventdata.DB_Event;
import voluntier.util.eventdata.EventParticipantData;
import voluntier.util.produces.EventParticipantsReturn;
import voluntier.util.userdata.DB_User;

@Path("/")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class EventRequestResource {
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory usersFactory = datastore.newKeyFactory().setKind("User");

	public EventRequestResource() {
	}

	@POST
	@Path("/acceptRequest")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response eventAcceptRequest (ParticipantData data) {
		LOG.fine("Trying to accept event participation from user: " + data.participant);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {
			
			TokensResource.checkIsValidAccess(data.token, data.email);

			Key userKey = usersFactory.newKey(data.participant);
			Entity target_user = txn.get(userKey);

			if (target_user == null || ActionsResource.isRemovedOrBannedUser(target_user)) {
				txn.rollback();
				LOG.warning("User:" + target_user.getString(DB_User.EMAIL) + " does not even exist.");
				return Response.status(Status.FORBIDDEN).build();
			}
			
			Entity event = DB_Event.acceptRequest(data.event_id, data.participant, data.email);
			
			txn.put(event);
			txn.commit();

			LOG.fine("User " + data.participant + " inserted in event correctly.");
			return Response.status(Status.NO_CONTENT).build();

		} catch (InvalidTokenException e) {
			txn.rollback();
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

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
	@Path("/declineRequest")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response eventDeclineRequest (ParticipantData data) {
		LOG.fine("Trying to accept event participation from user: " + data.participant);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			Key userKey = usersFactory.newKey(data.email);
			Entity user = txn.get(userKey);

			if (user == null || ActionsResource.isRemovedOrBannedUser(user)) {
				txn.rollback();
				LOG.warning("User: " + user.getString(DB_User.EMAIL) + " does not even exist.");
				return Response.status(Status.FORBIDDEN).build();
			}
			
			Entity event = DB_Event.declineRequest(data.event_id, data.participant, data.email);

			txn.put(event);
			txn.commit();

			LOG.fine("User " + data.participant + " deleted from event request list.");
			return Response.status(Status.NO_CONTENT).build();

		} catch (InvalidTokenException e) {
			txn.rollback();
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

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
	@Path("/getRequests")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response eventListRequests (EventParticipantsData data) 
			throws InvalidTokenException, InexistentChatIdException, InexistentEventException, InexistentUserException {
		LOG.fine("Trying to list participations from event: " + data.event_id + ". Request from: " + data.email);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);
			
			DB_Event.checkIsOwner(data.event_id, data.email);

			Triplet<List<EventParticipantData>, Integer, MoreResultsType> return_data = DB_Event
					.getEventLists(data.event_id, data.cursor == null ? 0 : data.cursor, false);

			List<EventParticipantData> requests = return_data.getValue0();
			Integer cursor = return_data.getValue1();
			MoreResultsType result = return_data.getValue2();

			LOG.fine("Event: " + data.event_id + " requests presented correctly.");
			return Response.ok(JsonUtil.json.toJson(new EventParticipantsReturn(requests, cursor, result))).build();

		} catch (InvalidTokenException e) {
			
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();
		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}

	}

}