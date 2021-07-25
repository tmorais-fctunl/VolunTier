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

import voluntier.exceptions.InvalidTokenException;
import voluntier.util.JsonUtil;
import voluntier.util.consumes.generic.RequestData;
import voluntier.util.data.statistics.DB_Statistics;
import voluntier.util.data.statistics.StatisticsReturn;
import voluntier.util.data.user.DB_User;


@Path("/")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class StatisticsResource {

	private static final Logger LOG = Logger.getLogger(EventResource.class.getName());
	
	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory usersFactory = datastore.newKeyFactory().setKind("User");

	public StatisticsResource() {
	}

	//FOR DEBUG ONLY. NOT TO BE USED
	@POST
	@Path("/createStats")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createStats (RequestData data) {
		LOG.fine("Trying to create statistics entity");

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);

			Key userKey = usersFactory.newKey(data.email);
			Entity user = datastore.get(userKey);

			if (user == null || ActionsResource.isRemovedOrBannedUser(user) || !ActionsResource.isSU(user)) {
			//	LOG.warning("User:" + user.getString(DB_User.EMAIL) + " cannot do this operation.");
				return Response.status(Status.FORBIDDEN).entity("User:" + user.getString(DB_User.EMAIL) + " cannot do this operation.").build();
			}

			DB_Statistics.createStatistics();

			return Response.status(Status.NO_CONTENT).build();
		} 
		catch (InvalidTokenException e) {
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();
	}
		
	}

	@POST
	@Path("/getStats")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStatisticNumbers(RequestData data) {
		LOG.fine("Trying to obtain statistics by user: " + data.email);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);
			
			Key userKey = usersFactory.newKey(data.email);
			Entity user = datastore.get(userKey);
			
			if (!ActionsResource.hasEventPermission(user))
				return Response.status(Status.FORBIDDEN).entity("User has not enough permissions to check app statistics").build();
			
			StatisticsReturn stats = new StatisticsReturn (DB_Statistics.getStatistics()/*, StatisticsModes.NUMBERS*/);

			return Response.ok(JsonUtil.json.toJson(stats)).build();
		}
		catch (InvalidTokenException e) {
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();
		}
	}
	/*
	@POST
	@Path("/getStatsPercentages")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStatisticPercentages(RequestData data) {
		LOG.fine("Trying to obtain statistics by user: " + data.email);

		if (!data.isValid())
			return Response.status(Status.BAD_REQUEST).build();

		try {
			TokensResource.checkIsValidAccess(data.token, data.email);
			
			Key userKey = usersFactory.newKey(data.email);
			Entity user = datastore.get(userKey);
			
			if (!ActionsResource.hasEventPermission(user))
				return Response.status(Status.FORBIDDEN).entity("User has not enough permissions to check app statistics").build();
			
			StatisticsReturn stats = new StatisticsReturn (DB_Statistics.getStatistics(), StatisticsModes.PERCENTAGES);

			return Response.ok(JsonUtil.json.toJson(stats)).build();
		}
		catch (InvalidTokenException e) {
			return Response.status(Status.FORBIDDEN).entity(e.getMessage()).build();
		}
	}
	*/
}
