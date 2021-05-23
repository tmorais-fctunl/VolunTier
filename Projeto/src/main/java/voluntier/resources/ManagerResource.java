package voluntier.resources;

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

import com.google.gson.Gson;

import voluntier.util.consumes.RequestData;
import voluntier.util.userdata.DB_User;
import voluntier.util.userdata.UserData_Minimal;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ManagerResource {
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());

	private final Gson g = new Gson();

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory usersFactory = datastore.newKeyFactory().setKind("User");
	private static KeyFactory sessionFactory = datastore.newKeyFactory().setKind("Session");

	public ManagerResource() { }

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response doLookUp(RequestData data) {

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).entity("Invalid").build();

		Transaction txn = datastore.newTransaction();
		try {
			Key tokenKey = sessionFactory.newKey(data.token);
			Entity token = txn.get(tokenKey);

			if (!TokensResource.isValidAccess(token)) {
				txn.rollback();
				LOG.warning("Failed removal of user: " + data.user_id);
				return Response.status(Status.FORBIDDEN).build();
			} else {
				Key tg_userKey = usersFactory.newKey(data.user_id);
				Entity tg_user = txn.get(tg_userKey);

				Key rq_userKey = usersFactory.newKey(token.getString("token_user_id"));
				Entity rq_user = txn.get(rq_userKey);

				if (tg_user == null) {
					txn.rollback();
					return Response.status(Status.FORBIDDEN).entity("").build();
				} else {
					// check permissions
					if (!ActionsResource.hasLookUpPermission(rq_user, tg_user, txn)) {
						txn.rollback();
						LOG.warning("User:" + rq_user.getString(DB_User.ID)
								+ " does not have enough permissions to look up: " + data.user_id);
						return Response.status(Status.FORBIDDEN).build();
					} else {
						UserData_Minimal user_data = new UserData_Minimal(tg_user);
						txn.rollback();
						return Response.ok(g.toJson(user_data)).build();
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
}
