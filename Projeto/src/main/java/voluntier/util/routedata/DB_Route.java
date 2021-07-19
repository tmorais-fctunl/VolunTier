package voluntier.util.routedata;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.LatLng;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.Value;
import com.google.datastore.v1.QueryResultBatch.MoreResultsType;

import voluntier.exceptions.AlreadyExistsException;
import voluntier.exceptions.CannotParticipateInSomeEventsException;
import voluntier.exceptions.IllegalCoordinatesException;
import voluntier.exceptions.ImpossibleActionException;
import voluntier.exceptions.InexistentChatIdException;
import voluntier.exceptions.InexistentElementException;
import voluntier.exceptions.InexistentEventException;
import voluntier.exceptions.InexistentLogIdException;
import voluntier.exceptions.InexistentMessageIdException;
import voluntier.exceptions.InexistentModeratorException;
import voluntier.exceptions.InexistentParticipantException;
import voluntier.exceptions.InexistentRatingException;
import voluntier.exceptions.InexistentRouteException;
import voluntier.exceptions.InexistentUserException;
import voluntier.exceptions.InvalidCursorException;
import voluntier.exceptions.MaximumSizeReachedException;
import voluntier.exceptions.RouteAlreadyExistsException;
import voluntier.exceptions.SomethingWrongException;
import voluntier.util.DB_Util;
import voluntier.util.GeoHashUtil;
import voluntier.util.GoogleStorageUtil;
import voluntier.util.JsonUtil;
import voluntier.util.chatdata.DB_Chat;
import voluntier.util.consumes.route.CreateRouteData;
import voluntier.util.eventdata.DB_Event;
import voluntier.util.eventdata.ParticipantDataReturn;
import voluntier.util.produces.ChatReturn;
import voluntier.util.produces.DownloadEventPictureReturn;
import voluntier.util.produces.DownloadSignedURLReturn;
import voluntier.util.produces.SearchEventReturn;
import voluntier.util.rating.DB_Rating;
import voluntier.util.userdata.DB_User;
import voluntier.util.userdata.State;
import voluntier.util.userdata.ParticipantStatus;

public class DB_Route {

	public static final String ID = "route_id";
	public static final String EVENTS = "route_events";
	public static final String PICTURES = "route_pictures";
	public static final String RATING_ID = "route_rating_id";
	public static final String CHAT_ID = "chat_id";
	public static final String CREATOR = "route_creator";
	public static final String CREATION_DATE = "route_creation_date";
	public static final String PARTICIPANTS = "route_participants";
	public static final String NUM_PARTICIPANTS = "route_num_participants";
	public static final String GEOHASH = "route_geohash";
	public static final String STATE = "route_state";
	public static final String DESCRIPTION = "route_description";
	public static final String NAME = "route_name";

	public static final int MAX_NAME_SIZE = 100;
	public static final int MAX_DESCRIPTION_SIZE = 1000;
	public static final int MAX_PARTICIPANTS_RETURN = 5;

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory routesFactory = datastore.newKeyFactory().setKind("Route");

	private static DB_Util util = new DB_Util(DB_Route::defaultBuilder);

	private static void defaultBuilder(Entity e) {
		util.builder = Entity.newBuilder(e.getKey())
				.set(ID, e.getString(ID))
				.set(EVENTS, e.getList(EVENTS))
				.set(GEOHASH, e.getString(GEOHASH))
				.set(CREATOR, e.getString(CREATOR))
				.set(DESCRIPTION, e.getString(DESCRIPTION))
				.set(NAME, e.getString(NAME))
				.set(CREATION_DATE, e.getTimestamp(CREATION_DATE))
				.set(PICTURES, e.getList(PICTURES))
				.set(PARTICIPANTS, e.getList(PARTICIPANTS))
				.set(RATING_ID, e.getString(RATING_ID))
				.set(CHAT_ID, e.getString(CHAT_ID))
				.set(NUM_PARTICIPANTS, e.getLong(NUM_PARTICIPANTS))
				.set(STATE, e.getString(STATE));
	}
	
