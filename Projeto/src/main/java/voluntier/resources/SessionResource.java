package voluntier.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.javatuples.Triplet;

import com.google.gson.Gson;

import voluntier.util.Argon2Util;
import voluntier.util.AuthToken;
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

	private final Gson g = new Gson();

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory usersFactory = datastore.newKeyFactory().setKind("User");
	private static KeyFactory sessionFactory = datastore.newKeyFactory().setKind("Session");
	private static KeyFactory refreshFactory = datastore.newKeyFactory().setKind("RefreshSession");

	public SessionResource() {}
	
	@POST
	@Path("/validate")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doValidation(RequestData data) {

		if(!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			// check if the token corresponds to the user received and hasnt expired yet
			if(!TokensResource.isValidAccess(data.token, data.email)) {
				LOG.warning("Failed validate attempt by user: " + data.email);
				return Response.status(Status.UNAUTHORIZED).build();
			}
			
			return Response.status(Status.NO_CONTENT).build();

		} catch(Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();

		}
	}

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response doLogin(LoginData data) {

		if(!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();
		try {
			Key userKey = usersFactory.newKey(data.email);
			Entity user = txn.get(userKey);

			// check if user exists
			if(user == null || ActionsResource.isRemovedOrBannedUser(user)) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).build();
			} else {
				String hsh_pwd = user.getString(DB_User.PASSWORD);

				// check for correct password
				if(!Argon2Util.verify(hsh_pwd, data.password)) {
					txn.rollback();
					LOG.warning("Failed login attempt by user: " + data.email);
					return Response.status(Status.FORBIDDEN).build();
				} else {
					// create a new refresh and access token
					Triplet<Entity, Entity, AuthToken> tokens = TokensResource.createNewAccessAndRefreshTokens(data.email);

					txn.put(tokens.getValue0(), tokens.getValue1());
					txn.commit();

					LOG.fine("Login by user: " + data.email);
					return Response.ok(g.toJson(new LoginReturn(tokens.getValue2(), user.getString(DB_User.USERNAME)))).build();
				}
			}

		} catch(Exception e) {
			txn.rollback();
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();

		} finally {
			if(txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
	}

	@POST
	@Path("/logout")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLogout(RequestData data) {

		if(!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();
		try {

			Key tokenKey = sessionFactory.newKey(data.token);
			Entity token = txn.get(tokenKey);

			// check if the token corresponds to the user received and hasnt expired yet
			if(!TokensResource.isValidAccess(token, data.email)) {
				txn.rollback();
				LOG.warning("Failed logout attempt by user: " + data.email);
				return Response.status(Status.FORBIDDEN).entity("Token expired or invalid: " + data.email).build();
			}

			invalidateSession(token, txn);
			txn.commit();
			
			LOG.fine("Logout by user: " + data.email);
			return Response.status(Status.NO_CONTENT).build();

		} catch(Exception e) {
			txn.rollback();
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();

		} finally {
			if(txn.isActive()) {
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
		if(!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		Transaction txn = datastore.newTransaction();
		try {

			// here the data.token is the refresh token
			Key old_refreshKey = refreshFactory.newKey(data.token);
			Entity old_refresh = txn.get(old_refreshKey);

			// check if the token corresponds to the user received and hasnt expired yet
			if(!TokensResource.isValidRefresh(old_refresh, data.email)) {
				txn.rollback();
				LOG.warning("Failed refreshing session for user: " + data.email);
				return Response.status(Status.FORBIDDEN).entity("Token expired or invalid: " + data.email).build();
			}

			// invalidate old refresh token
			old_refresh = TokensResource.invalidateRefreshToken(old_refresh, old_refreshKey);
			
			
			// create a new refresh and access token
			Triplet<Entity, Entity, AuthToken> tokens = TokensResource.createNewAccessAndRefreshTokens(data.email);

			txn.put(tokens.getValue0(), tokens.getValue1(), old_refresh);
			txn.commit();

			LOG.fine("Refreshed session by user: " + data.email);
			return Response.ok(g.toJson(tokens.getValue2())).build();
		} catch(Exception e) {
			txn.rollback();
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();

		} finally {
			if(txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
	}

	public static void invalidateAllSessionsOfUser(String user_email, Transaction txn) {
		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind("Session")
				.setFilter(PropertyFilter.eq(TokensResource.ACCESS_EMAIL, user_email))
				.build();

		QueryResults<Entity> res = datastore.run(query);

		res.forEachRemaining(accessToken -> {
			invalidateSession(accessToken, txn);
		});
	}
	
	public static void invalidateAllSessionsOfUser(String user_email, Transaction txn, String except) {
		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind("Session")
				.setFilter(PropertyFilter.eq(TokensResource.ACCESS_EMAIL, user_email))
				.build();

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
