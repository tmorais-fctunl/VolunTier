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
import org.javatuples.Triplet;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Transaction;
import com.google.datastore.v1.QueryResultBatch.MoreResultsType;

import voluntier.util.consumes.event.DeleteCommentData;
import voluntier.util.consumes.event.EventChatData;
import voluntier.exceptions.ImpossibleActionException;
import voluntier.exceptions.InexistentChatIdException;
import voluntier.exceptions.InexistentLogIdException;
import voluntier.exceptions.InexistentMessageIdException;
import voluntier.exceptions.InexistentModeratorException;
import voluntier.exceptions.InexistentParticipantException;
import voluntier.exceptions.InexistentEventException;
import voluntier.exceptions.InvalidCursorException;
import voluntier.exceptions.InvalidTokenException;
import voluntier.util.JsonUtil;
import voluntier.util.consumes.event.EventData;
import voluntier.util.consumes.event.LikeCommentData;
import voluntier.util.consumes.event.ChatModeratorData;
import voluntier.util.consumes.event.PostCommentData;
import voluntier.util.consumes.event.UpdateCommentData;
import voluntier.util.eventdata.DB_Event;
import voluntier.util.eventdata.MessageDataReturn;
import voluntier.util.produces.ChatReturn;
import voluntier.util.produces.EventModeratorsReturn;
import voluntier.util.produces.PostCommentReturn;
import voluntier.util.userdata.*;

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
		LOG.fine("Trying to add comment to event: " + data.event_id);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			Key userKey = usersFactory.newKey(data.email);
			Entity user = txn.get(userKey);

			if (user == null || ActionsResource.isRemovedOrBannedUser(user)) {
				txn.rollback();
				LOG.warning("Failed comment attempt by user: " + data.email);
				return Response.status(Status.FORBIDDEN).entity("Session expired or invalid: " + data.email).build();
			}
			
			String username = user.getString(DB_User.USERNAME);

			Pair<List<Entity>, Integer> recieved_data = DB_Event.postComment(data.event_id, data.email, username, data.comment);

			recieved_data.getValue0().forEach(ent -> txn.put(ent));

			txn.commit();

			LOG.fine("Comment inserted correctly.");
			return Response.ok(JsonUtil.json.toJson(new PostCommentReturn(recieved_data.getValue1()))).build();

		} catch (InvalidTokenException | InexistentChatIdException | InexistentLogIdException
				| InexistentParticipantException | ImpossibleActionException | InexistentEventException e) {
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
	@Path("/deleteComment")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteComment(DeleteCommentData data) {
		LOG.fine("Trying to delete comment from event: " + data.event_id);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			Entity event = DB_Event.deleteComment(data.event_id, data.comment_id, data.email);
			txn.put(event);
			txn.commit();

			LOG.fine("Comment deleted correctly.");
			return Response.status(Status.NO_CONTENT).build();

		} catch (InvalidTokenException | ImpossibleActionException | InexistentMessageIdException
				| InexistentChatIdException | InexistentLogIdException | InexistentEventException e) {
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
	@Path("/updateComment")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateComment(UpdateCommentData data) {
		LOG.fine("Trying to update comment from event: " + data.event_id);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			Entity event = DB_Event.updateComment(data.event_id, data.comment_id, data.email, data.comment);

			txn.put(event);
			txn.commit();

			LOG.fine("Comment updated correctly.");
			return Response.status(Status.NO_CONTENT).build();

		} catch (InvalidTokenException | ImpossibleActionException | InexistentMessageIdException
				| InexistentChatIdException | InexistentLogIdException | InexistentEventException e) {
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
		LOG.fine("Trying to like a comment from event: " + data.event_id);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			Entity event = DB_Event.giveOrRemoveLikeInComment(data.event_id, data.comment_id, data.email);

			txn.put(event);
			txn.commit();

			LOG.fine("Comment updated correctly.");
			return Response.status(Status.NO_CONTENT).build();

		} catch (InvalidTokenException | InexistentChatIdException | InexistentLogIdException
				| InexistentMessageIdException | InexistentParticipantException | ImpossibleActionException
				| InexistentEventException e) {
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
	@Path("/getEventChat")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getChat(EventChatData data) {
		LOG.fine("Trying to get chat from event: " + data.event_id);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			Triplet<List<MessageDataReturn>, Integer, MoreResultsType> messages = DB_Event.getChat(data.event_id, data.cursor,
					data.latest_first, data.email);
			return Response
					.ok(JsonUtil.json
							.toJson(new ChatReturn(messages.getValue0(), messages.getValue1(), messages.getValue2())))
					.build();

		} catch (InvalidTokenException | InexistentChatIdException | InvalidCursorException | InexistentLogIdException
				| InexistentParticipantException | InexistentEventException e) {
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
		LOG.fine("Trying to make chat moderator in event: " + data.event_id);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();

		try {
			Entity token = TokensResource.checkIsValidAccess(data.token, data.email);

			String req_email = token.getString(TokensResource.ACCESS_EMAIL);

			Entity event = DB_Event.makeChatModerator(data.event_id, req_email, data.mod);

			txn.put(event);
			txn.commit();

			LOG.fine("Added " + data.email + "to event chat moderators : " + data.event_id);
			return Response.status(Status.NO_CONTENT).build();

		} catch (InvalidTokenException | ImpossibleActionException | InexistentChatIdException
				| InexistentParticipantException | InexistentEventException e) {
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

			Entity event = DB_Event.removeChatModerator(data.event_id, req_email, data.mod);

			txn.put(event);
			txn.commit();

			LOG.fine("Removed " + data.mod + "from event chat moderators: " + data.event_id);
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
	@Path("/moderator/getall")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getChatModerators(EventData data) {
		LOG.fine("Trying to get event participants: " + data.event_id);

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
