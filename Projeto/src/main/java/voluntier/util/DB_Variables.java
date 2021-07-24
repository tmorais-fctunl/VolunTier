package voluntier.util;

import java.util.List;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;

public class DB_Variables {

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory variableFactory = datastore.newKeyFactory().setKind("Variable");

	private static Key register_expiration_key = variableFactory.newKey("register_email_expiration");
	private static Key forgot_password_expiration_key = variableFactory.newKey("forgot_password_expiration");

	private static Key access_expiration_key = variableFactory.newKey("access_expiration");
	private static Key refresh_expiration_key = variableFactory.newKey("refresh_expiration");

	private static Key currency_key = variableFactory.newKey("currency");

	private static Key routes_key = variableFactory.newKey("routes");
	private static Key events_key = variableFactory.newKey("events");
	

	private static Key chat_key = variableFactory.newKey("chat");
	
	public static List<String> getCategoriesList(){
		Entity categories = datastore.get(events_key);
		return DB_Util.getStringList(categories, "categories");
	}
	
	public static long getRegisterCodeExpiration() {
		Entity expiration = datastore.get(register_expiration_key);
		return expiration.getLong("time");
	}
	
	public static long getForgotPasswordCodeExpiration() {
		Entity expiration = datastore.get(forgot_password_expiration_key);
		return expiration.getLong("time");
	}
	
	public static long getAccessExpiration() {
		Entity expiration = datastore.get(access_expiration_key);
		return expiration.getLong("time");
	}
	
	public static long getRefreshExpiration() {
		Entity expiration = datastore.get(refresh_expiration_key);
		return expiration.getLong("time");
	}
	
	public static long getCurrencyPerMinute() {
		Entity cpm = datastore.get(currency_key);
		return cpm.getLong("earn_per_minute");
	}
	
	public static double getInitialCurrency() {
		Entity cpm = datastore.get(currency_key);
		return cpm.getDouble("initial");
	}
	
	public static long getMaxEventsPerDay() {
		Entity max = datastore.get(events_key);
		return max.getLong("max_events_per_day");
	}
	
	public static long getMaxRoutesPerDay() {
		Entity max = datastore.get(routes_key);
		return max.getLong("max_routes_per_day");
	}
	
	public static long getMaxMessageLogSize() {
		Entity max = datastore.get(chat_key);
		return max.getLong("max_message_log_size");
	}
}
