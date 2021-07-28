package voluntier.util.data.user;

import java.util.LinkedList;
import java.util.List;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.DoubleValue;
import com.google.cloud.datastore.Entity;

import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.LongValue;
import com.google.cloud.datastore.StringValue;

import voluntier.exceptions.AlreadyExistsException;
import voluntier.exceptions.CannotCreateMoreException;
import voluntier.exceptions.ImpossibleActionException;
import voluntier.exceptions.InexistentElementException;
import voluntier.exceptions.InexistentEventException;
import voluntier.exceptions.InexistentRouteException;
import voluntier.exceptions.InexistentUserException;
import voluntier.exceptions.NotEnoughCurrencyException;
import voluntier.util.DB_Util;
import voluntier.util.DB_Variables;
import voluntier.util.JsonUtil;
import voluntier.util.consumes.user.RegisterData;
import voluntier.util.consumes.user.UpdateProfileData;
import voluntier.util.data.event.MaxCreationData;
import voluntier.util.data.statistics.DB_Statistics;

public class DB_User {

	public static final String USERNAME = "username";
	public static final String EMAIL = "user_email";

	public static final String PASSWORD = "user_pwd";

	public static final String FULL_NAME = "user_full_name";
	public static final String LANDLINE = "user_landline";
	public static final String MOBILE = "user_mobile";
	public static final String ADDRESS = "user_address";
	public static final String ADDRESS2 = "user_address2";
	public static final String REGION = "user_region";
	public static final String POSTAL_CODE = "user_pc";

	public static final String ACCOUNT = "user_account";
	public static final String ROLE = "user_role";
	public static final String STATE = "user_state";
	public static final String PROFILE = "user_profile";
	public static final String DESCRIPTION = "user_description";

	public static final String WEBSITE = "user_website";
	public static final String FACEBOOK = "user_facebook";
	public static final String INSTAGRAM = "user_instagram";
	public static final String TWITTER = "user_twitter";

	public static final String PROFILE_PICTURE_MINIATURE = "profile_pic_200x200";

	public static final String EVENTS = "user_events";
	public static final String EVENTS_PARTICIPATING = "user_events_participating";
	public static final String N_EVENTS_PARTICIPATED = "n_user_events_participated";
	public static final String MAX_EVENTS_PER_DAY = "max_user_events_day";
	public static final String TOTAL_CURRENCY = "total_currency";
	public static final String CURRENT_CURRENCY = "current_currency";
	public static final String DONATIONS = "donations";

	public static final String ROUTES = "user_routes";
	public static final String ROUTES_PARTICIPATING = "user_routes_participating";
	public static final String MAX_ROUTES_PER_DAY = "max_user_routes_day";

	public static final String EMAIL_REGEX = ".+@.+[.].+";
	public static final String POSTAL_CODE_REGEX = "[0-9]{4}-[0-9]{3}";
	public static final String MOBILE_REGEX = "([+][0-9]{2,3}\\s)?[2789][0-9]{8}";
	public static final String USERNAME_REGEX = "[a-zA-Z][a-zA-Z0-9]*([.][a-zA-Z0-9]+|[a-zA-Z0-9]*)";
		
	public static final String SEPARATOR = "|";
	public static final int MILISSECONDS_IN_DAY = 86400000;

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory usersFactory = datastore.newKeyFactory().setKind("User");

	private static DB_Util util = new DB_Util(DB_User::defaultBuilder);
	
