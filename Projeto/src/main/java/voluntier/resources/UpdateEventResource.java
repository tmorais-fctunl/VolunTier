package voluntier.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Transaction;

import voluntier.exceptions.InvalidTokenException;
import voluntier.exceptions.InexistentEventException;
import voluntier.exceptions.ImpossibleActionException;
import voluntier.exceptions.IllegalCoordinatesException;
import voluntier.util.consumes.event.EventData;
import voluntier.util.consumes.event.UpdateEventData;
import voluntier.util.consumes.event.UpdateProfileData;
import voluntier.util.eventdata.DB_Event;
import voluntier.util.userdata.DB_User;
import voluntier.util.userdata.State;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/updateEvent")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class UpdateEventResource {

	private static final Logger LOG = Logger.getLogger(UpdateResource.class.getName());

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory usersFactory = datastore.newKeyFactory().setKind("User");

	public UpdateEventResource() {
	}

	@POST
	@Path("/attributes")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateAttributes(UpdateEventData data) {
		LOG.fine("Attempt to change event " + data.event_id + " attributes.");

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			Key userKey = usersFactory.newKey(data.email);
			Entity user = txn.get(userKey);

			// o user tem de existir e estar em condicoes
			if (user == null || ActionsResource.isRemovedOrBannedUser(user)) {
				txn.rollback();
				LOG.warning("User can't request this opetation.");
				return Response.status(Status.FORBIDDEN).entity("Invalid user").build();
			}

			Entity event = DB_Event.updateProperty(data);

			txn.put(event);
			txn.commit();

			LOG.fine("User " + data.email + " updated the attributes of event " + data.event_id);
			return Response.status(Status.NO_CONTENT).build();

		} catch (InvalidTokenException | InexistentEventException | 
				ImpossibleActionException | IllegalCoordinatesException e) {
			txn.rollback();
			LOG.severe(e.getMessage());
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
	@Path("/remove")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response removeEvent(EventData data) {
		LOG.fine("Trying to delete event " + data.event_id);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			Key userKey = usersFactory.newKey(data.email);
			Entity user = txn.get(userKey);

			if (user == null || ActionsResource.isRemovedOrBannedUser(user)) {
				txn.rollback();
				LOG.warning("User can't request this opetation.");
				return Response.status(Status.FORBIDDEN).entity("Invalid user").build();
			}

			Entity event = DB_Event.updateState(data.event_id, data.email, State.BANNED.toString());
			Entity updated_user = DB_User.removeEvent(userKey, user, data.event_id);
			
			txn.put(event, updated_user);
			txn.commit();

			LOG.fine("User " + data.email + " deleted event " + data.event_id);
			return Response.status(Status.NO_CONTENT).build();

		} catch (InvalidTokenException | ImpossibleActionException | InexistentEventException e) {
			txn.rollback();
			LOG.severe(e.getMessage());
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
	@Path("/profile")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateProfileEvent(UpdateProfileData data) {
		LOG.fine("Trying to update state of event " + data.event_id);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			Key userKey = usersFactory.newKey(data.email);
			Entity user = txn.get(userKey);

			// o user tem de existir e estar em condicoes
			if (user == null || ActionsResource.isRemovedOrBannedUser(user)) {
				txn.rollback();
				LOG.warning("User can't request this opetation.");
				return Response.status(Status.FORBIDDEN).entity("Invalid user").build();
			}

			Entity event = DB_Event.updateProfile(data.event_id, data.email, data.profile);

			txn.put(event);
			txn.commit();

			LOG.fine("User " + data.email + " deleted event " + data.event_id);
			return Response.status(Status.NO_CONTENT).build();

		} catch (InvalidTokenException | ImpossibleActionException | InexistentEventException e) {
			txn.rollback();
			LOG.severe(e.getMessage());
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
}
