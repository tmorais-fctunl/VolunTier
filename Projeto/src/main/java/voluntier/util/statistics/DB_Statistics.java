package voluntier.util.statistics;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.DoubleValue;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.LongValue;
import com.google.cloud.datastore.Transaction;

import voluntier.util.DB_Util;

public class DB_Statistics {
	
	public static final String STATISTICS_KEY = "statistics";
	
	public static final String TOTAL_NUM_USERS = "total_num_users";
	public static final String TOTAL_NUM_EVENTS = "total_num_events";
	public static final String TOTAL_NUM_ROUTES = "total_num_routes";
	public static final String TOTAL_NUM_CAUSES = "total_num_causes";
	
	public static final String NUM_PARTICIPATIONS_EVENT = "num_participations";
	public static final String TOTAL_NUM_PARTICIPATIONS_EVENT = "total_num_participations";
	public static final String TOTAL_NUM_PRESENCES = "total_num_presences";
	
	public static final String TOTAL_COMMENTS = "total_commments";
	
	public static final String TOTAL_TIME_PRESENCES = "total_time_presences";
	public static final String TOTAL_CURRENT_CURRENCY = "total_current_currency";
	public static final String TOTAL_ALLTIME_CURRENCY = "total_alltime_currency";
	public static final String TOTAL_DONATIONS = "total_donations";
	public static final String TOTAL_DONATED = "total_donated";

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory statisticsFactory = datastore.newKeyFactory().setKind("Statistics");
	
	private static DB_Util util = new DB_Util(DB_Statistics::defaultBuilder);
	
	private static void defaultBuilder(Entity statistics) {
		util.builder = Entity.newBuilder(statistics.getKey())
				.set(TOTAL_NUM_USERS, statistics.getLong(TOTAL_NUM_USERS))
				.set(TOTAL_NUM_EVENTS, statistics.getLong(TOTAL_NUM_EVENTS))
				.set(TOTAL_NUM_ROUTES, statistics.getLong(TOTAL_NUM_ROUTES))
				.set(TOTAL_NUM_CAUSES, statistics.getLong(TOTAL_NUM_CAUSES))
				.set(NUM_PARTICIPATIONS_EVENT, statistics.getLong(NUM_PARTICIPATIONS_EVENT))
				.set(TOTAL_NUM_PARTICIPATIONS_EVENT, statistics.getLong(TOTAL_NUM_PARTICIPATIONS_EVENT))
				.set(TOTAL_NUM_PRESENCES, statistics.getLong(TOTAL_NUM_PRESENCES))
				.set(TOTAL_COMMENTS, statistics.getLong(TOTAL_COMMENTS))
				.set(TOTAL_TIME_PRESENCES, statistics.getLong(TOTAL_TIME_PRESENCES))
				.set(TOTAL_CURRENT_CURRENCY, statistics.getDouble(TOTAL_CURRENT_CURRENCY))
				.set(TOTAL_ALLTIME_CURRENCY, statistics.getLong(TOTAL_ALLTIME_CURRENCY))
				.set(TOTAL_DONATIONS, statistics.getLong(TOTAL_DONATIONS))
				.set(TOTAL_DONATED, statistics.getDouble(TOTAL_DONATED));
	}
	
