package voluntier.util.statistics;

import com.google.cloud.datastore.Entity;

public class StatisticsReturn {
	
	public int total_num_users;
	public int total_num_events;
	public int total_num_routes;
	public int total_num_causes;
	
	public int num_participations_event;
	public int total_num_participations_event;
	public int total_num_presences;
	
	public int total_commments;
	/*public int total_comments_events;
	public int total_comments_routes;*/
	
	public long total_time_presences;
	public float total_current_currency;
	public long total_alltime_currency;
	public float total_dontations;
	
	public StatisticsReturn () {
		
	}
	
	public StatisticsReturn (Entity statistics) {
		
		this.total_num_users = (int) statistics.getLong(DB_Statistics.TOTAL_NUM_USERS);
		this.total_num_events =  (int)statistics.getLong(DB_Statistics.TOTAL_NUM_EVENTS);
		this.total_num_routes =	 (int)statistics.getLong(DB_Statistics.TOTAL_NUM_ROUTES);
		this.total_num_causes =  (int)statistics.getLong(DB_Statistics.TOTAL_NUM_CAUSES);
		this.num_participations_event = (int)statistics.getLong(DB_Statistics.NUM_PARTICIPATIONS_EVENT);
		this.total_num_participations_event = (int)statistics.getLong(DB_Statistics.TOTAL_NUM_PARTICIPATIONS_EVENT);
		this.total_num_presences = (int)statistics.getLong(DB_Statistics.TOTAL_NUM_PRESENCES);
		this.total_commments = (int)statistics.getLong(DB_Statistics.TOTAL_COMMENTS);
		/*this.total_comments_events = (int)statistics.getLong(DB_Statistics.TOTAL_COMMENTS_EVENTS);
		this.total_comments_routes = (int)statistics.getLong(DB_Statistics.TOTAL_COMMENTS_ROUTES);*/
		this.total_time_presences = (int)statistics.getLong(DB_Statistics.TOTAL_TIME_PRESENCES);
		this.total_current_currency = (float)statistics.getDouble(DB_Statistics.TOTAL_CURRENT_CURRENCY);
		this.total_alltime_currency = (int)statistics.getLong(DB_Statistics.TOTAL_ALLTIME_CURRENCY);
		this.total_dontations = (float)statistics.getDouble(DB_Statistics.TOTAL_DONATIONS);
	}

}
