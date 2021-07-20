package voluntier.resources;

import java.net.URL;
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
import org.javatuples.Triplet;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Transaction;
import com.google.datastore.v1.QueryResultBatch.MoreResultsType;

import voluntier.exceptions.ImpossibleActionException;
import voluntier.exceptions.InexistentChatIdException;
import voluntier.exceptions.IllegalCoordinatesException;
import voluntier.exceptions.InexistentModeratorException;
import voluntier.exceptions.InexistentParticipantException;
import voluntier.exceptions.InexistentPictureException;
import voluntier.exceptions.InexistentEventException;
import voluntier.exceptions.InvalidTokenException;
import voluntier.exceptions.MaximumSizeReachedException;
import voluntier.util.GoogleStorageUtil;
import voluntier.util.JsonUtil;
import voluntier.util.consumes.event.ConfirmPresenceData;
import voluntier.util.consumes.event.CreateEventData;
import voluntier.util.consumes.event.DeleteEventPictureData;
import voluntier.util.consumes.event.EventData;
import voluntier.util.consumes.event.ParticipantsData;
import voluntier.util.consumes.event.ParticipantData;
import voluntier.util.consumes.event.TargetData;
import voluntier.util.eventdata.DB_Event;
import voluntier.util.eventdata.ParticipantDataReturn;
import voluntier.util.produces.CreateEventReturn;
import voluntier.util.produces.DownloadEventPictureReturn;
import voluntier.util.produces.EventDataReturn;
import voluntier.util.produces.ParticipantsReturn;
import voluntier.util.produces.PicturesReturn;
import voluntier.util.produces.PresenceCodeReturn;
import voluntier.util.produces.SearchEventReturn;
import voluntier.util.produces.UploadPictureReturn;
import voluntier.util.produces.UserEventsReturn;
import voluntier.util.userdata.*;