	public static List<Entity> REWRITE(Entity route) {
		List<Entity> entities = new LinkedList<>();
		entities.add(Entity.newBuilder(route.getKey())
		.set(ID, route.getString(ID))
		.set(EVENTS, route.getList(EVENTS))
		.set(GEOHASH, route.getString(GEOHASH))
		.set(CREATOR, route.getString(CREATOR))
		.set(DESCRIPTION, route.getString(DESCRIPTION))
		.set(NAME, route.getString(NAME))
		.set(CREATION_DATE, route.getTimestamp(CREATION_DATE))
		.set(PICTURES, route.getList(PICTURES))
		.set(PARTICIPANTS, route.getList(PARTICIPANTS))
		.set(RATING_ID, route.getString(RATING_ID))
		.set(CHAT_ID, route.getString(CHAT_ID))
		.set(NUM_PARTICIPANTS, route.getLong(NUM_PARTICIPANTS))
		.set(STATE, route.getString(STATE))
		.build());
		
		return entities;
	}

	private static Key generateRouteKey(List<String> event_ids) throws RouteAlreadyExistsException {

		String id = "";
		for (String event : event_ids)
			id += event;

		Key key = routesFactory.newKey(id);

		if (datastore.get(key) != null)
			throw new RouteAlreadyExistsException("10: A route with the same events in this order already exists");

		return key;
	}

	private static String generateNewPictureID(Entity route) {
		String id = route.getString(ID);
		int number;

		List<Value<?>> pictures = route.getList(PICTURES);
		if (pictures.size() > 0)
			number = (Integer.parseInt(((String) pictures.get(pictures.size() - 1).get()).split(id + "-")[1]) + 1);
		else
			number = 1;

		return id + "-" + number;
	}

	public static Pair<List<Entity>, String> createNew(CreateRouteData create_route_data)
			throws IllegalCoordinatesException, RouteAlreadyExistsException, InexistentEventException,
			ImpossibleActionException, InexistentUserException {

		String first_event_id = create_route_data.event_ids.get(0);
		Entity first_event = DB_Event.getEvent(first_event_id);
		LatLng first_event_location = first_event.getLatLng(DB_Event.LOCATION);

		String geohash = GeoHashUtil.convertCoordsToGeoHashHighPrecision(first_event_location.getLatitude(),
				first_event_location.getLongitude());

		for (String e : create_route_data.event_ids) {
			if (!e.equals(first_event_id)) {
				Entity event = DB_Event.getEvent(e);
				DB_Event.checkNotEnded(event);
			}
		}

		ListValue.Builder participants = ListValue.newBuilder();

		Pair<List<Entity>, String> chat = DB_Chat.createNew(create_route_data.email);
		String chat_id = chat.getValue1();
		List<Entity> entities = chat.getValue0();

		Key routeKey = generateRouteKey(create_route_data.event_ids);
		String route_id = routeKey.getName();

		ListValue.Builder pictures = ListValue.newBuilder();

		Timestamp creation_date = Timestamp.now();

		String creator = create_route_data.email;
		
		ListValue.Builder events = ListValue.newBuilder();
		for(String id : create_route_data.event_ids){
			Entity event = DB_Event.getEvent(id);
			events.addValue(JsonUtil.json.toJson(new SearchEventReturn(event)));
		}

		Pair<Entity, String> rating = DB_Rating.createNew();
		String rating_id = rating.getValue1();
		Entity rating_ent = rating.getValue0();

		entities.add(rating_ent);

		entities.add(Entity.newBuilder(routeKey)
				.set(ID, route_id)
				.set(EVENTS, events.build())
				.set(GEOHASH, geohash)
				.set(CREATOR, creator)
				.set(NAME, create_route_data.route_name)
				.set(DESCRIPTION, create_route_data.description)
				.set(CREATION_DATE, creation_date)
				.set(PICTURES, pictures.build())
				.set(PARTICIPANTS, participants.build())
				.set(RATING_ID, rating_id)
				.set(CHAT_ID, chat_id)
				.set(NUM_PARTICIPANTS, 0)
				.set(STATE, State.ENABLED.toString())
				.build());
		
		Entity user = DB_User.getUser(create_route_data.email);
		Entity updated_user = DB_User.addRoute(user.getKey(), user, route_id);
		entities.add(updated_user);
		
		return new Pair<>(entities, route_id);
	}

