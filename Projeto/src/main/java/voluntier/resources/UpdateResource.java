package voluntier.resources;

import java.net.URL;
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

import voluntier.exceptions.InvalidTokenException;
import voluntier.util.Argon2Util;
import voluntier.util.DB_Variables;
import voluntier.util.GoogleStorageUtil;
import voluntier.util.JsonUtil;
import voluntier.util.consumes.generic.AppPropertiesData;
import voluntier.util.consumes.generic.UploadImageData;
import voluntier.util.consumes.user.RemoveUserData;
import voluntier.util.consumes.user.UpdateProfileData;
import voluntier.util.consumes.user.UpdateRoleData;
import voluntier.util.consumes.user.UpdateStateData;
import voluntier.util.data.user.DB_User;
import voluntier.util.data.user.ProfilePicture;
import voluntier.util.data.user.State;
import voluntier.util.produces.pictures.UploadSignedURLReturn;

@Path("/update")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class UpdateResource {

	private static final Logger LOG = Logger.getLogger(UpdateResource.class.getName());

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory usersFactory = datastore.newKeyFactory().setKind("User");

	public UpdateResource() {
	}

	@POST
	@Path("/remove")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doRemoval(RemoveUserData data) {

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();
		try {
			TokensResource.checkIsValidAccess(data.token, data.email);
			// user being removed (attempted)
			Key tg_userKey = usersFactory.newKey(data.target);
			Entity tg_user = txn.get(tg_userKey);

			// user requesting removal
			Key rq_userKey = usersFactory.newKey(data.email);
			Entity rq_user = txn.get(rq_userKey);

			// check if user being removed exists and is not already removed
			if (tg_user == null) {
				txn.rollback();
				LOG.warning("User:" + rq_user.getString(DB_User.EMAIL)
						+ " is trying to remove an inexistent or already removed user: " + data.target);
				return Response.status(Status.FORBIDDEN).build();
			} else {
				// check permissions
				if (!ActionsResource.hasRemovePermission(rq_user, tg_user, txn)) {
					txn.rollback();
					LOG.warning("User:" + rq_user.getString(DB_User.EMAIL)
							+ " does not have enough permissions to remove: " + data.target);
					return Response.status(Status.FORBIDDEN).build();
				} else {
					// set removed flag for user
					tg_user = DB_User.remove(tg_userKey, tg_user);

					// invalidate all sessions related to this user
					SessionResource.invalidateAllSessionsOfUser(data.target, txn);

					txn.put(tg_user);
					txn.commit();

					LOG.fine("User: " + rq_user.getString(DB_User.EMAIL) + " removed User: " + data.target);
					return Response.status(Status.NO_CONTENT).build();
				}
			}

		} catch (InvalidTokenException e) {
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
	@Path("/state")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doUpdateState(UpdateStateData data) {

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();
		try {
			TokensResource.checkIsValidAccess(data.token, data.email);
			// target user
			Key tg_userKey = usersFactory.newKey(data.target);
			Entity tg_user = txn.get(tg_userKey);

			// user requesting change
			Key rq_userKey = usersFactory.newKey(data.email);
			Entity rq_user = txn.get(rq_userKey);

			// check if target user exists and is not removed
			if (tg_user == null) {
				txn.rollback();
				LOG.warning("User:" + rq_user.getString(DB_User.EMAIL)
						+ " is trying to change state of an inexistent or already removed user: " + data.target);
				return Response.status(Status.FORBIDDEN).build();
			} else {
				// check permissions
				if (!ActionsResource.hasStatePermission(rq_user, tg_user, txn)) {
					txn.rollback();
					LOG.warning("User:" + rq_user.getString(DB_User.EMAIL)
							+ " does not have enough permissions to change state of user: " + data.target);
					return Response.status(Status.FORBIDDEN).build();
				} else {
					// set state flag for target user
					tg_user = DB_User.setState(data.state, tg_userKey, tg_user);

					if (data.state.equals(State.BANNED.toString()))
						SessionResource.invalidateAllSessionsOfUser(data.target, txn);

					txn.put(tg_user);
					txn.commit();

					LOG.fine("User: " + rq_user.getString(DB_User.EMAIL) + " changed state of User: " + data.target
							+ " to: " + data.state);
					return Response.status(Status.NO_CONTENT).build();
				}
			}

		} catch (InvalidTokenException e) {
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
	public Response doUpdateProfile(UpdateProfileData data) {

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();
		try {
			TokensResource.checkIsValidAccess(data.token, data.email);
			// target user
			Key tg_userKey = usersFactory.newKey(data.target);
			Entity tg_user = txn.get(tg_userKey);

			// user requesting update
			Key rq_userKey = usersFactory.newKey(data.email);
			Entity rq_user = txn.get(rq_userKey);

			// check if target user exists and is not removed
			if (tg_user == null) {
				txn.rollback();
				LOG.warning("User:" + rq_user.getString(DB_User.EMAIL)
						+ " is trying to change profile attributes of an inexistent or already removed user: "
						+ data.target);
				return Response.status(Status.FORBIDDEN).build();
			} else {
				// check permissions
				if (!ActionsResource.hasAtribPermission(rq_user, tg_user, txn)) {
					txn.rollback();
					LOG.warning("User:" + rq_user.getString(DB_User.EMAIL)
							+ " does not have enough permissions to change profile of user: " + data.target);
					return Response.status(Status.FORBIDDEN).build();
				} else {

					if (data.password != null
							&& !Argon2Util.verify(tg_user.getString(DB_User.PASSWORD), data.old_password)) {
						txn.rollback();
						return Response.status(Status.NOT_ACCEPTABLE).entity("Current password is not correct.")
								.build();
					}

					if (data.password != null)
						SessionResource.invalidateAllSessionsOfUser(data.target, txn, data.token);

					// set state flag for target user
					tg_user = DB_User.changeProperty(data, tg_userKey, tg_user);

					txn.put(tg_user);
					txn.commit();

					LOG.fine("User: " + rq_user.getString(DB_User.EMAIL) + " changed profile of User: " + data.target);
					return Response.status(Status.NO_CONTENT).build();
				}
			}

		} catch (InvalidTokenException e) {
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
	@Path("/role")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doUpdateRole(UpdateRoleData data) {

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();
		try {
			TokensResource.checkIsValidAccess(data.token, data.email);
			// target user
			Key tg_userKey = usersFactory.newKey(data.target);
			Entity tg_user = txn.get(tg_userKey);

			// user requesting change
			Key rq_userKey = usersFactory.newKey(data.email);
			Entity rq_user = txn.get(rq_userKey);

			// check if target user exists and is not removed
			if (tg_user == null) {
				txn.rollback();
				LOG.warning("User:" + rq_user.getString(DB_User.EMAIL)
						+ " is trying to change role of an inexistent or already removed user: " + data.target);
				return Response.status(Status.FORBIDDEN).entity("No user with email " + data.target).build();
			} else {
				// check permissions
				if (!ActionsResource.hasRolePermission(rq_user, tg_user, txn, data.role)) {
					txn.rollback();
					LOG.warning("User:" + rq_user.getString(DB_User.EMAIL)
							+ " does not have enough permissions to change role of user: " + data.target);
					return Response.status(Status.FORBIDDEN).entity("Not enough permissions .").build();
				} else {
					// set state flag for target user
					tg_user = DB_User.changeRole(data.role, tg_userKey, tg_user);

					txn.put(tg_user);
					txn.commit();

					LOG.fine("User: " + rq_user.getString(DB_User.EMAIL) + " changed role of User: " + data.target
							+ " to: " + data.role);
					return Response.status(Status.NO_CONTENT).build();
				}
			}

		} catch (InvalidTokenException e) {
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
	@Path("/picture")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response doUpdateProfilePicture(UploadImageData data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).entity("Invalid").build();

		Transaction txn = datastore.newTransaction();
		try {
			TokensResource.checkIsValidAccess(txn, data.token, data.email);

			Key userKey = usersFactory.newKey(data.email);
			Entity user = txn.get(userKey);
			String username = user.getString(DB_User.USERNAME);

			user = DB_User.changeProfilePicture(data.data, userKey, user);

			txn.put(user);
			txn.commit();

			String ext = ProfilePicture.getImageType(data.data);
			URL signedURL = GoogleStorageUtil.signURLForUpload(DB_User.getProfilePictureFilename(username, ext));

			return Response.ok(JsonUtil.json.toJson(new UploadSignedURLReturn(signedURL))).build();

		} catch (InvalidTokenException e) {
			txn.rollback();
			LOG.severe(e.getMessage());
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
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
	@Path("/applicationVariables")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateAppVariables(AppPropertiesData data) {
		LOG.fine("Trying to change property by user: " + data.email);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);
			
			Key userKey = usersFactory.newKey(data.email);
			Entity user = datastore.get(userKey);
			
			if (!ActionsResource.hasGAPermission(user))
				return Response.status(Status.FORBIDDEN).entity("User has not enough permissions to change app properties").build();
			
			if (!DB_Variables.changeVariables(data.variable, data.variableValue))
				return Response.status(Status.CONFLICT).build();

			return Response.status(Status.NO_CONTENT).build();
		}
		catch (InvalidTokenException e) {
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();
		}
	}
	
}
