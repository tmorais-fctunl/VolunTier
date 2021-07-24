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
	public int total_donations;
	public float total_donated;

	public float users_per_event;
	public float presence_time_per_user;
	public float presence_time_per_event;
	public float donated_per_cause;
	public float donations_average;
	public float comments_per_time;
	public float comments_per_user;
	public float presences_average;
	public float presences_average_total;

	public StatisticsReturn () {

	}

	public StatisticsReturn (Entity statistics/*, StatisticsModes type*/) {

		/*switch (type) {
		case NUMBERS: 
			defineNumbers(statistics);
			break;
		case PERCENTAGES: */
			defineNumbers (statistics);
			definePerTime(statistics);
		/*	break;
		default:

		}*/

	}

	private void defineNumbers (Entity statistics) {
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
		this.total_donations = (int)statistics.getLong(DB_Statistics.TOTAL_DONATIONS);
		this.total_donated = (float)statistics.getDouble(DB_Statistics.TOTAL_DONATED);
	}

	private void definePerTime (Entity statistcs) {
		try {
			this.users_per_event = total_num_users / total_num_events;
			this.presence_time_per_user = total_time_presences / total_num_users;
			this.presence_time_per_event = total_time_presences / total_num_events;
			this.donated_per_cause = total_donated / total_num_causes;
			this.donations_average = total_donated / total_donations;
			this.comments_per_time = total_commments / total_time_presences;
			this.comments_per_user = total_commments / total_num_users;
			this.presences_average = total_num_presences / num_participations_event;
			this.presences_average = total_num_presences / total_num_participations_event;
		} catch (ArithmeticException e) {
			
		}
	}

}
