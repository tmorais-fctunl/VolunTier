package voluntier.resources;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.javatuples.Triplet;

import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.Builder;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.datastore.v1.QueryResultBatch;
import com.google.datastore.v1.QueryResultBatch.MoreResultsType;

import voluntier.exceptions.InvalidTokenException;
import voluntier.util.JsonUtil;
import voluntier.util.consumes.CursorData;
import voluntier.util.produces.RankingData;
import voluntier.util.userdata.DB_User;

@Path("/")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RankingResource {

	private static final int SEARCH_RESULTS_LIMIT = 5;

	private static final Logger LOG = Logger.getLogger(EventResource.class.getName());

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	//private static KeyFactory usersFactory = datastore.newKeyFactory().setKind("User");

	public RankingResource() {
	}

	@POST
	@Path("/totalCurrencyRank")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response allTimeRank (CursorData data) {
		LOG.fine("Attempt to get ranking by: " + data.email);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {

			TokensResource.checkIsValidAccess(data.token, data.email);

			RankingData ranking = new RankingData( rankingTotalPoints(data.cursor) );

			LOG.fine("Ranking obtained by user : " + data.email);
			return Response.ok(JsonUtil.json.toJson(ranking)).build();

		} catch (InvalidTokenException e) {
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@POST
	@Path("/totalCurrencyRank")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response eventsParticipatedRank (CursorData data) {
		LOG.fine("Attempt to get ranking by: " + data.email);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {

			TokensResource.checkIsValidAccess(data.token, data.email);

			RankingData ranking = new RankingData( rankingEventsParticipated(data.cursor) );

			LOG.fine("Ranking obtained by user : " + data.email);
			return Response.ok(JsonUtil.json.toJson(ranking)).build();

		} catch (InvalidTokenException e) {
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();

		} catch (Exception e) {
			LOG.severe(e.getMessage());
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	public static Triplet<List<Entity>, Cursor, QueryResultBatch.MoreResultsType> rankingTotalPoints (String cursor){

		Builder<Entity> builder = Query.newEntityQueryBuilder().setKind("User").setOrderBy(OrderBy.desc(DB_User.TOTAL_CURRENCY))
				.setLimit(SEARCH_RESULTS_LIMIT);

		return rankingQuery (builder, cursor);
	}
	
	public static Triplet<List<Entity>, Cursor, QueryResultBatch.MoreResultsType> rankingEventsParticipated (String cursor){

		Builder<Entity> builder = Query.newEntityQueryBuilder().setKind("User").setOrderBy(OrderBy.desc(DB_User.N_EVENTS_PARTICIPATED))
				.setLimit(SEARCH_RESULTS_LIMIT);

		return rankingQuery (builder, cursor);
	}

	public static Triplet<List<Entity>, Cursor, QueryResultBatch.MoreResultsType> rankingQuery (Builder<Entity> builder, String cursor){

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

		return new Triplet<List<Entity>, Cursor, QueryResultBatch.MoreResultsType>(ranking,
				results.getCursorAfter(), MoreResultsType.MORE_RESULTS_AFTER_LIMIT);
	}


}
