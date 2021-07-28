package voluntier.util;

import java.util.List;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Transaction;

import voluntier.util.data.statistics.Variables;
import voluntier.util.produces.generic.VariablesReturn;

public class DB_Variables {

	private static final String CATEGORIES = "categories";
	private static final String MAX_MESSAGE_LOG_SIZE = "max_message_log_size";
	private static final String MAX_ROUTES_PER_DAY = "max_routes_per_day";
	private static final String MAX_EVENTS_PER_DAY = "max_events_per_day";
	private static final String INITIAL = "initial";
	private static final String EARN_PER_MINUTE = "earn_per_minute";
	private static final String TIME = "time";
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
		return DB_Util.getStringList(categories, CATEGORIES);
	}

	public static long getRegisterCodeExpiration() {
		Entity expiration = datastore.get(register_expiration_key);
		return expiration.getLong(TIME);
	}

	public static long getForgotPasswordCodeExpiration() {
		Entity expiration = datastore.get(forgot_password_expiration_key);
		return expiration.getLong(TIME);
	}

	public static long getAccessExpiration() {
		Entity expiration = datastore.get(access_expiration_key);
		return expiration.getLong(TIME);
	}

	public static long getRefreshExpiration() {
		Entity expiration = datastore.get(refresh_expiration_key);
		return expiration.getLong(TIME);
	}

	public static long getCurrencyPerMinute() {
		Entity cpm = datastore.get(currency_key);
		return cpm.getLong(EARN_PER_MINUTE);
	}

	public static double getInitialCurrency() {
		Entity cpm = datastore.get(currency_key);
		return cpm.getDouble(INITIAL);
	}

	public static long getMaxEventsPerDay() {
		Entity max = datastore.get(events_key);
		return max.getLong(MAX_EVENTS_PER_DAY);
	}

	public static long getMaxRoutesPerDay() {
		Entity max = datastore.get(routes_key);
		return max.getLong(MAX_ROUTES_PER_DAY);
	}

	public static long getMaxMessageLogSize() {
		Entity max = datastore.get(chat_key);
		return max.getLong(MAX_MESSAGE_LOG_SIZE);
	}

	private static boolean setProperty (Entity entity) {
		Transaction txn = datastore.newTransaction();
		try {
			txn.put(entity);	
			txn.commit();
			return true;
		} catch (Exception e) {
			txn.rollback();
			return false;
		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return false;
			}
		}
	}
	
	public static VariablesReturn getAllVariables () {
		return new VariablesReturn((int) getAccessExpiration(), (int)getMaxMessageLogSize(), (int)getCurrencyPerMinute(), getInitialCurrency(),
				(int) getMaxEventsPerDay(), (int)getMaxRoutesPerDay(), (int)getForgotPasswordCodeExpiration(), 
				(int) getRefreshExpiration(), (int)getRegisterCodeExpiration());
	}
	
	public static boolean changeVariables (String property, String propertyValue) {
		Variables prop = Variables.valueOf(property);
		
		switch (prop) {
			case REGISTER_CODE_EXPIRATION : 
				return setRegisterCodeExpiration(Integer.parseInt(propertyValue));
			case FORGOT_PASS_EXPIRATION:
				return setForgotPasswordCodeExpiration(Integer.parseInt(propertyValue));
			case ACCESS_EXPIRATION:
				return setAccessExpiration(Integer.parseInt(propertyValue));
			case REFRESH_EXPIRATION:
				return setRefreshExpiration(Integer.parseInt(propertyValue));
			case CURRENCY_PER_MINUTE:
				return setCurrencyPerMinute(Integer.parseInt(propertyValue));
			case INITIAL_CURRENCY:
				return setInitialCurrency(Double.parseDouble(propertyValue));
			case MAX_EVENTS:
				return setMaxEventsPerDay(Integer.parseInt(propertyValue));
			case MAX_ROUTES:
				return setMaxRoutesPerDay(Integer.parseInt(propertyValue));
			case MAX_MESSAGE_LOG:
				return setMaxMessageLogSize(Integer.parseInt(propertyValue));
			default:
				return false;
		}
	}

	private static boolean setRegisterCodeExpiration(int time) {
		Entity expiration = datastore.get(register_expiration_key);
		expiration = Entity.newBuilder(register_expiration_key).set(TIME, time).build();
		return setProperty(expiration);
	}

	private static boolean setForgotPasswordCodeExpiration(int time) {
		Entity expiration = datastore.get(forgot_password_expiration_key);
		expiration = Entity.newBuilder(forgot_password_expiration_key).set(TIME, time).build();
		return setProperty(expiration);
	}

	private static boolean setAccessExpiration(int time) {
		Entity expiration = datastore.get(access_expiration_key);
		expiration = Entity.newBuilder(access_expiration_key).set(TIME, time).build();
		return setProperty(expiration);
	}

	private static boolean setRefreshExpiration(int time) {
		Entity expiration = datastore.get(refresh_expiration_key);
		expiration = Entity.newBuilder(refresh_expiration_key).set(TIME, time).build();
		return setProperty(expiration);
	}

	private static boolean setCurrencyPerMinute(int amount) {
		Entity cpm = datastore.get(currency_key);
		cpm = Entity.newBuilder(currency_key).set(EARN_PER_MINUTE, amount).set(INITIAL, cpm.getDouble(INITIAL)).build();
		return setProperty(cpm);
	}

	private static boolean setInitialCurrency(double amount) {
		Entity cic = datastore.get(currency_key);
		cic = Entity.newBuilder(currency_key).set(INITIAL, amount).set(EARN_PER_MINUTE, cic.getLong(EARN_PER_MINUTE)).build();
		return setProperty(cic);
	}

	private static boolean setMaxEventsPerDay(int max_events) {
		Entity max = datastore.get(events_key);
		max = Entity.newBuilder(events_key).set(MAX_EVENTS_PER_DAY, max_events).set(CATEGORIES, max.getList(CATEGORIES)).build();
		return setProperty(max);
	}

	private static boolean setMaxRoutesPerDay(int max_routes) {
		Entity max = datastore.get(routes_key);
		max = Entity.newBuilder(routes_key).set(MAX_ROUTES_PER_DAY, max_routes).build();
		return setProperty(max);
	}

	private static boolean setMaxMessageLogSize(int max_mls) {
		Entity max = datastore.get(chat_key);
		max = Entity.newBuilder(chat_key).set(MAX_MESSAGE_LOG_SIZE, max_mls).build();
		return setProperty(max);
	}

}