	private static void defaultBuilder(Entity user) {
		util.builder = Entity.newBuilder(user.getKey())
				.set(USERNAME, user.getString(USERNAME))
				.set(EMAIL, user.getString(EMAIL))
				.set(PASSWORD, user.getString(PASSWORD))
				.set(FULL_NAME, user.getString(FULL_NAME))
				.set(LANDLINE, user.getString(LANDLINE))
				.set(MOBILE, user.getString(MOBILE))
				.set(ADDRESS, user.getString(ADDRESS))
				.set(ADDRESS2, user.getString(ADDRESS2))
				.set(REGION, user.getString(REGION))
				.set(POSTAL_CODE, user.getString(POSTAL_CODE))
				.set(ACCOUNT, user.getString(ACCOUNT))
				.set(ROLE, user.getString(ROLE))
				.set(STATE, user.getString(STATE))
				.set(PROFILE, user.getString(PROFILE))
				.set(DESCRIPTION, user.getString(DESCRIPTION))
				.set(WEBSITE, user.getString(WEBSITE))
				.set(FACEBOOK, user.getString(FACEBOOK))
				.set(INSTAGRAM, user.getString(INSTAGRAM))
				.set(TWITTER, user.getString(TWITTER))
				.set(N_EVENTS_PARTICIPATED, user.getLong(N_EVENTS_PARTICIPATED))
				.set(MAX_EVENTS_PER_DAY, user.getString(MAX_EVENTS_PER_DAY))
				.set(TOTAL_CURRENCY, user.getDouble(TOTAL_CURRENCY))
				.set(CURRENT_CURRENCY, user.getDouble(CURRENT_CURRENCY))
				.set(DONATIONS, user.getList(DONATIONS))
				.set(PROFILE_PICTURE_MINIATURE, StringValue.newBuilder(user.getString(PROFILE_PICTURE_MINIATURE))
						.setExcludeFromIndexes(true)
						.build())
				.set(EVENTS, user.getList(EVENTS))
				.set(EVENTS_PARTICIPATING, user.getList(EVENTS_PARTICIPATING))
				.set(ROUTES, user.getList(ROUTES))
				.set(ROUTES_PARTICIPATING, user.getList(ROUTES_PARTICIPATING))
				.set(MAX_ROUTES_PER_DAY, user.getString(MAX_ROUTES_PER_DAY));
	}

	public static Entity REWRITE(Entity user) {	

		return Entity.newBuilder(user.getKey())
				.set(USERNAME, user.getString(USERNAME))
				.set(EMAIL, user.getString(EMAIL))
				.set(PASSWORD, user.getString(PASSWORD))
				.set(FULL_NAME, user.getString(FULL_NAME))
				.set(LANDLINE, user.getString(LANDLINE))
				.set(MOBILE, user.getString(MOBILE))
				.set(ADDRESS, user.getString(ADDRESS))
				.set(ADDRESS2, user.getString(ADDRESS2))
				.set(REGION, user.getString(REGION))
				.set(POSTAL_CODE, user.getString(POSTAL_CODE))
				.set(ACCOUNT, user.getString(ACCOUNT))
				.set(ROLE, user.getString(ROLE))
				.set(STATE, user.getString(STATE))
				.set(PROFILE, user.getString(PROFILE))
				.set(DESCRIPTION, user.getString(DESCRIPTION))
				.set(WEBSITE, user.getString(WEBSITE))
				.set(FACEBOOK, user.getString(FACEBOOK))
				.set(INSTAGRAM, user.getString(INSTAGRAM))
				.set(TWITTER, user.getString(TWITTER))
				.set(N_EVENTS_PARTICIPATED, 0)
				.set(MAX_EVENTS_PER_DAY, user.getString(MAX_EVENTS_PER_DAY))
				.set(TOTAL_CURRENCY, 50.0)
				.set(CURRENT_CURRENCY, 50.0)
				.set(DONATIONS, DB_Util.emptyList().build())
				.set(PROFILE_PICTURE_MINIATURE, StringValue.newBuilder(user.getString(PROFILE_PICTURE_MINIATURE))
						.setExcludeFromIndexes(true)
						.build())
				.set(EVENTS, DB_Util.emptyList().build())
				.set(EVENTS_PARTICIPATING, DB_Util.emptyList().build())
				.set(ROUTES, DB_Util.emptyList().build())
				.set(ROUTES_PARTICIPATING, DB_Util.emptyList().build())
				.set(MAX_ROUTES_PER_DAY, user.getString(MAX_ROUTES_PER_DAY))
				.build();
	}
	