	public static Response createStatistics () {
		Key statisticsKey = statisticsFactory.newKey(STATISTICS_KEY);
		Transaction txn = datastore.newTransaction();
		
		Entity statistics = Entity.newBuilder(statisticsKey)
				.set(TOTAL_NUM_USERS, 0)
				.set(TOTAL_NUM_EVENTS, 0)
				.set(TOTAL_NUM_ROUTES, 0)
				.set(TOTAL_NUM_CAUSES, 0)
				.set(NUM_PARTICIPATIONS_EVENT, 0)
				.set(TOTAL_NUM_PARTICIPATIONS_EVENT, 0)
				.set(TOTAL_NUM_PRESENCES, 0)
				.set(TOTAL_COMMENTS, 0)
				.set(TOTAL_TIME_PRESENCES, 0)
				.set(TOTAL_CURRENT_CURRENCY, 0.0)
				.set(TOTAL_ALLTIME_CURRENCY, 0)
				.set(TOTAL_DONATIONS, 0)
				.set(TOTAL_DONATED, 0.0)
				.build();
		
		try {
			txn.put(statistics);	
			txn.commit();
		} catch (Exception e) {
			txn.rollback();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();

		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
		
		return Response.status(Status.NO_CONTENT).build();
	}
	
	public static Entity getStatistics () {
		Key statisticsKey = statisticsFactory.newKey(STATISTICS_KEY);
		return datastore.get(statisticsKey);
	}
	
	private static void putStatistics (Entity statistics) {
		Transaction txn = datastore.newTransaction();
		try {
			txn.put(statistics);	
			txn.commit();
		} catch (Exception e) {
			txn.rollback();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
	public static void updateNumUsers (boolean add) {
		Entity stat = getStatistics();
		stat = add ? util.updateProperty(stat, TOTAL_NUM_USERS, LongValue.of(stat.getLong(TOTAL_NUM_USERS) + 1))
				: util.updateProperty(stat, TOTAL_NUM_USERS, LongValue.of(stat.getLong(TOTAL_NUM_USERS) - 1));
		putStatistics(stat);
	}
	
	public static void updateNumEvents (boolean add) {
		Entity stat = getStatistics();
		stat = add ? util.updateProperty(stat, TOTAL_NUM_EVENTS, LongValue.of(stat.getLong(TOTAL_NUM_EVENTS) + 1))
				: util.updateProperty(stat, TOTAL_NUM_EVENTS, LongValue.of(stat.getLong(TOTAL_NUM_EVENTS) - 1));
		putStatistics(stat);
	}
	
	public static void updateNumRoutes (boolean add) {
		Entity stat = getStatistics();
		stat = add ? util.updateProperty(stat, TOTAL_NUM_ROUTES, LongValue.of(stat.getLong(TOTAL_NUM_ROUTES) + 1))
				: util.updateProperty(stat, TOTAL_NUM_ROUTES, LongValue.of(stat.getLong(TOTAL_NUM_ROUTES) - 1));
		putStatistics(stat);
	}
	
	public static void updateNumCauses (boolean add) {
		Entity stat = getStatistics();
		stat = add ? util.updateProperty(stat, TOTAL_NUM_CAUSES, LongValue.of(stat.getLong(TOTAL_NUM_CAUSES) + 1))
				: util.updateProperty(stat, TOTAL_NUM_CAUSES, LongValue.of(stat.getLong(TOTAL_NUM_CAUSES) - 1));
		putStatistics(stat);
	}
	
	public static void updateNumParticipations (boolean add) {
		Entity stat = getStatistics();
		stat = add ? util.updateProperty(stat, NUM_PARTICIPATIONS_EVENT, LongValue.of(stat.getLong(NUM_PARTICIPATIONS_EVENT) + 1))
				: util.updateProperty(stat, NUM_PARTICIPATIONS_EVENT, LongValue.of(stat.getLong(NUM_PARTICIPATIONS_EVENT) - 1));
		putStatistics(stat);
	}
	
	public static void updateNumParticipations (boolean add, int number) {
		Entity stat = getStatistics();
		stat = add ? util.updateProperty(stat, NUM_PARTICIPATIONS_EVENT, LongValue.of(stat.getLong(NUM_PARTICIPATIONS_EVENT) + number))
				: util.updateProperty(stat, NUM_PARTICIPATIONS_EVENT, LongValue.of(stat.getLong(NUM_PARTICIPATIONS_EVENT) - number));
		putStatistics(stat);
	}
	
	public static void updateTotalNumParticipations () {
		Entity stat = getStatistics();
		util.updateProperty(stat, TOTAL_NUM_PARTICIPATIONS_EVENT, LongValue.of(stat.getLong(TOTAL_NUM_PARTICIPATIONS_EVENT) + 1));
		putStatistics(stat);
	}
	
	public static void updateNumPresences () {
		Entity stat = getStatistics();
		util.updateProperty(stat, TOTAL_NUM_PRESENCES, LongValue.of(stat.getLong(TOTAL_NUM_PRESENCES) + 1));
		putStatistics(stat);
	}
	
	public static void updateNumComments (boolean add) {
		Entity stat = getStatistics();
		stat = add ? util.updateProperty(stat, TOTAL_COMMENTS, LongValue.of(stat.getLong(TOTAL_COMMENTS) + 1))
				: util.updateProperty(stat, TOTAL_COMMENTS, LongValue.of(stat.getLong(TOTAL_COMMENTS) - 1));
		putStatistics(stat);
	}
	
	/*public static void updateNumCommentsEvent (boolean add) {
		Entity stat = getStatistics();
		stat = add ? util.updateProperty(stat, TOTAL_COMMENTS_EVENTS, LongValue.of(stat.getLong(TOTAL_COMMENTS_EVENTS) + 1))
				: util.updateProperty(stat, TOTAL_COMMENTS_EVENTS, LongValue.of(stat.getLong(TOTAL_COMMENTS_EVENTS) - 1));
		putStatistics(stat);
	}
	
	public static void updateNumCommentsRoute (boolean add) {
		Entity stat = getStatistics();
		stat = add ? util.updateProperty(stat, TOTAL_COMMENTS_ROUTES, LongValue.of(stat.getLong(TOTAL_COMMENTS_ROUTES) + 1))
				: util.updateProperty(stat, TOTAL_COMMENTS_ROUTES, LongValue.of(stat.getLong(TOTAL_COMMENTS_ROUTES) - 1));
		putStatistics(stat);
	}*/
	
	public static void updateTotalTimePresences (long time) {
		Entity stat = getStatistics();
		util.updateProperty(stat, TOTAL_TIME_PRESENCES, LongValue.of(stat.getLong(TOTAL_TIME_PRESENCES) + time));
		putStatistics(stat);
	}
	
	public static void updateTotalCurrentCurrency (boolean add, float amount) {
		Entity stat = getStatistics();
		stat = add ? util.updateProperty(stat, TOTAL_CURRENT_CURRENCY, DoubleValue.of(stat.getDouble(TOTAL_CURRENT_CURRENCY) + amount))
				: util.updateProperty(stat, TOTAL_CURRENT_CURRENCY, DoubleValue.of(stat.getDouble(TOTAL_CURRENT_CURRENCY) - amount));
		putStatistics(stat);
	}
	
	public static void updateTotalCurrency (boolean add, int amount) {
		Entity stat = getStatistics();
		util.updateProperty(stat, TOTAL_ALLTIME_CURRENCY, LongValue.of(stat.getLong(TOTAL_ALLTIME_CURRENCY) + amount));
		putStatistics(stat);
	}
	
	public static void updateTotalDonated (float amount) {
		Entity stat = getStatistics();
		util.updateProperty(stat, TOTAL_DONATED, DoubleValue.of(stat.getDouble(TOTAL_DONATED) + amount));
		putStatistics(stat);
	}
	
	public static void updateTotalDonations () {
		Entity stat = getStatistics();
		util.updateProperty(stat, TOTAL_DONATIONS, DoubleValue.of(stat.getDouble(TOTAL_DONATIONS) + 1));
		putStatistics(stat);
	}
	
}
