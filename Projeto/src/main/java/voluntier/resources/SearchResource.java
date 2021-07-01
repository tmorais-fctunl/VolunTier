package voluntier.resources;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.StructuredQuery.Builder;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.Transaction;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.datastore.v1.QueryResultBatch;
import com.google.datastore.v1.QueryResultBatch.MoreResultsType;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.Filter;
import com.google.gson.Gson;

import voluntier.util.GoogleStorageUtil;
import voluntier.util.consumes.RequestData;
import voluntier.util.consumes.SearchUserData;
import voluntier.util.produces.GetPictureReturn;
import voluntier.util.produces.SearchData;
import voluntier.util.userdata.DB_User;
import voluntier.util.userdata.UserData_Minimal;

@Path("/")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class SearchResource {
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());

	private static final int SEARCH_RESULTS_LIMIT = 2;

	private final Gson json = new Gson();

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory usersFactory = datastore.newKeyFactory().setKind("User");
	private static KeyFactory sessionFactory = datastore.newKeyFactory().setKind("Session");
	private static KeyFactory usernamesFactory = datastore.newKeyFactory().setKind("ID");

	public SearchResource() {
	}

	@POST
	@Path("/user")
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
				LOG.warning("Invalid token");
				return Response.status(Status.FORBIDDEN).build();
			} else {
				Key tg_userKey = usersFactory.newKey(data.email);
				Entity tg_user = txn.get(tg_userKey);

				Key rq_userKey = usersFactory.newKey(token.getString(TokensResource.ACCESS_EMAIL));
				Entity rq_user = txn.get(rq_userKey);

				if (tg_user == null) {
					txn.rollback();
					return Response.status(Status.FORBIDDEN).build();
				} else {
					// check permissions
					if (!ActionsResource.hasLookUpPermission(rq_user, tg_user, txn)) {
						txn.rollback();
						LOG.warning("User:" + rq_user.getString(DB_User.EMAIL)
								+ " does not have enough permissions to look up: " + data.email);
						return Response.status(Status.FORBIDDEN).build();
					} else {
						UserData_Minimal user_data = new UserData_Minimal(tg_user);
						txn.rollback();
						return Response.ok(json.toJson(user_data)).build();
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
	@Path("/search")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response doSearch(@QueryParam("q") String query, SearchUserData data) {

		if (!data.isValid() || query == null)
			return Response.status(Status.BAD_REQUEST).entity("Invalid").build();

		try {
			// check if the token corresponds to the user received and hasnt expired yet
			if (!TokensResource.isValidAccess(data.token, data.email)) {
				LOG.warning("Failed search attempt by user: " + data.email);
				return Response.status(Status.FORBIDDEN).entity("Token expired or invalid: " + data.email).build();
			}

			SearchData res = new SearchData(searchUser(query, data.cursor));

			return Response.ok(json.toJson(res)).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path("/picture/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response doGetPicture(@PathParam("username") String username, RequestData data) {

		if (!data.isValid() || !UserData_Minimal.usernameValid(username))
			return Response.status(Status.BAD_REQUEST).entity("Invalid").build();

		try {
			if (!TokensResource.isValidAccess(data.token, data.email)) {
				LOG.warning("Failed logout attempt by user: " + data.email);
				return Response.status(Status.FORBIDDEN).entity("Token expired or invalid: " + data.email).build();
			}

			Key usernameKey = usernamesFactory.newKey(username);
			Entity usernameEnt = datastore.get(usernameKey);

			if (usernameEnt != null) {
				Key emailKey = usersFactory.newKey(usernameEnt.getString(DB_User.EMAIL));
				Entity emailEnt = datastore.get(emailKey);
				String encodedMiniature = emailEnt.getString(DB_User.PROFILE_PICTURE_MINIATURE);
				String GCS_filename = DB_User.getProfilePictureFilename(username);
				Pair<URL, Long> downloadData = GoogleStorageUtil.signURLForDownload(GCS_filename);

				return Response.ok(json.toJson(new GetPictureReturn(downloadData.getValue0(), downloadData.getValue1(),
						encodedMiniature.equals("") ? null : encodedMiniature))).build();
			}

			return Response.status(Status.NOT_FOUND).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	private static List<Entity> removeDuplicates(List<Entity> usernames, List<Entity> full_names) {
		List<Entity> users = usernames;
		full_names.forEach(user -> {
			if (!users.contains(user))
				users.add(user);
		});

		return users;
	}

	public static Triplet<List<Entity>, Cursor[], QueryResultBatch.MoreResultsType> searchUser(String q,
			String[] cursor) {

		Triplet<List<Entity>, Cursor, QueryResultBatch.MoreResultsType> usernames = searchUsername(q,
				cursor != null ? cursor[0] : null);
		Triplet<List<Entity>, Cursor, QueryResultBatch.MoreResultsType> full_names = searchFullName(q,
				cursor != null ? cursor[1] : null);

		List<Entity> users = removeDuplicates(usernames.getValue0(), full_names.getValue0());

		if (users.size() < SEARCH_RESULTS_LIMIT || (usernames.getValue2() == MoreResultsType.NO_MORE_RESULTS
				&& full_names.getValue2() == MoreResultsType.NO_MORE_RESULTS))
			return new Triplet<List<Entity>, Cursor[], QueryResultBatch.MoreResultsType>(users,
					new Cursor[] { usernames.getValue1(), full_names.getValue1() }, MoreResultsType.NO_MORE_RESULTS);

		return new Triplet<List<Entity>, Cursor[], QueryResultBatch.MoreResultsType>(users,
				new Cursor[] { usernames.getValue1(), full_names.getValue1() },
				MoreResultsType.MORE_RESULTS_AFTER_LIMIT);
	}

	public static Triplet<List<Entity>, Cursor, QueryResultBatch.MoreResultsType> searchUsername(String q,
			String cursor) {
		return runQuery(CompositeFilter.and(PropertyFilter.ge(DB_User.USERNAME, q),
				PropertyFilter.lt(DB_User.USERNAME, q + "z")), cursor);
	}

	public static Triplet<List<Entity>, Cursor, QueryResultBatch.MoreResultsType> searchFullName(String q,
			String cursor) {
		return runQuery(CompositeFilter.and(PropertyFilter.ge(DB_User.FULL_NAME, q),
				PropertyFilter.lt(DB_User.FULL_NAME, q + "z")), cursor);
	}

	public static Triplet<List<Entity>, Cursor, QueryResultBatch.MoreResultsType> runQuery(Filter filter,
			String cursor) {
		Builder<Entity> b = Query.newEntityQueryBuilder().setKind("User").setFilter(filter)
				.setLimit(SEARCH_RESULTS_LIMIT);

		if (cursor != null)
			b.setStartCursor(Cursor.fromUrlSafe(cursor));

		Query<Entity> query = b.build();

		QueryResults<Entity> res = datastore.run(query);

		List<Entity> users = new LinkedList<>();
		res.forEachRemaining(user -> {
			users.add(user);
		});

		return new Triplet<List<Entity>, Cursor, QueryResultBatch.MoreResultsType>(users, res.getCursorAfter(),
				res.getMoreResults());
	}
}