	public static Entity createNew(String email, String username, String password, Key userKey) {
		UserData_AllProperties data = new UserData_AllProperties(new RegisterData(email, username, password));
		ListValue empty_list = ListValue.newBuilder().build();

		MaxCreationData obj = new MaxCreationData();

		String maxString = JsonUtil.json.toJson(obj);
		
		DB_Statistics.updateNumUsers(true);
		double initial_currency = DB_Variables.getInitialCurrency();
		
		DB_Statistics.updateTotalCurrency(true, (int) initial_currency);
		DB_Statistics.updateTotalCurrentCurrency(true, (float) initial_currency);

		return Entity.newBuilder(userKey)
				.set(USERNAME, data.username)
				.set(PASSWORD, data.password)
				.set(FULL_NAME, data.full_name)
				.set(EMAIL, data.email)
				.set(ROLE, data.getRole().toString())
				.set(STATE, data.getState().toString())
				.set(PROFILE, data.profile)
				.set(LANDLINE, data.landline)
				.set(MOBILE, data.mobile)
				.set(ADDRESS, data.address)
				.set(ADDRESS2, data.address2)
				.set(REGION, data.region)
				.set(POSTAL_CODE, data.pc)
				.set(ACCOUNT, data.getAccount().toString())
				.set(DESCRIPTION, data.description)
				.set(WEBSITE, data.website)
				.set(FACEBOOK, data.facebook)
				.set(INSTAGRAM, data.instagram)
				.set(TWITTER, data.twitter)
				.set(TOTAL_CURRENCY, initial_currency)
				.set(CURRENT_CURRENCY, initial_currency)
				.set(N_EVENTS_PARTICIPATED, 0)
				.set(MAX_EVENTS_PER_DAY, maxString)
				.set(DONATIONS, empty_list)
				.set(PROFILE_PICTURE_MINIATURE, StringValue.newBuilder(data.profile_pic)
						.setExcludeFromIndexes(true)
						.build())
				.set(N_EVENTS_PARTICIPATED, 0)
				.set(DONATIONS, empty_list)
				.set(EVENTS, empty_list)
				.set(EVENTS_PARTICIPATING, empty_list)
				.set(ROUTES, empty_list)
				.set(ROUTES_PARTICIPATING, empty_list)
				.set(MAX_ROUTES_PER_DAY, maxString)
				.build();
	}

	public static Entity changePassword(String new_password, Key userKey, Entity user) {
		
		return util.updateProperty(user, PASSWORD, StringValue.of(UserData_Modifiable.hashPassword(new_password)));
	}

	public static Entity remove(Key userKey, Entity user) {
		DB_Statistics.updateNumUsers(false);
		
		return util.updateProperty(user, ACCOUNT, StringValue.of(Account.REMOVED.toString()));
	}

	public static Entity setState(String state, Key userKey, Entity user) {
		return util.updateProperty(user, STATE, StringValue.of(state));
	}

	public static Entity changeProperty(UpdateProfileData data, Key userKey, Entity user) {
		return Entity.newBuilder(userKey)
				.set(USERNAME, user.getString(USERNAME))
				.set(EMAIL, user.getString(EMAIL))
				.set(PASSWORD, data.getPassword(user.getString(PASSWORD)))
				.set(FULL_NAME, data.getFullName(user.getString(FULL_NAME)))
				.set(LANDLINE, data.getLandline(user.getString(LANDLINE)))
				.set(MOBILE, data.getMobile(user.getString(MOBILE)))
				.set(ADDRESS, data.getAddress(user.getString(ADDRESS)))
				.set(ADDRESS2, data.getAddress2(user.getString(ADDRESS2)))
				.set(REGION, data.getRegion(user.getString(REGION)))
				.set(POSTAL_CODE, data.getPc(user.getString(POSTAL_CODE)))
				.set(ACCOUNT, user.getString(ACCOUNT))
				.set(ROLE, user.getString(ROLE))
				.set(STATE, user.getString(STATE))
				.set(PROFILE, data.getProfile(user.getString(PROFILE)))
				.set(DESCRIPTION, data.getDescription(user.getString(DESCRIPTION)))
				.set(WEBSITE, data.getWebsite(user.getString(WEBSITE)))
				.set(FACEBOOK, data.getFacebook(user.getString(FACEBOOK)))
				.set(INSTAGRAM, data.getInstagram(user.getString(INSTAGRAM)))
				.set(TWITTER, data.getTwitter(user.getString(TWITTER)))
				.set(N_EVENTS_PARTICIPATED, user.getLong(N_EVENTS_PARTICIPATED))
				.set(MAX_EVENTS_PER_DAY, user.getString(MAX_EVENTS_PER_DAY))
				.set(TOTAL_CURRENCY, user.getDouble(TOTAL_CURRENCY))
				.set(CURRENT_CURRENCY, user.getDouble(CURRENT_CURRENCY))
				.set(DONATIONS, user.getList(DONATIONS))
				.set(PROFILE_PICTURE_MINIATURE, StringValue.newBuilder(user.getString(PROFILE_PICTURE_MINIATURE))
						.setExcludeFromIndexes(true)
						.build())
				.set(EVENTS, user.getList(EVENTS))
				.set(EVENTS_PARTICIPATING, user.getList(EVENTS_PARTICIPATING))
				.set(ROUTES, user.getList(ROUTES))
				.set(ROUTES_PARTICIPATING, user.getList(ROUTES_PARTICIPATING))
				.set(MAX_ROUTES_PER_DAY, user.getString(MAX_ROUTES_PER_DAY))
				.build();
	}