@Path("/")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class EventResource {
	private static final Logger LOG = Logger.getLogger(EventResource.class.getName());

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory usersFactory = datastore.newKeyFactory().setKind("User");

	public EventResource() {
	}

	@POST
	@Path("/addEvent")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createEvent(CreateEventData data) {
		LOG.fine("Trying to add event to user: " + data.email);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			Key userKey = usersFactory.newKey(data.email);
			Entity user = txn.get(userKey);

			if (user == null || ActionsResource.isRemovedOrBannedUser(user)) {
				txn.rollback();
				LOG.warning("User:" + user.getString(DB_User.EMAIL) + " cannot do this operation.");
				return Response.status(Status.FORBIDDEN).build();
			}

			Triplet<List<Entity>, String, String> ents = DB_Event.createNew(data);
			String event_id = ents.getValue1();
			String filename = ents.getValue2();
			URL upload_url = GoogleStorageUtil.signURLForUpload(filename);
			Entity updated_user = DB_User.addEvent(userKey, user, event_id);

			ents.getValue0().forEach(ent -> txn.put(ent));
			txn.put(updated_user);
			txn.commit();

			LOG.fine("Event: " + data.event_name + " inserted correctly.");
			return Response.ok(JsonUtil.json.toJson(new CreateEventReturn(event_id, upload_url))).build();

		} catch (InvalidTokenException | IllegalCoordinatesException e) {
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
	@Path("/participateEvent")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response participateEvent(EventData data) {
		LOG.fine("User " + data.email + "trying to participate in event " + data.event_id);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			List<Entity> ents = DB_Event.participateInEvent(data.event_id, data.email, false);
			
			ents.forEach(e -> txn.put(e));
			
			txn.commit();

			return Response.status(Status.NO_CONTENT).build();

		} catch (InvalidTokenException | ImpossibleActionException | InexistentEventException e) {
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
	@Path("/removeParticipant")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response removeParticipant(ParticipantData data) {
		LOG.fine("Trying to make chat moderator in event: " + data.event_id);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			String req_email = data.email;

			List<Entity> ents = DB_Event.removeParticipant(data.event_id, data.participant, req_email);

			Key userKey = usersFactory.newKey(data.participant);
			Entity user = txn.get(userKey);
			Entity updated_user = DB_User.leaveEvent(userKey, user, data.event_id);
			if (data.email.equals(data.participant))
				txn.put(updated_user);

			ents.forEach(ent -> txn.put(ent));
			txn.commit();

			LOG.fine("Removed " + data.participant + "from event: " + data.event_id);
			return Response.status(Status.NO_CONTENT).build();

		} catch (InvalidTokenException | InexistentChatIdException | ImpossibleActionException
				| InexistentModeratorException | InexistentEventException e) {
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
	@Path("/getEvent")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getEvent(EventData data) {
		LOG.fine("Trying to get event: " + data.event_id);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			Triplet<Entity, ParticipantStatus, String> event = DB_Event.getEvent(data.event_id, data.email);
			List<DownloadEventPictureReturn> download_urls = DB_Event.getPicturesDownloadURLs(event.getValue0());

			return Response.ok(JsonUtil.json.toJson(new EventDataReturn(
					event.getValue0(), download_urls, event.getValue1(), event.getValue2()))).build();

		} catch (InvalidTokenException | InexistentEventException e) {
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path("/getParticipants")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getParticipants(ParticipantsData data) {
		LOG.fine("Trying to get event participants: " + data.event_id);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			Triplet<List<ParticipantDataReturn>, Integer, MoreResultsType> return_data = DB_Event
					.getEventLists(data.event_id, data.cursor == null ? 0 : data.cursor, true, null);

			List<ParticipantDataReturn> participants = return_data.getValue0();
			Integer cursor = return_data.getValue1();
			MoreResultsType result = return_data.getValue2();

			return Response.ok(JsonUtil.json.toJson(new ParticipantsReturn(participants, cursor, result))).build();

		} catch (InvalidTokenException | InexistentChatIdException | InexistentEventException e) {
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path("/user/events")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getUserEvents(TargetData data) {
		LOG.fine("Trying to get user events: " + data.target);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);
			Key userKey = usersFactory.newKey(data.target);
			Entity user = datastore.get(userKey);

			List<String> ids = DB_User.getEventIds(user);
			List<SearchEventReturn> events = new LinkedList<>();
			ids.forEach(id -> {
				try {
					events.add(new SearchEventReturn(DB_Event.getEvent(id)));
				} catch (InexistentEventException e) {}});
			
			return Response.ok(JsonUtil.json.toJson(new UserEventsReturn(events))).build();

		} catch (InvalidTokenException e) {
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path("/user/participatingEvents")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getUserParticipatingEvents(TargetData data) {
		LOG.fine("Trying to get user participating events: " + data.target);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);
			Key userKey = usersFactory.newKey(data.target);
			Entity user = datastore.get(userKey);

			List<String> ids = DB_User.getParticipatingEventIds(user);
			List<SearchEventReturn> events = new LinkedList<>();
			ids.forEach(id -> {
				try {
					events.add(new SearchEventReturn(DB_Event.getEvent(id)));
				} catch (InexistentEventException e) {}});
			return Response.ok(JsonUtil.json.toJson(new UserEventsReturn(events))).build();

		} catch (InvalidTokenException e) {
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path("/event/addPicture")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addPictureToEvent(EventData data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			Pair<Entity, String> res = DB_Event.addPicture(data.event_id, data.email);
			Entity event = res.getValue0();
			String filename = res.getValue1();

			URL upload_url = GoogleStorageUtil.signURLForUpload(filename);

			txn.put(event);
			txn.commit();

			return Response.ok(JsonUtil.json.toJson(new UploadPictureReturn(upload_url, filename))).build();

		} catch (InvalidTokenException | InexistentEventException | ImpossibleActionException
				| MaximumSizeReachedException e) {
			txn.rollback();
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			txn.rollback();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();

		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
	}

	@POST
	@Path("/event/deletePicture")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deletePictureFromEvent(DeleteEventPictureData data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			Entity updated_event = DB_Event.deletePicture(data.event_id, data.pic_id, data.email);

			txn.put(updated_event);
			txn.commit();

			return Response.status(Status.NO_CONTENT).build();

		} catch (InvalidTokenException | InexistentEventException | ImpossibleActionException
				| InexistentPictureException e) {
			txn.rollback();
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			txn.rollback();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();

		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
	}

	@POST
	@Path("/event/getPictures")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getPicturesFromEvent(EventData data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			List<DownloadEventPictureReturn> download_urls = DB_Event.getPicturesDownloadURLs(data.event_id);

			return Response.ok(JsonUtil.json.toJson(new PicturesReturn(download_urls))).build();

		} catch (InvalidTokenException | InexistentEventException e) {
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path("/event/presenceCode")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response requestPresenceCode(EventData data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			String code = DB_Event.getPresenceConfirmationCode(data.event_id, data.email);

			return Response.ok(JsonUtil.json.toJson(new PresenceCodeReturn(code))).build();

		} catch (InvalidTokenException | InexistentEventException | ImpossibleActionException e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path("/event/confirmPresence")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response confirmPresence(ConfirmPresenceData data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();
		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			Entity updated_event = DB_Event.confirmPresence(data.event_id, data.email);

			txn.put(updated_event);
			txn.commit();

			return Response.status(Status.NO_CONTENT).build();

		} catch (InvalidTokenException | InexistentEventException | 
				ImpossibleActionException | InexistentParticipantException e) {
			txn.rollback();
			LOG.severe(e.getMessage());
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			txn.rollback();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();

		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
	}

}
