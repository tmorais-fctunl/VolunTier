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
//import com.google.gson.Gson;

import voluntier.util.consumes.event.EventData;
import voluntier.util.consumes.event.UpdateEventData;
import voluntier.util.consumes.event.UpdateProfileData;
import voluntier.util.eventdata.DB_Event;
import voluntier.util.userdata.Profile;
import voluntier.util.userdata.State;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/updateEvent")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class UpdateEventResource {
	
	private static final Logger LOG = Logger.getLogger(UpdateResource.class.getName());

	//private final Gson json = new Gson();

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory usersFactory = datastore.newKeyFactory().setKind("User");
	private static KeyFactory sessionFactory = datastore.newKeyFactory().setKind("Session");
	private static KeyFactory eventFactory = datastore.newKeyFactory().setKind("Event");
	
	public UpdateEventResource() {
	}
	
	@POST
	@Path("/attributes")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateAttributes (UpdateEventData data) {
		LOG.fine("Attempt to change event " + data.event_name + " attributes.");
		
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();
		
		Transaction txn = datastore.newTransaction();
		
		try {

			Key tokenKey = sessionFactory.newKey(data.token);
			Entity token = txn.get(tokenKey);

			//o token tem de pertencer a quem faz o request
			if (!TokensResource.isValidAccess(token, data.email)) {
				txn.rollback();
				LOG.warning("Failed request to change attributes of event " + data.event_name + " by user " + data.email);
				return Response.status(Status.FORBIDDEN).entity("Invalid token").build();
			}
			
			Key userKey = usersFactory.newKey(data.email);
			Entity user = txn.get(userKey);
			
			//o user tem de existir e estar em condicoes
			if (user == null || ActionsResource.isRemovedOrBannedUser(user)) {
				txn.rollback();
				LOG.warning("User can't request this opetation.");
				return Response.status(Status.FORBIDDEN).entity("Invalid user").build();
			}
			
			Key eventKey = eventFactory.newKey(data.event_name);
			Entity event = txn.get(eventKey);
			
			if (event == null) {
				txn.rollback();
				LOG.warning("Event named " + data.event_name + " does not exist.");
				return Response.status(Status.BAD_REQUEST).entity("Invalid event").build();
			} else {
				String owner_email = event.getString(DB_Event.OWNER_EMAIL);
				String state = event.getString(DB_Event.STATE);

				// se nao for owner, nao pode alterar nada. vamos querer adicionar mais condicoes eventualmente...
				if ( !isOwner(data.email, owner_email) || !isActive(state) ) {
					txn.rollback();
					LOG.warning("User " + data.email + " can not change properties of event " + data.event_name + " or event does not exist");
					return Response.status(Status.FORBIDDEN).build();
				}
				
				event = DB_Event.updateProperty(data, eventKey, event);
				
				txn.put(event);
				txn.commit();
				
				LOG.fine("User " + data.email  + " updated the attributes of event " + data.event_name);
				return Response.status(Status.NO_CONTENT).build();
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
	@Path("/remove")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response removeEvent (EventData data) {
		LOG.fine("Trying to delete event " + data.event_name);
		
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();
		
		Transaction txn = datastore.newTransaction();
		
		try {

			Key tokenKey = sessionFactory.newKey(data.token);
			Entity token = txn.get(tokenKey);

			//o token tem de pertencer a quem faz o request
			if (!TokensResource.isValidAccess(token, data.email)) {
				txn.rollback();
				LOG.warning("Failed request to delete event " + data.event_name + " by user " + data.email);
				return Response.status(Status.FORBIDDEN).entity("Invalid token").build();
			}
			
			Key userKey = usersFactory.newKey(data.email);
			Entity user = txn.get(userKey);
			
			//o user tem de existir e estar em condicoes
			if (user == null || ActionsResource.isRemovedOrBannedUser(user)) {
				txn.rollback();
				LOG.warning("User can't request this opetation.");
				return Response.status(Status.FORBIDDEN).entity("Invalid user").build();
			}
			
			Key eventKey = eventFactory.newKey(data.event_name);
			Entity event = txn.get(eventKey);
			
			if (event == null) {
				txn.rollback();
				LOG.warning("Event named " + data.event_name + " does not exist.");
				return Response.status(Status.BAD_REQUEST).entity("Invalid event").build();
			} else {
				String owner_email = event.getString(DB_Event.OWNER_EMAIL);
				String state = event.getString(DB_Event.STATE);
				
				// se nao for owner, nao pode alterar nada. vamos querer adicionar mais condicoes eventualmente...
				if ( !isOwner(data.email, owner_email) || !isActive(state)) {
					txn.rollback();
					LOG.warning("User " + data.email + " can not delete event " + data.event_name);
					return Response.status(Status.FORBIDDEN).build();
				}
				
				event = DB_Event.updateState(eventKey, event, State.BANNED.toString());
				
				txn.put(event);
				txn.commit();
				
				LOG.fine("User " + data.email  + " deleted event " + data.event_name);
				return Response.status(Status.NO_CONTENT).build();
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
	
	//um metodo so para mudar o owner?, tem de ser melhor verificado que os restantes atributos..?
	
	@POST
	@Path("/profile")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateProfileEvent (UpdateProfileData data) {
		LOG.fine("Trying to update state of event " + data.event_name);
		
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();
		
		Transaction txn = datastore.newTransaction();
		
		try {

			Key tokenKey = sessionFactory.newKey(data.token);
			Entity token = txn.get(tokenKey);

			//o token tem de pertencer a quem faz o request
			if (!TokensResource.isValidAccess(token, data.email)) {
				txn.rollback();
				LOG.warning("Failed request to delete event " + data.event_name + " by user " + data.email);
				return Response.status(Status.FORBIDDEN).entity("Invalid token").build();
			}
			
			Key userKey = usersFactory.newKey(data.email);
			Entity user = txn.get(userKey);
			
			//o user tem de existir e estar em condicoes
			if (user == null || ActionsResource.isRemovedOrBannedUser(user)) {
				txn.rollback();
				LOG.warning("User can't request this opetation.");
				return Response.status(Status.FORBIDDEN).entity("Invalid user").build();
			}
			
			Key eventKey = eventFactory.newKey(data.event_name);
			Entity event = txn.get(eventKey);
			
			if (event == null) {
				txn.rollback();
				LOG.warning("Event named " + data.event_name + " does not exist.");
				return Response.status(Status.BAD_REQUEST).entity("Invalid event").build();
			} else {
				String owner_email = event.getString(DB_Event.OWNER_EMAIL);
				String state = event.getString(DB_Event.STATE);
				
				// se nao for owner, nao pode alterar nada. vamos querer adicionar mais condicoes eventualmente...
				if ( !isOwner(data.email, owner_email) || !isActive(state)) {
					txn.rollback();
					LOG.warning("User " + data.email + " can not update state of event " + data.event_name);
					return Response.status(Status.FORBIDDEN).build();
				}
				
				event = DB_Event.updateProfile(eventKey, event, data.profile);
				
				txn.put(event);
				txn.commit();
				
				LOG.fine("User " + data.email  + " deleted event " + data.event_name);
				return Response.status(Status.NO_CONTENT).build();
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
	
	static boolean isOwner (String email, String owner_emai) {
		return email.equals(owner_emai);
	}
	
	static boolean isActive (String state) {
		return state.equals(State.ENABLED.toString());
	}
	
	static boolean isPublic (String profile) {
		return profile.equals(Profile.PUBLIC.toString());
	}
	
	static boolean isFull (long capacity, long num_participants) {
		return num_participants <= capacity;
	}
	

}