	public static Entity changeRole(String role, Key userKey, Entity user) {
		return util.updateProperty(user, ROLE, StringValue.of(role));
	}

	public static Entity changeProfilePicture(String data, Key userKey, Entity user) {
		return util.updateProperty(user, PROFILE_PICTURE_MINIATURE, StringValue.newBuilder(data).setExcludeFromIndexes(true).build());
	}

	public static Entity createID(String username, String email, Key usernameKey) {
		return Entity.newBuilder(usernameKey)
				.set(USERNAME, username)
				.set(EMAIL, email)
				.build();
	}

	public static List<String> getEventIds(Entity user) {
		return DB_Util.getStringList(user, EVENTS);
	}

	public static List<String> getRouteIds(Entity user) {
		return DB_Util.getStringList(user, ROUTES);
	}

	public static List<String> getParticipatingEventIds(Entity user) {
		return DB_Util.getStringList(user, EVENTS_PARTICIPATING);
	}

	public static List<String> getParticipatingRouteIds(Entity user) {
		return DB_Util.getStringList(user, ROUTES_PARTICIPATING);
	}

	public static Entity addEvent(Key userKey, Entity user, String event_id) throws AlreadyExistsException, CannotCreateMoreException, ImpossibleActionException {
		
		user = eventCreationLimit(user);

		//user = participateEvent(userKey, user, event_id);
		user = util.addUniqueStringToList(user, EVENTS, event_id);
		
		DB_Statistics.updateNumEvents(true);
		
		return user;
	}

	public static Entity removeEvent(Key userKey, Entity user, String event_id) throws InexistentEventException, InexistentElementException {

		if (!DB_Util.existsInStringList(user, EVENTS, event_id))
			throw new InexistentEventException("There is no event with the given event id");

		user = util.removeStringFromList(user, EVENTS, event_id);

		DB_Statistics.updateNumEvents(false);
		
		return user;

	}

	public static Entity participateEvent(Key userKey, Entity user, String event_id) throws ImpossibleActionException, AlreadyExistsException {
		
		user =  util.addUniqueStringToList(user, EVENTS_PARTICIPATING, event_id);
		
		DB_Statistics.updateNumParticipations(true);
		DB_Statistics.updateTotalNumParticipations();
		
		return user;
	}

	public static Entity addRoute(Key userKey, Entity user, String route_id) throws AlreadyExistsException, CannotCreateMoreException {
		
		user = routeCreationLimit (user);

		user = util.addUniqueStringToList(user, ROUTES, route_id);
				
		return user;
	}

	public static Entity removeRoute(Key userKey, Entity user, String route_id) throws InexistentRouteException, InexistentElementException {

		if (!DB_Util.existsInStringList(user, ROUTES, route_id))
			throw new InexistentRouteException("There is no route with the given route id");

		user = util.removeStringFromList(user, ROUTES, route_id);
		
		DB_Statistics.updateNumRoutes(false);
		
		return user;
	}

	public static Entity participateRoute(Key userKey, Entity user, String route_id) throws AlreadyExistsException {

		return util.addUniqueStringToList(user, ROUTES_PARTICIPATING, route_id);
	}
	
	public static Entity leaveRoute(Key userKey, Entity user, String route_id) throws InexistentRouteException, InexistentElementException  {

		if (!DB_Util.existsInStringList(user, ROUTES_PARTICIPATING, route_id))
			throw new InexistentRouteException("User does not belong to that route");

		return util.removeStringFromList(user, ROUTES_PARTICIPATING, route_id);
	}

	public static Entity leaveEvent(Key userKey, Entity user, String event_id) throws InexistentEventException, InexistentElementException {

		if (!DB_Util.existsInStringList(user, EVENTS_PARTICIPATING, event_id))
			throw new InexistentEventException("There is no event with the given event id");

		user = util.removeStringFromList(user, EVENTS_PARTICIPATING, event_id);
		
		DB_Statistics.updateNumParticipations(false);
		
		return user;
	}