	public static Entity getRoute(String route_id) throws InexistentRouteException {
		Key routeKey = routesFactory.newKey(route_id);
		Entity route = datastore.get(routeKey);

		if (route == null)
			throw new InexistentRouteException("11: No route with id: " + route_id);

		return route;
	}

	private static List<Entity> getEventsInRoute(Entity route) throws InexistentEventException {
		List<SearchEventReturn> event_ids = DB_Util.getJsonList(route, EVENTS, SearchEventReturn.class);
		List<Entity> event_entities = new LinkedList<>();

		for (SearchEventReturn e : event_ids)
			event_entities.add(DB_Event.getEvent(e.event_id));

		return event_entities;
	}

	public static List<Entity> participate(String route_id, String user_email)
			throws InexistentRouteException, InexistentEventException, AlreadyExistsException,
			ImpossibleActionException, InexistentUserException, CannotParticipateInSomeEventsException {
		Entity route = getRoute(route_id);

		if (DB_Util.existsInStringList(route, PARTICIPANTS, user_email))
			throw new AlreadyExistsException("User is already apart of this route");

		List<Entity> events = getEventsInRoute(route);
		boolean canParticipate = true;

		for (Entity event : events)
			if (!DB_Event.isActive(event) || DB_Event.isFull(event) || !DB_Event.isPublic(event))
				canParticipate = false;

		List<Entity> ents = new LinkedList<>();

		if (canParticipate) {
			for (Entity event : events) {
				List<Entity> updated_event_and_user = DB_Event.participateInEvent(event.getString(DB_Event.ID),
						user_email, false);
				Entity updated_event = updated_event_and_user.get(0);
				Entity updated_user = updated_event_and_user.get(1);
				updated_user = DB_User.participateRoute(updated_user.getKey(), updated_user, route_id);
				ents.add(updated_event);
				ents.add(updated_user);
			}

			route = util.addUniqueStringToList(route, PARTICIPANTS, user_email);

			ents.add(route);
			return ents;

		} else
			throw new CannotParticipateInSomeEventsException("Some events are full or removed or private");
	}

	public static Pair<Entity, String> addPicture(String event_id, String req_email)
			throws ImpossibleActionException, MaximumSizeReachedException, InexistentRouteException,
			SomethingWrongException, InexistentParticipantException {
		Entity route = getRoute(event_id);
		checkIsActive(route);
		checkIsParticipant(route, req_email);

		try {
			String new_pic_id = generateNewPictureID(route);
			PictureData pic = new PictureData(new_pic_id, req_email, Timestamp.now().toString());

			route = util.addUniqueJsonToList(route, PICTURES, pic);

			return new Pair<>(route, new_pic_id);
		} catch (AlreadyExistsException e) {
			throw new SomethingWrongException("The unique picture id was already in use");
		}
	}

	public static Entity deletePicture(String route_id, String pic_id, String req_email)
			throws ImpossibleActionException, InexistentRouteException, InexistentParticipantException {
		Entity route = getRoute(route_id);
		checkIsActive(route);
		checkIsParticipant(route, req_email);

		try {
			route = util.removeJsonFromList(route, PICTURES,
					(pic -> pic.picture_id.equals(pic_id) && pic.author.equals(req_email)), PictureData.class);
			return route;
		} catch (InexistentElementException e) {
			throw new ImpossibleActionException("The pair " + req_email + " + " + pic_id + " does not exist");
		}
	}

	public static List<DownloadEventPictureReturn> getPicturesDownloadURLs(String route)
			throws InexistentRouteException {
		Entity event = getRoute(route);
		return getPicturesDownloadURLs(event);
	}

