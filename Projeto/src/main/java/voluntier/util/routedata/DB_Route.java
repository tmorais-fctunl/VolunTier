package voluntier.util.routedata;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.LatLng;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.Value;

import voluntier.exceptions.AlreadyExistsException;
import voluntier.exceptions.CannotParticipateInSomeEventsException;
import voluntier.exceptions.IllegalCoordinatesException;
import voluntier.exceptions.ImpossibleActionException;
import voluntier.exceptions.InexistentElementException;
import voluntier.exceptions.InexistentEventException;
import voluntier.exceptions.InexistentParticipantException;
import voluntier.exceptions.InexistentRouteException;
import voluntier.exceptions.InexistentUserException;
import voluntier.exceptions.MaximumSizeReachedException;
import voluntier.exceptions.RouteAlreadyExistsException;
import voluntier.exceptions.SomethingWrongException;
import voluntier.util.DB_Util;
import voluntier.util.GeoHashUtil;
import voluntier.util.GoogleStorageUtil;
import voluntier.util.consumes.route.CreateRouteData;
import voluntier.util.eventdata.DB_Event;
import voluntier.util.eventdata.chatdata.DB_Chat;
import voluntier.util.produces.DownloadEventPictureReturn;
import voluntier.util.produces.DownloadSignedURLReturn;
import voluntier.util.userdata.State;

public class DB_Route {

	public static final String ID = "route_id";
	public static final String EVENT_IDS = "route_events";
	public static final String PICTURES = "route_pictures";
	public static final String RATING_ID = "route_rating_id";
	public static final String CHAT_ID = "chat_id";
	public static final String CREATOR = "route_creator";
	public static final String CREATION_DATE = "route_creation_date";
	public static final String PARTICIPANTS = "route_participants";
	public static final String NUM_PARTICIPANTS = "route_num_participants";
	public static final String GEOHASH = "route_geohash";
	public static final String STATE = "route_state";

	public static final int MAX_DESCRIPTION_SIZE = 500;

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory routesFactory = datastore.newKeyFactory().setKind("Route");

	private static DB_Util util = new DB_Util((e, builder) -> {
		builder = Entity.newBuilder(e.getKey())
				.set(ID, e.getString(ID))
				.set(EVENT_IDS, e.getList(EVENT_IDS))
				.set(GEOHASH, e.getString(GEOHASH))
				.set(CREATOR, e.getString(CREATOR))
				.set(CREATION_DATE, e.getTimestamp(CREATION_DATE))
				.set(NUM_PARTICIPANTS, e.getLong(NUM_PARTICIPANTS))
				.set(PICTURES, e.getList(PICTURES))
				.set(PARTICIPANTS, e.getList(PARTICIPANTS))
				.set(RATING_ID, e.getString(RATING_ID))
				.set(RATING_ID, e.getString(RATING_ID))
				.set(STATE, e.getString(STATE));
	});

	private static Key generateRouteKey(List<String> event_ids) throws RouteAlreadyExistsException {

		String id = "";
		for (String event : event_ids)
			id += event;

		Key key = routesFactory.newKey(id);

		if (datastore.get(key) != null)
			throw new RouteAlreadyExistsException("10: A route with the same events in this order already exists");

		return key;
	}

	public static Pair<List<Entity>, String> createNew(CreateRouteData create_event_data)
			throws IllegalCoordinatesException, RouteAlreadyExistsException, InexistentEventException,
			ImpossibleActionException {

		String first_event_id = create_event_data.event_ids.get(0);
		Entity first_event = DB_Event.getEvent(first_event_id);
		LatLng first_event_location = first_event.getLatLng(DB_Event.LOCATION);

		String geohash = GeoHashUtil.convertCoordsToGeoHashHighPrecision(first_event_location.getLatitude(),
				first_event_location.getLongitude());

		for (String e : create_event_data.event_ids) {
			if (!e.equals(first_event_id)) {
				Entity event = DB_Event.getEvent(e);
				DB_Event.checkNotEnded(event);
			}
		}

		ListValue.Builder participants = ListValue.newBuilder();

		Pair<List<Entity>, String> chat = DB_Chat.createNew(create_event_data.email);
		String chat_id = chat.getValue1();
		List<Entity> entities = chat.getValue0();

		Key routeKey = generateRouteKey(create_event_data.event_ids);
		String route_id = routeKey.getName();

		ListValue.Builder pictures = ListValue.newBuilder();

		Timestamp creation_date = Timestamp.now();

		int num_participants = 0;

		String creator = create_event_data.email;

		String rating_id = "";
		// int rating_sum = 0;
		// int rating_num = 0;
		// ListValue.Builder rated_participants = ListValue.newBuilder();

		entities.add(Entity.newBuilder(routeKey).set(ID, route_id).set(GEOHASH, geohash).set(CREATOR, creator)
				.set(CREATION_DATE, creation_date).set(NUM_PARTICIPANTS, num_participants)
				.set(PICTURES, pictures.build()).set(PARTICIPANTS, participants.build()).set(RATING_ID, rating_id)
				.set(RATING_ID, chat_id).set(STATE, State.ENABLED.toString()).build());

		return new Pair<>(entities, route_id);
	}