	public static Entity leaveEvent (String user_email, double amount, int difficulty) throws InexistentUserException{
		Entity user = getUser(user_email);
		
		user = util.updateProperty(user, N_EVENTS_PARTICIPATED, LongValue.of(user.getLong(N_EVENTS_PARTICIPATED) + 1));

		int earnedAmount = (int) amount*difficulty;
		
		user = util.updateProperty(user, TOTAL_CURRENCY, DoubleValue.of(user.getDouble(TOTAL_CURRENCY) + amount*difficulty));
		user = util.updateProperty(user, CURRENT_CURRENCY, DoubleValue.of(user.getDouble(CURRENT_CURRENCY) + amount*difficulty));
		
		DB_Statistics.updateNumPresences();
		DB_Statistics.updateTotalCurrency(true, earnedAmount);
		DB_Statistics.updateTotalCurrentCurrency(true, earnedAmount);
		
		return  user;
	}

	public static Entity donate(Entity user, float amount, String cause_id, String cause_name) throws NotEnoughCurrencyException {
		if(user.getDouble(CURRENT_CURRENCY) < amount)
			throw new NotEnoughCurrencyException("User: " + user.getString(EMAIL) + " does not have enough currency to make this donation");
		
		user = util.updateProperty(user, TOTAL_CURRENCY, DoubleValue.of(user.getDouble(TOTAL_CURRENCY)));
		user = util.updateProperty(user, CURRENT_CURRENCY, DoubleValue.of(user.getDouble(CURRENT_CURRENCY) - amount));
		user = util.addJsonToList(user, DONATIONS, new DonationData(cause_name, cause_id, amount, Timestamp.now().toString()));
		
		return user;
	}

	public static List<DonationData> getDonations(Entity user) {

		return DB_Util.getJsonList(user, DONATIONS, DonationData.class);
	}

	public static Entity getUser(String user_email) 
			throws InexistentUserException {
		Key userKey = usersFactory.newKey(user_email);
		Entity user = datastore.get(userKey);

		if(user == null)
			throw new InexistentUserException("Inexistent user: " + user_email);

		return user;
	}

	public static String getProfilePictureFilename(String username, String ext) {
		return username + "_profile_picture." + ext;
	}

	public static String getName (String user_email) {
		Key userKey = usersFactory.newKey(user_email);
		Entity user = datastore.get(userKey);

		return user.getString(FULL_NAME);
	}

	public static boolean isPublicProfile (Entity user) {
		return user.getString(PROFILE).equals(Profile.PUBLIC.toString());
	}

	public static List<String> getPrivateProfileInfo (Entity user){
		List<String> info = new LinkedList<>();
		info.add(user.getString(USERNAME));
		info.add(user.getString(EMAIL));
		info.add(user.getString(PROFILE_PICTURE_MINIATURE));
		return info;
	}
	
	private static Entity routeCreationLimit (Entity user) throws CannotCreateMoreException {
		return creationLimit (user, MAX_ROUTES_PER_DAY, (int) DB_Variables.getMaxRoutesPerDay());
	}
	
	private static Entity eventCreationLimit (Entity user) throws CannotCreateMoreException {
		return creationLimit (user, MAX_EVENTS_PER_DAY, (int) DB_Variables.getMaxEventsPerDay());
	}
	
	private static Entity creationLimit (Entity user, String property, int limit) throws CannotCreateMoreException {
		String maxEvents = user.getString(property);
				
		MaxCreationData data = JsonUtil.json.fromJson(maxEvents, MaxCreationData.class);

		Timestamp now = Timestamp.now();
		String now_string = now + "";
		
		if (data.last_event_dates.size() < limit)
			return util.updateProperty(user, property, StringValue.of(getNewLimitString(now_string, data)));

		// when lowering the limit on the database, need to delete extra (old) dates
		if(data.last_event_dates.size() > limit) {
			List<String> temp = new LinkedList<>();
			int i = 0;
			for(String date : data.last_event_dates)
				if(i++ >= data.last_event_dates.size() - limit)
					temp.add(date);
			data.last_event_dates = temp;
		}
		
		String firstDate = data.last_event_dates.remove(0);
		double nowMillis = now.toDate().getTime();
		double firstDateMillis = Timestamp.parseTimestamp(firstDate).toDate().getTime();
		
		if (nowMillis - firstDateMillis < MILISSECONDS_IN_DAY)
			throw new CannotCreateMoreException("The user cannot create more than " + limit + " in less than 24 hours.");
		
		return util.updateProperty(user, property, StringValue.of(getNewLimitString(now_string, data)));
	}
	
	private static String getNewLimitString (String now, MaxCreationData data) {
		data.last_event_dates.add(now);
		return JsonUtil.json.toJson( data );
	}
}
