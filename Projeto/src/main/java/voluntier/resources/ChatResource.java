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

import voluntier.exceptions.ImpossibleActionException;
import voluntier.exceptions.InexistentChatIdException;
import voluntier.exceptions.InexistentLogIdException;
import voluntier.exceptions.InexistentMessageIdException;
import voluntier.exceptions.InexistentModeratorException;
import voluntier.exceptions.InexistentParticipantException;
import voluntier.exceptions.InexistentEventException;
import voluntier.exceptions.InvalidCursorException;
import voluntier.exceptions.InvalidTokenException;
import voluntier.exceptions.InexistentRouteException;
import voluntier.exceptions.InexistentUserException;
import voluntier.exceptions.SomethingWrongException;
import voluntier.util.JsonUtil;
import voluntier.util.consumes.chat.ChatData;
import voluntier.util.consumes.chat.ChatModeratorData;
import voluntier.util.consumes.chat.DeleteCommentData;
import voluntier.util.consumes.chat.LikeCommentData;
import voluntier.util.consumes.chat.PostCommentData;
import voluntier.util.consumes.chat.UpdateCommentData;
import voluntier.util.consumes.event.EventData;
import voluntier.util.data.event.DB_Event;
import voluntier.util.data.route.DB_Route;
import voluntier.util.data.user.*;
import voluntier.util.produces.chat.ChatReturn;
import voluntier.util.produces.chat.PostCommentReturn;
import voluntier.util.produces.event.EventModeratorsReturn;

@Path("/")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ChatResource {
	private static final Logger LOG = Logger.getLogger(ChatResource.class.getName());

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory usersFactory = datastore.newKeyFactory().setKind("User");

	public ChatResource() {
	}

	@POST
	@Path("/postComment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addComment(PostCommentData data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			Key userKey = usersFactory.newKey(data.email);
			Entity user = txn.get(userKey);

			if (user == null)
				throw new InexistentUserException();
			
			String username = user.getString(DB_User.USERNAME);
			
			Pair<List<Entity>, Integer> recieved_data = null;
			if(data.event_id != null)
				recieved_data = DB_Event.postComment(data.event_id, data.email, username, data.comment);
			if(data.route_id != null)
				recieved_data = DB_Route.postComment(data.route_id, data.email, username, data.comment);
			
			recieved_data.getValue0().forEach(ent -> txn.put(ent));

			txn.commit();

			LOG.fine("Comment inserted correctly.");
			return Response.ok(JsonUtil.json.toJson(new PostCommentReturn(recieved_data.getValue1()))).build();

		} catch (InvalidTokenException | InexistentChatIdException | InexistentLogIdException
				| InexistentParticipantException | ImpossibleActionException | InexistentEventException
				| InexistentRouteException | SomethingWrongException e) {
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
	@Path("/deleteComment")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteComment(DeleteCommentData data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);
			
			Entity event_or_route = null;
			if(data.event_id != null)
				event_or_route = DB_Event.deleteComment(data.event_id, data.comment_id, data.email);
			if(data.route_id != null)
				event_or_route = DB_Route.deleteComment(data.route_id, data.comment_id, data.email);
			
			txn.put(event_or_route);
			txn.commit();

			LOG.fine("Comment deleted correctly.");
			return Response.status(Status.NO_CONTENT).build();

		} catch (InvalidTokenException | ImpossibleActionException | InexistentMessageIdException
				| InexistentChatIdException | InexistentLogIdException | InexistentEventException | 
				InexistentRouteException | InexistentParticipantException e) {
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
	@Path("/updateComment")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateComment(UpdateCommentData data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);
			
			Entity event_or_route = null;
			if(data.event_id != null)
				event_or_route = DB_Event.updateComment(data.event_id, data.comment_id, data.email, data.comment);
			if(data.route_id != null)
				event_or_route = DB_Route.updateComment(data.route_id, data.comment_id, data.email, data.comment);
			
			txn.put(event_or_route);
			txn.commit();

			LOG.fine("Comment updated correctly.");
			return Response.status(Status.NO_CONTENT).build();

		} catch (InvalidTokenException | ImpossibleActionException | InexistentMessageIdException
				| InexistentChatIdException | InexistentLogIdException | InexistentEventException 
				| InexistentRouteException e) {
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
	@Path("/likeComment")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response likeComment(LikeCommentData data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);
			
			Entity event_or_route = null;
			if(data.event_id != null)
				event_or_route = DB_Event.giveOrRemoveLikeInComment(data.event_id, data.comment_id, data.email);
			if(data.route_id != null)
				event_or_route = DB_Route.giveOrRemoveLikeInComment(data.route_id, data.comment_id, data.email);
			
			txn.put(event_or_route);
			txn.commit();

			LOG.fine("Comment liked correctly.");
			return Response.status(Status.NO_CONTENT).build();

		} catch (InvalidTokenException | InexistentChatIdException | InexistentLogIdException
				| InexistentMessageIdException | InexistentParticipantException | ImpossibleActionException
				| InexistentEventException e) {
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
	@Path("/getChat")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getChat(ChatData data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);
			
			ChatReturn messages = null;
			if(data.event_id != null)
				messages = DB_Event.getChat(data.event_id, data.cursor,
					data.latest_first, data.email);
			if(data.route_id != null)
				messages = DB_Route.getChat(data.route_id, data.cursor,
						data.latest_first, data.email);
			
			return Response
					.ok(JsonUtil.json.toJson(messages)).build();

		} catch (InvalidTokenException | InexistentChatIdException | InvalidCursorException | InexistentLogIdException
				| InexistentParticipantException | InexistentEventException  | InexistentRouteException e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path("/moderator/add")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response makeChatModerator(ChatModeratorData data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);
			
			Entity event_or_route = null;
			if(data.event_id != null)
				event_or_route = DB_Event.makeChatModerator(data.event_id, data.email, data.mod);
			if(data.route_id != null)
				event_or_route = DB_Route.makeChatModerator(data.route_id, data.email, data.mod);
			
			txn.put(event_or_route);
			txn.commit();

			LOG.fine("Added " + data.email + "to chat moderators");
			return Response.status(Status.NO_CONTENT).build();

		} catch (InvalidTokenException | ImpossibleActionException | InexistentChatIdException
				| InexistentParticipantException | InexistentEventException | InexistentRouteException e) {
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
	@Path("/moderator/remove")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response removeChatModerator(ChatModeratorData data) {
		LOG.fine("Trying to make chat moderator in event: " + data.event_id);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			String req_email = data.email;

			Entity event_or_route = null;
			if(data.event_id != null)
				event_or_route = DB_Event.removeChatModerator(data.event_id, req_email, data.mod);
			if(data.route_id != null)
				event_or_route = DB_Route.removeChatModerator(data.route_id, req_email, data.mod);
			
			txn.put(event_or_route);
			txn.commit();

			LOG.fine("Removed " + data.mod + "from chat moderators");
			return Response.status(Status.NO_CONTENT).build();

		} catch (InvalidTokenException | InexistentChatIdException | ImpossibleActionException
				| InexistentModeratorException | InexistentEventException | InexistentRouteException e) {
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
	@Path("/moderator/getall")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getChatModerators(EventData data) {
		LOG.fine("Trying to get event moderators: " + data.event_id);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			List<String> moderators = DB_Event.getChatModerators(data.event_id, data.email);
			return Response.ok(JsonUtil.json.toJson(new EventModeratorsReturn(moderators))).build();

		} catch (InvalidTokenException | InexistentChatIdException | InexistentParticipantException
				| InexistentEventException e) {
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
}