	public static Entity getRoute(String route_id) throws InexistentRouteException {
		Key routeKey = routesFactory.newKey(route_id);
		Entity route = datastore.get(routeKey);

		if (route == null)
			throw new InexistentRouteException("11: No route with id: " + route_id);

		return route;
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

	public static List<Entity> getEventsInRoute(Entity route) throws InexistentEventException {
		List<String> event_ids = DB_Util.getStringList(route, EVENT_IDS);
		List<Entity> event_entities = new LinkedList<>();

		for (String id : event_ids)
			event_entities.add(DB_Event.getEvent(id));

		return event_entities;
	}

	public static List<Entity> participate(String route_id, String user_email)
			throws InexistentRouteException, InexistentEventException, AlreadyExistsException, 
			ImpossibleActionException, InexistentUserException, CannotParticipateInSomeEventsException {
		Entity route = getRoute(route_id);

		if (DB_Util.existsInStringList(route, PARTICIPANTS, user_email))
			throw new AlreadyExistsException("User already in this route");

		List<Entity> events = getEventsInRoute(route);
		boolean canParticipate = true;

		for (Entity event : events)
			if (!DB_Event.isActive(event) || !DB_Event.isFull(event) || !DB_Event.isPublic(event))
				canParticipate = false;

		List<Entity> ents = new LinkedList<>();

		if (canParticipate) {
			for(Entity event : events) {
				List<Entity> updated_event_and_user = DB_Event.participateInEvent(event.getString(DB_Event.ID),
						user_email);
				updated_event_and_user.forEach(e -> ents.add(event));
			}

			route = util.addUniqueStringToList(route, PARTICIPANTS, user_email);

			ents.add(route);
			return ents;
			
		} else
			throw new CannotParticipateInSomeEventsException("Some events are full or removed");
	}
	
	public static Pair<Entity, String> addPicture(String event_id, String req_email) throws ImpossibleActionException,
			MaximumSizeReachedException, InexistentRouteException, SomethingWrongException, InexistentParticipantException {
		Entity route = getRoute(event_id);
		checkIsActive(route);
		checkIsParticipant(route, req_email);
		
		try {
			String new_pic_id = generateNewPictureID(route);
			PictureData pic = new PictureData(new_pic_id, req_email);

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

		PictureData request_picture_delete = new PictureData(pic_id, req_email);

		try {
			route = util.removeJsonFromList(route, PICTURES, request_picture_delete);
			return route;
		} catch (InexistentElementException e) {
			throw new ImpossibleActionException("The pair " + req_email + " + " + pic_id + " does not exist");
		}
	}

	public static List<DownloadEventPictureReturn> getPicturesURLs(String route) throws InexistentRouteException {
		Entity event = getRoute(route);
		return getPicturesURLs(event);
	}

	public static List<DownloadEventPictureReturn> getPicturesURLs(Entity route) {
		List<PictureData> pictures = DB_Util.getJsonList(route, PICTURES, PictureData.class);
		List<DownloadEventPictureReturn> download_urls = new LinkedList<>();

		pictures.forEach(picture -> {
			Pair<URL, Long> url = GoogleStorageUtil.signURLForDownload(picture.picture_id);
			DownloadSignedURLReturn dwld_url = new DownloadSignedURLReturn(url.getValue0(), url.getValue1());
			download_urls.add(new DownloadEventPictureReturn(dwld_url, picture.picture_id));
		});

		return download_urls;
	}

	public static Entity giveRating(String route_id, String user_email, float rating)
			throws InexistentRouteException, InexistentParticipantException {
		Entity route = getRoute(route_id);
		checkIsParticipant(route, user_email);

		// String rating_id = route.getSting(RATING_ID);
		// Entity rating = DB_Rating.giveRating(rating_id, user_email, rating);
		// return rating;

		return null;
	}

	public static void checkIsParticipant(Entity route, String email) throws InexistentParticipantException {
		if(!DB_Util.existsInStringList(route, PARTICIPANTS, email))
			throw new InexistentParticipantException("1: User " + email + " does not participate in this route: " + route.getString(ID));
		
	}

	public static void checkIsActive(Entity route) throws ImpossibleActionException {
		if (!isActive(route))
			throw new ImpossibleActionException("2: Route not active: " + route.getString(ID));
	}

	public static boolean isActive(Entity route) {
		return route.getString(STATE).equals(State.ENABLED.toString());
	}

}
