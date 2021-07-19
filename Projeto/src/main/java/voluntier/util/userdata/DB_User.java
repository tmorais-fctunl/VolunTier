package voluntier.util.userdata;

import java.util.LinkedList;
import java.util.List;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;

import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.Value;

import voluntier.exceptions.ImpossibleActionException;
import voluntier.exceptions.InexistentEventException;
import voluntier.exceptions.InexistentRouteException;
import voluntier.exceptions.InexistentUserException;
import voluntier.util.consumes.RegisterData;
import voluntier.util.consumes.UpdateProfileData;

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

	public static final String WEBSITE = "user_website";
	public static final String FACEBOOK = "user_facebook";
	public static final String INSTAGRAM = "user_instagram";
	public static final String TWITTER = "user_twitter";

	public static final String PROFILE_PICTURE_MINIATURE = "profile_pic_200x200";

	public static final String EVENTS = "user_events";
	public static final String EVENTS_PARTICIPATING = "user_events_participating";
	public static final String N_EVENTS_PARTICIPATED = "n_user_events_participated";
	public static final String TOTAL_CURRENCY = "total_currency";
	public static final String CURRENT_CURRENCY = "current_currency";
	public static final String DONATIONS = "donations";
	
	public static final String ROUTES = "user_routes";
	public static final String ROUTES_PARTICIPATING = "user_routes_participating";
	
	public static final String EMAIL_REGEX = ".+@.+[.].+";
	public static final String POSTAL_CODE_REGEX = "[0-9]{4}-[0-9]{3}";
	public static final String MOBILE_REGEX = "([+][0-9]{2,3}\\s)?[2789][0-9]{8}";
	public static final String USERNAME_REGEX = "[a-zA-Z][a-zA-Z0-9]*([.][a-zA-Z0-9]+|[a-zA-Z0-9]*)";
	
	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory usersFactory = datastore.newKeyFactory().setKind("User");
	
	public static Entity REWRITE(Entity user) {
		ListValue empty_list = ListValue.newBuilder().build();
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
				.set(WEBSITE, user.getString(WEBSITE))
				.set(FACEBOOK, user.getString(FACEBOOK))
				.set(INSTAGRAM, user.getString(INSTAGRAM))
				.set(TWITTER, user.getString(TWITTER))
				.set(N_EVENTS_PARTICIPATED, 0)
				.set(TOTAL_CURRENCY, 0)
				.set(CURRENT_CURRENCY, 0)
				.set(DONATIONS, empty_list)
				.set(PROFILE_PICTURE_MINIATURE, StringValue.newBuilder(user.getString(PROFILE_PICTURE_MINIATURE))
						.setExcludeFromIndexes(true)
						.build())
				.set(EVENTS, user.getList(EVENTS))
				.set(EVENTS_PARTICIPATING, user.getList(EVENTS_PARTICIPATING))
				.set(ROUTES, empty_list)
				.set(ROUTES_PARTICIPATING, empty_list)
				.build();
	}
		
	public static Entity changePassword(String new_password, Key userKey, Entity user) {
		return Entity.newBuilder(userKey)
				.set(USERNAME, user.getString(USERNAME))
				.set(EMAIL, user.getString(EMAIL))
				.set(PASSWORD, UserData_Modifiable.hashPassword(new_password))
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
				.set(WEBSITE, user.getString(WEBSITE))
				.set(FACEBOOK, user.getString(FACEBOOK))
				.set(INSTAGRAM, user.getString(INSTAGRAM))
				.set(TWITTER, user.getString(TWITTER))
				.set(N_EVENTS_PARTICIPATED, user.getDouble(N_EVENTS_PARTICIPATED))
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
				.build();
	}
	
	public static Entity remove(Key userKey, Entity user) {
		return Entity.newBuilder(userKey)
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
				.set(ACCOUNT, Account.REMOVED.toString())
				.set(ROLE, user.getString(ROLE))
				.set(STATE, user.getString(STATE))
				.set(PROFILE, user.getString(PROFILE))
				.set(WEBSITE, user.getString(WEBSITE))
				.set(FACEBOOK, user.getString(FACEBOOK))
				.set(INSTAGRAM, user.getString(INSTAGRAM))
				.set(TWITTER, user.getString(TWITTER))
				.set(N_EVENTS_PARTICIPATED, user.getDouble(N_EVENTS_PARTICIPATED))
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
				.build();
	}
	
	public static Entity setState(String state, Key userKey, Entity user) {
		return Entity.newBuilder(userKey)
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
				.set(STATE, state)
				.set(PROFILE, user.getString(PROFILE))
				.set(WEBSITE, user.getString(WEBSITE))
				.set(FACEBOOK, user.getString(FACEBOOK))
				.set(INSTAGRAM, user.getString(INSTAGRAM))
				.set(TWITTER, user.getString(TWITTER))
				.set(N_EVENTS_PARTICIPATED, user.getDouble(N_EVENTS_PARTICIPATED))
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
				.build();
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
				.set(WEBSITE, data.getWebsite(user.getString(WEBSITE)))
				.set(FACEBOOK, data.getFacebook(user.getString(FACEBOOK)))
				.set(INSTAGRAM, data.getInstagram(user.getString(INSTAGRAM)))
				.set(TWITTER, data.getTwitter(user.getString(TWITTER)))
				.set(N_EVENTS_PARTICIPATED, user.getDouble(N_EVENTS_PARTICIPATED))
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
				.build();
	}
	
	public static Entity changeRole(String role, Key userKey, Entity user) {
		return Entity.newBuilder(userKey)
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
				.set(ROLE, role)
				.set(STATE, user.getString(STATE))
				.set(PROFILE, user.getString(PROFILE))
				.set(WEBSITE, user.getString(WEBSITE))
				.set(FACEBOOK, user.getString(FACEBOOK))
				.set(INSTAGRAM, user.getString(INSTAGRAM))
				.set(TWITTER, user.getString(TWITTER))
				.set(N_EVENTS_PARTICIPATED, user.getDouble(N_EVENTS_PARTICIPATED))
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
				.build();
	}
	
	public static Entity changeProfilePicture(String data, Key userKey, Entity user) {
		return Entity.newBuilder(userKey)
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
				.set(WEBSITE, user.getString(WEBSITE))
				.set(FACEBOOK, user.getString(FACEBOOK))
				.set(INSTAGRAM, user.getString(INSTAGRAM))
				.set(TWITTER, user.getString(TWITTER))
				.set(N_EVENTS_PARTICIPATED, user.getDouble(N_EVENTS_PARTICIPATED))
				.set(TOTAL_CURRENCY, user.getDouble(TOTAL_CURRENCY))
				.set(CURRENT_CURRENCY, user.getDouble(CURRENT_CURRENCY))
				.set(DONATIONS, user.getList(DONATIONS))
				.set(PROFILE_PICTURE_MINIATURE, StringValue.newBuilder(data)
						.setExcludeFromIndexes(true)
						.build())
				.set(EVENTS, user.getList(EVENTS))
				.set(EVENTS_PARTICIPATING, user.getList(EVENTS_PARTICIPATING))
				.set(ROUTES, user.getList(ROUTES))
				.set(ROUTES_PARTICIPATING, user.getList(ROUTES_PARTICIPATING))
				.build();
	}
	
	public static Entity createID(String username, String email, Key usernameKey) {
		return Entity.newBuilder(usernameKey)
				.set(USERNAME, username)
				.set(EMAIL, email)
				.build();
	}
	
	public static List<String> getEvents(Entity user) {
		List<String> events = new LinkedList<>();
		List<Value<?>> event_list = user.getList(EVENTS);
		event_list.forEach(event -> {
			String event_id = (String) event.get();
			events.add(event_id);
		});
		
		return events;
	}
	
	public static List<String> getRoutes(Entity user) {
		List<String> routes = new LinkedList<>();
		List<Value<?>> route_list = user.getList(ROUTES);
		route_list.forEach(route -> {
			String route_id = (String) route.get();
			routes.add(route_id);
		});
		
		return routes;
	}
	
	public static List<String> getParticipatingEvents(Entity user) {
		List<String> participating_events = new LinkedList<>();
		List<Value<?>> event_list = user.getList(EVENTS_PARTICIPATING);
		event_list.forEach(event -> {
			String event_id = (String) event.get();
			participating_events.add(event_id);
		});
		
		return participating_events;
	}
	
	public static List<String> getParticipatingRoutes(Entity user) {
		List<String> participating_routes = new LinkedList<>();
		List<Value<?>> routes_list = user.getList(ROUTES_PARTICIPATING);
		routes_list.forEach(route -> {
			String route_id = (String) route.get();
			participating_routes.add(route_id);
		});
		
		return participating_routes;
	}
	
	private static Entity updateEventList(Key userKey, Entity user, ListValue events_list) {
		return Entity.newBuilder(userKey)
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
				.set(WEBSITE, user.getString(WEBSITE))
				.set(FACEBOOK, user.getString(FACEBOOK))
				.set(INSTAGRAM, user.getString(INSTAGRAM))
				.set(TWITTER, user.getString(TWITTER))
				.set(N_EVENTS_PARTICIPATED, user.getDouble(N_EVENTS_PARTICIPATED))
				.set(TOTAL_CURRENCY, user.getDouble(TOTAL_CURRENCY))
				.set(CURRENT_CURRENCY, user.getDouble(CURRENT_CURRENCY))
				.set(DONATIONS, user.getList(DONATIONS))
				.set(PROFILE_PICTURE_MINIATURE, StringValue.newBuilder(user.getString(PROFILE_PICTURE_MINIATURE))
						.setExcludeFromIndexes(true)
						.build())
				.set(EVENTS, events_list)
				.set(EVENTS_PARTICIPATING, user.getList(EVENTS_PARTICIPATING))
				.set(ROUTES, user.getList(ROUTES))
				.set(ROUTES_PARTICIPATING, user.getList(ROUTES_PARTICIPATING))
				.build();
	}
	
	private static Entity updateParticipatingEventList(Key userKey, Entity user, ListValue participating_list) {
		return Entity.newBuilder(userKey)
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
				.set(WEBSITE, user.getString(WEBSITE))
				.set(FACEBOOK, user.getString(FACEBOOK))
				.set(INSTAGRAM, user.getString(INSTAGRAM))
				.set(TWITTER, user.getString(TWITTER))
				.set(N_EVENTS_PARTICIPATED, user.getDouble(N_EVENTS_PARTICIPATED))
				.set(TOTAL_CURRENCY, user.getDouble(TOTAL_CURRENCY))
				.set(CURRENT_CURRENCY, user.getDouble(CURRENT_CURRENCY))
				.set(DONATIONS, user.getList(DONATIONS))
				.set(PROFILE_PICTURE_MINIATURE, StringValue.newBuilder(user.getString(PROFILE_PICTURE_MINIATURE))
						.setExcludeFromIndexes(true)
						.build())
				.set(EVENTS, user.getList(EVENTS))
				.set(EVENTS_PARTICIPATING, participating_list)
				.set(ROUTES, user.getList(ROUTES))
				.set(ROUTES_PARTICIPATING, user.getList(ROUTES_PARTICIPATING))
				.build();
	}
	
	private static Entity updateRouteList(Key userKey, Entity user, ListValue routes_list) {
		return Entity.newBuilder(userKey)
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
				.set(WEBSITE, user.getString(WEBSITE))
				.set(FACEBOOK, user.getString(FACEBOOK))
				.set(INSTAGRAM, user.getString(INSTAGRAM))
				.set(TWITTER, user.getString(TWITTER))
				.set(PROFILE_PICTURE_MINIATURE, StringValue.newBuilder(user.getString(PROFILE_PICTURE_MINIATURE))
						.setExcludeFromIndexes(true)
						.build())
				.set(EVENTS, user.getList(EVENTS))
				.set(EVENTS_PARTICIPATING, user.getList(EVENTS_PARTICIPATING))
				.set(ROUTES, routes_list)
				.set(ROUTES_PARTICIPATING, user.getList(ROUTES_PARTICIPATING))
				.build();
	}
	
	private static Entity updateParticipatingRouteList(Key userKey, Entity user, ListValue participating_list) {
		return Entity.newBuilder(userKey)
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
				.set(WEBSITE, user.getString(WEBSITE))
				.set(FACEBOOK, user.getString(FACEBOOK))
				.set(INSTAGRAM, user.getString(INSTAGRAM))
				.set(TWITTER, user.getString(TWITTER))
				.set(PROFILE_PICTURE_MINIATURE, StringValue.newBuilder(user.getString(PROFILE_PICTURE_MINIATURE))
						.setExcludeFromIndexes(true)
						.build())
				.set(EVENTS, user.getList(EVENTS))
				.set(EVENTS_PARTICIPATING, user.getList(EVENTS_PARTICIPATING))
				.set(ROUTES, user.getList(ROUTES))
				.set(ROUTES_PARTICIPATING, participating_list)
				.build();
	}
	
	public static Entity addEvent(Key userKey, Entity user, String event_id) {
		
		List<String> events = getEvents(user);
		if(events.contains(event_id))
			return user;

		ListValue.Builder events_list = ListValue.newBuilder().set(user.getList(EVENTS));
		events_list.addValue(event_id);
		
		return updateEventList(userKey, user, events_list.build());
	}
	
	public static Entity removeEvent(Key userKey, Entity user, String event_id) throws InexistentEventException {
		List<String> events = getEvents(user);
		if(!events.contains(event_id))
			throw new InexistentEventException();
		
		ListValue.Builder events_list = ListValue.newBuilder();
		
		events.remove(event_id);
		events.forEach(event -> events_list.addValue(event));

		return updateEventList(userKey, user, events_list.build());
	}
	
	public static Entity participateEvent(Key userKey, Entity user, String event_id) throws ImpossibleActionException {
		
		List<String> events = getParticipatingEvents(user);
		if(events.contains(event_id))
			throw new ImpossibleActionException("User already participating in event: " + event_id);

		ListValue.Builder events_list = ListValue.newBuilder().set(user.getList(EVENTS_PARTICIPATING));
		events_list.addValue(event_id);
		
		return updateParticipatingEventList(userKey, user, events_list.build());
	}
	
	public static Entity addRoute(Key userKey, Entity user, String route_id) {
		
		List<String> routes = getRoutes(user);
		if(routes.contains(route_id))
			return user;

		ListValue.Builder routes_list = ListValue.newBuilder().set(user.getList(ROUTES));
		routes_list.addValue(route_id);
		
		return updateRouteList(userKey, user, routes_list.build());
	}
	
	public static Entity removeRoute(Key userKey, Entity user, String route_id) throws InexistentRouteException {
		List<String> routes = getRoutes(user);
		if(!routes.contains(route_id))
			throw new InexistentRouteException();
		
		ListValue.Builder routes_list = ListValue.newBuilder();
		
		routes.remove(route_id);
		routes.forEach(route -> routes_list.addValue(route));

		return updateRouteList(userKey, user, routes_list.build());
	}
	
	public static Entity participateRoute(Key userKey, Entity user, String route_id) throws ImpossibleActionException {
		
		List<String> route_ids = getParticipatingRoutes(user);
		if(route_ids.contains(route_id))
			throw new ImpossibleActionException("User already participating in route: " + route_id);

		ListValue.Builder routes_list = ListValue.newBuilder().set(user.getList(ROUTES_PARTICIPATING));
		routes_list.addValue(route_id);
		
		return updateParticipatingRouteList(userKey, user, routes_list.build());
	}
	
	public static Entity leaveEvent(Key userKey, Entity user, String event_id) throws InexistentEventException {
		List<String> events = getParticipatingEvents(user);
		if(!events.contains(event_id))
			throw new InexistentEventException();
		
		ListValue.Builder events_list = ListValue.newBuilder();
		
		events.remove(event_id);
		events.forEach(event -> events_list.addValue(event));

		return updateParticipatingEventList(userKey, user, events_list.build());
	}
	
	public static Entity createNew(String email, String username, String password, Key userKey) {
		UserData_AllProperties data = new UserData_AllProperties(new RegisterData(email, username, password));
		ListValue empty_list = ListValue.newBuilder().build();
		
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
				.set(WEBSITE, data.website)
				.set(FACEBOOK, data.facebook)
				.set(INSTAGRAM, data.instagram)
				.set(TWITTER, data.twitter)
				.set(TOTAL_CURRENCY, 0)
				.set(CURRENT_CURRENCY, 0)
				.set(N_EVENTS_PARTICIPATED, 0)
				.set(DONATIONS, empty_list)
				.set(PROFILE_PICTURE_MINIATURE, StringValue.newBuilder(data.profile_pic)
						.setExcludeFromIndexes(true)
						.build())
				.set(EVENTS, empty_list)
				.set(EVENTS_PARTICIPATING, empty_list)
				.set(ROUTES, empty_list)
				.set(ROUTES_PARTICIPATING, empty_list)
				.build();
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
}
