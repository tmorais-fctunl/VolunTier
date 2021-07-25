package voluntier.resources;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import com.google.appengine.repackaged.com.google.common.collect.Iterators;
import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.Builder;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.Filter;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.datastore.v1.QueryResultBatch;
import com.google.datastore.v1.QueryResultBatch.MoreResultsType;

import voluntier.exceptions.InexistentUserException;
import voluntier.exceptions.InvalidTokenException;
import voluntier.util.JsonUtil;
import voluntier.util.consumes.generic.CursorData;
import voluntier.util.data.event.DB_Event;
import voluntier.util.data.event.RankingType;
import voluntier.util.data.user.DB_User;
import voluntier.util.produces.event.SearchEventData;
import voluntier.util.produces.generic.RankingData;

@Path("/")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RankingResource {

	private static final int SEARCH_RESULTS_LIMIT = 5;

	private static final Logger LOG = Logger.getLogger(EventResource.class.getName());

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public RankingResource() {
	}

	@POST
	@Path("/totalCurrencyRank")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response currencyRank(CursorData data) {
		LOG.fine("Attempt to get ranking by: " + data.email);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {

			TokensResource.checkIsValidAccess(data.token, data.email);

			Triplet<List<Entity>, Cursor, QueryResultBatch.MoreResultsType> users_page = rankingTotalPoints(
					data.cursor);

			RankingData.UserSearchData current_user_data = null;
			if (data.cursor == null) {
				Pair<Entity, Integer> current_user_rank = getCurrencyRank(data.email);

				current_user_data = new RankingData.UserSearchData(current_user_rank.getValue0(),
						(int) current_user_rank.getValue0().getDouble(DB_User.TOTAL_CURRENCY),
						current_user_rank.getValue1());
			}

			RankingData ranking = new RankingData(users_page, RankingType.TOTAL_CURRENCY.toString(), current_user_data);

			LOG.fine("Ranking obtained by user : " + data.email);
			return Response.ok(JsonUtil.json.toJson(ranking)).build();

		} catch (InvalidTokenException | InexistentUserException e) {
			LOG.warning(e.getMessage());
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path("/presencesRank")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response eventsParticipatedRank(CursorData data) {
		LOG.fine("Attempt to get ranking by: " + data.email);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {

			TokensResource.checkIsValidAccess(data.token, data.email);

			Triplet<List<Entity>, Cursor, QueryResultBatch.MoreResultsType> users_page = rankingEventsParticipated(
					data.cursor);

			RankingData.UserSearchData current_user_data = null;
			if (data.cursor == null) {
				Pair<Entity, Integer> current_user_rank = getEventsRank(data.email);

				current_user_data = new RankingData.UserSearchData(current_user_rank.getValue0(),
						(int) current_user_rank.getValue0().getLong(DB_User.N_EVENTS_PARTICIPATED),
						current_user_rank.getValue1());
			}

			RankingData ranking = new RankingData(users_page, RankingType.N_EVENTS.toString(), current_user_data);

			LOG.fine("Ranking obtained by user : " + data.email);
			return Response.ok(JsonUtil.json.toJson(ranking)).build();

		} catch (InvalidTokenException | InexistentUserException e) {
			LOG.warning(e.getMessage());
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	@POST
	@Path("/event/searchByCategory")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchEventsByCategory (@QueryParam("c") String category, CursorData data) {
		LOG.fine("Attempt to get events by category from user: " + data.email);

		if (!data.isValid() || category == null)
			return Response.status(Status.BAD_REQUEST).build();

		LOG.warning(category);

		try {

			TokensResource.checkIsValidAccess(data.token, data.email);

			SearchEventData events = new SearchEventData( getEventsByCategory(data.cursor, category) );

			LOG.fine("Events by category obtained by user : " + data.email);
			return Response.ok(JsonUtil.json.toJson(events)).build();

		} catch (InvalidTokenException e) {
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}


	public static Triplet<List<Entity>, Cursor, QueryResultBatch.MoreResultsType> rankingTotalPoints(String cursor) {

		Builder<Entity> builder = Query.newEntityQueryBuilder().setKind("User")
				.setOrderBy(OrderBy.desc(DB_User.TOTAL_CURRENCY), OrderBy.asc(DB_User.USERNAME)).setLimit(SEARCH_RESULTS_LIMIT);

		return rankingQuery(builder, cursor);
	}

	public static Triplet<List<Entity>, Cursor, QueryResultBatch.MoreResultsType> rankingEventsParticipated(
			String cursor) {

		Builder<Entity> builder = Query.newEntityQueryBuilder().setKind("User")
				.setOrderBy(OrderBy.desc(DB_User.N_EVENTS_PARTICIPATED), OrderBy.asc(DB_User.USERNAME)).setLimit(SEARCH_RESULTS_LIMIT);

		return rankingQuery(builder, cursor);
	}


	public static Triplet<List<Entity>, Cursor, QueryResultBatch.MoreResultsType> getEventsByCategory (String cursor, String category){

		Builder<Entity> builder = Query.newEntityQueryBuilder().setKind("Event")
				.setFilter(PropertyFilter.eq(DB_Event.CATEGORY, category))
				.setLimit(SEARCH_RESULTS_LIMIT);

		return rankingQuery (builder, cursor);
	}

	public static Triplet<List<Entity>, Cursor, QueryResultBatch.MoreResultsType> rankingQuery(Builder<Entity> builder,
			String cursor) {

		if (cursor != null)
			builder.setStartCursor(Cursor.fromUrlSafe(cursor));

		QueryResults<Entity> results = datastore.run(builder.build());

		List<Entity> ranking = new LinkedList<>();
		results.forEachRemaining(rank -> {
			ranking.add(rank);
		});

		if (ranking.size() < SEARCH_RESULTS_LIMIT || (results.getMoreResults() == MoreResultsType.NO_MORE_RESULTS))
			return new Triplet<List<Entity>, Cursor, QueryResultBatch.MoreResultsType>(ranking,
					results.getCursorAfter(), MoreResultsType.NO_MORE_RESULTS);

		return new Triplet<List<Entity>, Cursor, QueryResultBatch.MoreResultsType>(ranking, results.getCursorAfter(),
				MoreResultsType.MORE_RESULTS_AFTER_LIMIT);
	}

	public static Pair<Entity, Integer> getCurrencyRank(String user_email) throws InexistentUserException {

		Entity user = DB_User.getUser(user_email);
		int rank = getRankQuery(PropertyFilter.ge(DB_User.TOTAL_CURRENCY, user.getDouble(DB_User.TOTAL_CURRENCY)));
		int same_rank_string_order = getSameRankQuery(
				PropertyFilter.eq(DB_User.TOTAL_CURRENCY, user.getDouble(DB_User.TOTAL_CURRENCY)), user);
		return new Pair<>(user, rank - same_rank_string_order);
	}

	public static Pair<Entity, Integer> getEventsRank(String user_email) throws InexistentUserException {

		Entity user = DB_User.getUser(user_email);

		int rank = getRankQuery(
				PropertyFilter.ge(DB_User.N_EVENTS_PARTICIPATED, user.getLong(DB_User.N_EVENTS_PARTICIPATED)));
		int same_rank_string_order = getSameRankQuery(
				PropertyFilter.eq(DB_User.N_EVENTS_PARTICIPATED, user.getLong(DB_User.N_EVENTS_PARTICIPATED)), user);
		return new Pair<>(user, rank - same_rank_string_order);
	}

	public static int getRankQuery(Filter filter) {
		Builder<Key> builder = Query.newKeyQueryBuilder().setKind("User").setFilter(CompositeFilter.and(filter));

		QueryResults<Key> results = datastore.run(builder.build());

		int size = Iterators.size(results);

		return size;
	}

	public static int getSameRankQuery(Filter filter, Entity user) {
		Builder<Key> builder = Query.newKeyQueryBuilder().setKind("User").setFilter(
				CompositeFilter.and(filter, PropertyFilter.gt(DB_User.USERNAME, user.getString(DB_User.USERNAME))));

		QueryResults<Key> results = datastore.run(builder.build());

		int size = Iterators.size(results);

		return size;
	}
}