	public static List<DownloadEventPictureReturn> getPicturesDownloadURLs(Entity route) {
		List<PictureData> pictures = DB_Util.getJsonList(route, PICTURES, PictureData.class);
		List<DownloadEventPictureReturn> download_urls = new LinkedList<>();

		pictures.forEach(picture -> {
			Pair<URL, Long> url = GoogleStorageUtil.signURLForDownload(picture.picture_id);
			DownloadSignedURLReturn dwld_url = new DownloadSignedURLReturn(url.getValue0(), url.getValue1());
			download_urls.add(new DownloadEventPictureReturn(dwld_url, picture.picture_id, picture.timestamp));
		});

		return download_urls;
	}

	public static Triplet<List<ParticipantDataReturn>, Integer, MoreResultsType> getParticipants(String route_id,
			int cursor, String user_email) throws InexistentChatIdException, InexistentUserException,
			ImpossibleActionException, InexistentRouteException {

		Entity route = getRoute(route_id);

		List<String> user_emails = DB_Util.getStringList(route, PARTICIPANTS);
		List<ParticipantDataReturn> participant_roles = new LinkedList<>();

		int i = 0;
		int counter = 0;
		for (String participant_email : user_emails) {
			if (counter + 1 > MAX_PARTICIPANTS_RETURN)
				break;
			if (++i <= cursor)
				continue;

			++counter;

			Entity user = DB_User.getUser(participant_email);
			String encodedPicture = user.getString(DB_User.PROFILE_PICTURE_MINIATURE);
			String username = user.getString(DB_User.USERNAME);

			ParticipantStatus status = getStatus(route, participant_email);
			participant_roles
					.add(new ParticipantDataReturn(participant_email, username, encodedPicture, status.toString()));
		}

		int new_cursor = i;
		boolean more_results = new_cursor < user_emails.size();
		return new Triplet<>(participant_roles, more_results ? new_cursor : null,
				more_results ? MoreResultsType.MORE_RESULTS_AFTER_LIMIT : MoreResultsType.NO_MORE_RESULTS);
	}

	public static Pair<List<Entity>, Integer> postComment(String route_id, String email, String username,
			String comment) throws InexistentChatIdException, InexistentLogIdException, SomethingWrongException,
			InexistentRouteException, ImpossibleActionException, InexistentParticipantException {

		Entity event = getRoute(route_id);

		checkIsActive(event);
		checkIsParticipant(event, email);

		String chat_id = event.getString(CHAT_ID);
		return DB_Chat.postMessage(chat_id, email, username, comment);
	}

	public static Entity deleteComment(String route_id, int comment_id, String email)
			throws InexistentRouteException, ImpossibleActionException, InexistentChatIdException,
			InexistentLogIdException, InexistentMessageIdException {

		Entity event = getRoute(route_id);
		checkIsActive(event);

		String chat_id = event.getString(CHAT_ID);
		return DB_Chat.deleteMessage(chat_id, comment_id, email);
	}

	public static Entity updateComment(String route_id, int comment_id, String user_email, String comment_content)
			throws InexistentRouteException, ImpossibleActionException, InexistentChatIdException,
			InexistentLogIdException, InexistentMessageIdException {

		Entity event = getRoute(route_id);
		checkIsActive(event);

		String chat_id = event.getString(CHAT_ID);
		return DB_Chat.editMessage(chat_id, comment_id, user_email, comment_content);
	}

	public static Entity giveOrRemoveLikeInComment(String route_id, int comment_id, String req_email)
			throws InexistentRouteException, ImpossibleActionException, InexistentParticipantException,
			InexistentChatIdException, InexistentLogIdException, InexistentMessageIdException,
			InexistentRatingException {

		Entity event = getRoute(route_id);
		checkIsActive(event);
		checkIsParticipant(event, req_email);

		String chat_id = event.getString(CHAT_ID);
		return DB_Chat.giveOrRemoveLikeInMessage(chat_id, comment_id, req_email);
	}

