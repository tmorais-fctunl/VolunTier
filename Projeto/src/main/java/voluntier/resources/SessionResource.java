package voluntier.resources;

import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.javatuples.Triplet;

import voluntier.exceptions.InvalidTokenException;
import voluntier.util.Argon2Util;
import voluntier.util.AuthToken;
import voluntier.util.JsonUtil;
import voluntier.util.consumes.LoginData;
import voluntier.util.consumes.RequestData;
import voluntier.util.produces.LoginReturn;
import voluntier.util.userdata.DB_User;

import com.google.cloud.datastore.*;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

@Path("/")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class SessionResource {
	private static final Logger LOG = Logger.getLogger(SessionResource.class.getName());

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory usersFactory = datastore.newKeyFactory().setKind("User");
	private static KeyFactory usernamesFactory = datastore.newKeyFactory().setKind("ID");
	private static KeyFactory refreshFactory = datastore.newKeyFactory().setKind("RefreshSession");

	public SessionResource() {
	}

	@POST
	@Path("/validate")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doValidation(RequestData data) {

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);
			return Response.status(Status.NO_CONTENT).build();

		} catch (InvalidTokenException e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.UNAUTHORIZED).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();

		}
	}

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response doLogin(LoginData data) throws MessagingException {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();
		try {
			Key userKey = usersFactory.newKey(data.user);
			Entity user = txn.get(userKey);
			
			String email = null;

			// check if user exists
			if (user == null) {
				Key usernameKey = usernamesFactory.newKey(data.user);
				Entity username = txn.get(usernameKey);
				if(username != null) {
					email = username.getString(DB_User.EMAIL);
					userKey = usersFactory.newKey(email);
					user = txn.get(userKey);
				}
			} else 
				email = data.user;
			
			if (user == null || ActionsResource.isRemovedOrBannedUser(user)) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).build();
			}
			
			String hsh_pwd = user.getString(DB_User.PASSWORD);

			// check for correct password
			if (!Argon2Util.verify(hsh_pwd, data.password)) {
				txn.rollback();
				LOG.warning("Failed login attempt by user: " + data.user);
				return Response.status(Status.FORBIDDEN).build();
			} else {
				// create a new refresh and access token
				Triplet<Entity, Entity, AuthToken> tokens = TokensResource.createNewAccessAndRefreshTokens(email);

				txn.put(tokens.getValue0(), tokens.getValue1());
				txn.commit();

				LOG.fine("Login by user: " + data.user);
				return Response
						.ok(JsonUtil.json.toJson(new LoginReturn(tokens.getValue2(), user.getString(DB_User.USERNAME))))
						.build();
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
	@Path("/logout")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLogout(RequestData data) {

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();
		try {
			Entity token = TokensResource.checkIsValidAccess(data.token, data.email);

			invalidateSession(token, txn);
			txn.commit();

			LOG.fine("Logout by user: " + data.email);
			return Response.status(Status.NO_CONTENT).build();

		} catch (InvalidTokenException e) {
			txn.rollback();
			LOG.severe(e.getMessage());
			return Response.status(Status.FORBIDDEN).build();

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
	@Path("/refresh")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response doRefresh(RequestData data) {
		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();
		try {
			Entity old_refresh = TokensResource.checkIsValidRefresh(data.token, data.email);

			// invalidate old refresh token
			old_refresh = TokensResource.invalidateRefreshToken(old_refresh, old_refresh.getKey());

			// create a new refresh and access token
			Triplet<Entity, Entity, AuthToken> tokens = TokensResource.createNewAccessAndRefreshTokens(data.email);

			txn.put(tokens.getValue0(), tokens.getValue1(), old_refresh);
			txn.commit();

			LOG.fine("Refreshed session by user: " + data.email);
			return Response.ok(JsonUtil.json.toJson(tokens.getValue2())).build();
		} catch (InvalidTokenException e) {
			txn.rollback();
			LOG.severe(e.getMessage());
			return Response.status(Status.FORBIDDEN).build();

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

	public static void invalidateAllSessionsOfUser(String user_email, Transaction txn) {
		Query<Entity> query = Query.newEntityQueryBuilder().setKind("Session")
				.setFilter(PropertyFilter.eq(TokensResource.ACCESS_EMAIL, user_email)).build();

		QueryResults<Entity> res = datastore.run(query);

		res.forEachRemaining(accessToken -> {
			invalidateSession(accessToken, txn);
		});
	}

	public static void invalidateAllSessionsOfUser(String user_email, Transaction txn, String except) {
		Query<Entity> query = Query.newEntityQueryBuilder().setKind("Session")
				.setFilter(PropertyFilter.eq(TokensResource.ACCESS_EMAIL, user_email)).build();

		QueryResults<Entity> res = datastore.run(query);

		res.forEachRemaining(accessToken -> {
			if (!accessToken.getString(TokensResource.ACCESS_TOKEN).equals(except))
				invalidateSession(accessToken, txn);
		});
	}

	public static void invalidateSession(Entity token, Transaction txn) {
		Key refreshKey = refreshFactory.newKey(token.getString(TokensResource.REFESH_TOKEN));
		Entity refresh = txn.get(refreshKey);
		refresh = TokensResource.invalidateRefreshToken(refresh, refreshKey);

		token = TokensResource.invalidateAccessToken(token, token.getKey());

		txn.put(token, refresh);
	}
}
