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

import voluntier.util.Argon2Util;
import voluntier.util.GoogleStorageUtil;
import voluntier.util.JsonUtil;
import voluntier.util.consumes.RequestData;
import voluntier.util.consumes.UpdateProfileData;
import voluntier.util.consumes.UpdateRoleData;
import voluntier.util.consumes.UpdateStateData;
import voluntier.util.consumes.UploadImageData;
import voluntier.util.produces.UploadSignedURLReturn;
import voluntier.util.userdata.DB_User;
import voluntier.util.userdata.State;

@Path("/update")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class UpdateResource {

	private static final Logger LOG = Logger.getLogger(UpdateResource.class.getName());

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory usersFactory = datastore.newKeyFactory().setKind("User");
	private static KeyFactory sessionFactory = datastore.newKeyFactory().setKind("Session");

	public UpdateResource() {
	}

	@POST
	@Path("/remove")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doRemoval(RequestData data) {

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();
		try {

			Key tokenKey = sessionFactory.newKey(data.token);
			Entity token = txn.get(tokenKey);

			if (!TokensResource.isValidAccess(token)) {
				txn.rollback();
				LOG.warning("Failed removal of user: " + data.email);
				return Response.status(Status.FORBIDDEN).entity("Invalid token").build();
			} else {
				// user being removed (attempted)
				Key tg_userKey = usersFactory.newKey(data.email);
				Entity tg_user = txn.get(tg_userKey);

				// user requesting removal
				Key rq_userKey = usersFactory.newKey(token.getString(TokensResource.ACCESS_EMAIL));
				Entity rq_user = txn.get(rq_userKey);

				// check if user being removed exists and is not already removed
				if (tg_user == null) {
					txn.rollback();
					LOG.warning("User:" + rq_user.getString(DB_User.EMAIL)
							+ " is trying to remove an inexistent or already removed user: " + data.email);
					return Response.status(Status.FORBIDDEN).build();
				} else {
					// check permissions
					if (!ActionsResource.hasRemovePermission(rq_user, tg_user, txn)) {
						txn.rollback();
						LOG.warning("User:" + rq_user.getString(DB_User.EMAIL)
								+ " does not have enough permissions to remove: " + data.email);
						return Response.status(Status.FORBIDDEN).build();
					} else {
						// set removed flag for user
						tg_user = DB_User.remove(tg_userKey, tg_user);

						// invalidate all sessions related to this user
						SessionResource.invalidateAllSessionsOfUser(data.email, txn);

						txn.put(tg_user);
						txn.commit();

						LOG.fine("User: " + rq_user.getString(DB_User.EMAIL) + " removed User: " + data.email);
						return Response.status(Status.NO_CONTENT).build();
					}
				}
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
	@Path("/state")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doUpdateState(UpdateStateData data) {

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();
		try {

			Key tokenKey = sessionFactory.newKey(data.token);
			Entity token = txn.get(tokenKey);

			if (!TokensResource.isValidAccess(token)) {
				txn.rollback();
				LOG.warning("Failed request to change state of user: " + data.email);
				return Response.status(Status.FORBIDDEN).entity("Invalid token").build();
			} else {
				// target user
				Key tg_userKey = usersFactory.newKey(data.email);
				Entity tg_user = txn.get(tg_userKey);

				// user requesting change
				Key rq_userKey = usersFactory.newKey(token.getString(TokensResource.ACCESS_EMAIL));
				Entity rq_user = txn.get(rq_userKey);

				// check if target user exists and is not removed
				if (tg_user == null) {
					txn.rollback();
					LOG.warning("User:" + rq_user.getString(DB_User.EMAIL)
							+ " is trying to change state of an inexistent or already removed user: " + data.email);
					return Response.status(Status.FORBIDDEN).build();
				} else {
					// check permissions
					if (!ActionsResource.hasStatePermission(rq_user, tg_user, txn)) {
						txn.rollback();
						LOG.warning("User:" + rq_user.getString(DB_User.EMAIL)
								+ " does not have enough permissions to change state of user: " + data.email);
						return Response.status(Status.FORBIDDEN).build();
					} else {
						// set state flag for target user
						tg_user = DB_User.setState(data.state, tg_userKey, tg_user);

						if (data.state.equals(State.BANNED.toString()))
							SessionResource.invalidateAllSessionsOfUser(data.email, txn);

						txn.put(tg_user);
						txn.commit();

						LOG.fine("User: " + rq_user.getString(DB_User.EMAIL) + " changed state of User: " + data.email
								+ " to: " + data.state);
						return Response.status(Status.NO_CONTENT).build();
					}
				}
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
	@Path("/profile")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doUpdateProfile(UpdateProfileData data) {

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();
		try {

			Key tokenKey = sessionFactory.newKey(data.token);
			Entity token = txn.get(tokenKey);

			if (!TokensResource.isValidAccess(token)) {
				txn.rollback();
				LOG.warning("Failed request to change state of user: " + data.email);
				return Response.status(Status.FORBIDDEN).entity("Invalid token").build();
			} else {
				// target user
				Key tg_userKey = usersFactory.newKey(data.email);
				Entity tg_user = txn.get(tg_userKey);

				// user requesting update
				Key rq_userKey = usersFactory.newKey(token.getString(TokensResource.ACCESS_EMAIL));
				Entity rq_user = txn.get(rq_userKey);

				// check if target user exists and is not removed
				if (tg_user == null) {
					txn.rollback();
					LOG.warning("User:" + rq_user.getString(DB_User.EMAIL)
							+ " is trying to change profile attributes of an inexistent or already removed user: "
							+ data.email);
					return Response.status(Status.FORBIDDEN).build();
				} else {
					// check permissions
					if (!ActionsResource.hasAtribPermission(rq_user, tg_user, txn)) {
						txn.rollback();
						LOG.warning("User:" + rq_user.getString(DB_User.EMAIL)
								+ " does not have enough permissions to change profile of user: " + data.email);
						return Response.status(Status.FORBIDDEN).build();
					} else {

						if (data.password != null
								&& !Argon2Util.verify(tg_user.getString(DB_User.PASSWORD), data.old_password)) {
							txn.rollback();
							return Response.status(Status.NOT_ACCEPTABLE).entity("Current password is not correct.")
									.build();
						}

						if (data.password != null)
							SessionResource.invalidateAllSessionsOfUser(data.email, txn, data.token);

						// set state flag for target user
						tg_user = DB_User.changeProperty(data, tg_userKey, tg_user);

						txn.put(tg_user);
						txn.commit();

						LOG.fine("User: " + rq_user.getString(DB_User.EMAIL) + " changed profile of User: "
								+ data.email);
						return Response.status(Status.NO_CONTENT).build();
					}
				}
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
	@Path("/role")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doUpdateRole(UpdateRoleData data) {

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();
		try {

			Key tokenKey = sessionFactory.newKey(data.token);
			Entity token = txn.get(tokenKey);

			if (!TokensResource.isValidAccess(token)) {
				txn.rollback();
				LOG.warning("Failed request to change state of user: " + data.email);
				return Response.status(Status.FORBIDDEN).entity("Invalid token").build();
			} else {
				// target user
				Key tg_userKey = usersFactory.newKey(data.email);
				Entity tg_user = txn.get(tg_userKey);

				// user requesting change
				Key rq_userKey = usersFactory.newKey(token.getString(TokensResource.ACCESS_EMAIL));
				Entity rq_user = txn.get(rq_userKey);

				// check if target user exists and is not removed
				if (tg_user == null) {
					txn.rollback();
					LOG.warning("User:" + rq_user.getString(DB_User.EMAIL)
							+ " is trying to change role of an inexistent or already removed user: " + data.email);
					return Response.status(Status.FORBIDDEN).entity("No user with email " + data.email).build();
				} else {
					// check permissions
					if (!ActionsResource.hasRolePermission(rq_user, tg_user, txn, data.role)) {
						txn.rollback();
						LOG.warning("User:" + rq_user.getString(DB_User.EMAIL)
								+ " does not have enough permissions to change role of user: " + data.email);
						return Response.status(Status.FORBIDDEN).entity("Not enough permissions .").build();
					} else {
						// set state flag for target user
						tg_user = DB_User.changeRole(data.role, tg_userKey, tg_user);

						txn.put(tg_user);
						txn.commit();

						LOG.fine("User: " + rq_user.getString(DB_User.EMAIL) + " changed role of User: " + data.email
								+ " to: " + data.role);
						return Response.status(Status.NO_CONTENT).build();
					}
				}
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
	@Path("/picture")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response doUpdateProfilePicture(UploadImageData data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).entity("Invalid").build();

		Transaction txn = datastore.newTransaction();
		try {
			if (!TokensResource.isValidAccess(txn, data.token, data.email)) {
				LOG.warning("Failed request to get a signed URL attempt by user: " + data.email);
				return Response.status(Status.FORBIDDEN).entity("Token expired or invalid: " + data.email).build();
			}
			
			Key userKey = usersFactory.newKey(data.email);
			Entity user = txn.get(userKey);
			String username = user.getString(DB_User.USERNAME);
			
			user = DB_User.changeProfilePicture(data.data, userKey, user);
			
			txn.put(user);
			txn.commit();

			URL signedURL = GoogleStorageUtil.signURLForUpload(DB_User.getProfilePictureFilename(username));

			return Response.ok(JsonUtil.json.toJson(new UploadSignedURLReturn(signedURL))).build();

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
}