	public static ChatReturn getChat(String route_id, Integer cursor,
			boolean lastest_first, String req_email) throws InexistentRouteException, InexistentChatIdException,
			InvalidCursorException, InexistentLogIdException {

		Entity event = getRoute(route_id);
		// checkIsParticipant(event, req_email);

		String chat_id = event.getString(CHAT_ID);
		return DB_Chat.getChat(chat_id, cursor == null ? 0 : cursor, lastest_first, req_email);
	}

	public static Entity giveRating(String route_id, String user_email, float rating_number)
			throws InexistentRouteException, InexistentParticipantException, InexistentRatingException {
		Entity route = getRoute(route_id);
		checkIsParticipant(route, user_email);

		String rating_id = route.getString(RATING_ID);
		Entity rating = DB_Rating.giveRating(rating_id, rating_number, user_email);

		return rating;
	}

	public static double getAverageRating(String route_id) throws InexistentRouteException, InexistentRatingException {
		Entity route = getRoute(route_id);
		return getAverageRating(route);
	}

	public static double getAverageRating(Entity route) throws InexistentRatingException {

		String rating_id = route.getString(RATING_ID);
		double rating = DB_Rating.getAverageRating(rating_id);

		return rating;
	}

	public static Entity makeChatModerator(String event_id, String req_email, String target_email)
			throws InexistentChatIdException, InexistentParticipantException, ImpossibleActionException,
			InexistentRouteException {

		Entity route = getRoute(event_id);
		checkIsActive(route);
		checkIsParticipant(route, target_email);
		checkIsCreator(route, req_email);

		String chat_id = route.getString(CHAT_ID);
		return DB_Chat.makeModerator(chat_id, target_email, req_email);
	}

	public static Entity removeChatModerator(String event_id, String req_email, String target_email)
			throws InexistentChatIdException, InexistentParticipantException, InexistentModeratorException,
			ImpossibleActionException, InexistentRouteException {

		Entity route = getRoute(event_id);
		checkIsActive(route);
		checkIsParticipant(route, req_email);

		String chat_id = route.getString(CHAT_ID);
		return DB_Chat.removeModerator(chat_id, target_email, req_email);
	}

	public static ParticipantStatus getStatus(Entity route, String user_email) throws InexistentChatIdException {
		if (isCreator(route, user_email))
			return ParticipantStatus.CREATOR;

		List<String> mods = DB_Chat.getModerators(route.getString(CHAT_ID));
		if (mods.contains(user_email))
			return ParticipantStatus.MOD;

		if (isParticipant(route, user_email))
			return ParticipantStatus.PARTICIPANT;
		return ParticipantStatus.NON_PARTICIPANT;
	}

	public static RouteDataReturn getRouteData(String route_id, String user_email)
			throws InexistentRouteException, InexistentRatingException, InexistentChatIdException {
		Entity route = getRoute(route_id);
		RouteDataReturn data = new RouteDataReturn(route, user_email);

		return data;
	}

	public static void checkIsParticipant(Entity route, String email) throws InexistentParticipantException {
		if (!isParticipant(route, email))
			throw new InexistentParticipantException(
					"1: User " + email + " does not participate in this route: " + route.getString(ID));
	}

	public static boolean isParticipant(Entity route, String email) {
		return DB_Util.existsInStringList(route, PARTICIPANTS, email);
	}

	public static void checkIsActive(Entity route) throws ImpossibleActionException {
		if (!isActive(route))
			throw new ImpossibleActionException("2: Route not active: " + route.getString(ID));
	}

	public static void checkIsCreator(Entity route, String email) throws ImpossibleActionException {
		if (!isCreator(route, email))
			throw new ImpossibleActionException("3: User not the creator: " + email);
	}

	public static boolean isCreator(Entity route, String email) {
		return route.getString(CREATOR).equals(email);
	}

	public static boolean isActive(Entity route) {
		return route.getString(STATE).equals(State.ENABLED.toString());
	}

}
