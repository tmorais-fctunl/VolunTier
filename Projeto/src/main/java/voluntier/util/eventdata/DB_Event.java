package voluntier.util.eventdata;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.Value;
import com.google.datastore.v1.QueryResultBatch.MoreResultsType;

import voluntier.exceptions.IllegalCoordinatesException;
import voluntier.exceptions.ImpossibleActionException;
import voluntier.exceptions.InexistentChatIdException;
import voluntier.exceptions.InexistentEventException;
import voluntier.exceptions.InexistentLogIdException;
import voluntier.exceptions.InexistentMessageIdException;
import voluntier.exceptions.InexistentModeratorException;
import voluntier.exceptions.InexistentParticipantException;
import voluntier.exceptions.InexistentPictureException;
import voluntier.exceptions.InexistentUserException;
import voluntier.exceptions.InvalidCursorException;
import voluntier.exceptions.MaximumSizeReachedException;
import voluntier.exceptions.SomethingWrongException;
import voluntier.util.GeoHashUtil;
import voluntier.util.GoogleStorageUtil;
import voluntier.util.consumes.event.CreateEventData;
import voluntier.util.consumes.event.UpdateEventData;
import voluntier.util.eventdata.chatdata.DB_Chat;
import voluntier.util.produces.DownloadEventPictureReturn;
import voluntier.util.produces.DownloadSignedURLReturn;
import voluntier.util.userdata.DB_User;
import voluntier.util.userdata.Profile;
import voluntier.util.userdata.State;
import voluntier.util.userdata.StatusEvent;

public class DB_Event {

	public static final String NAME = "event_name";
	public static final String ID = "event_id";
	public static final String LOCATION = "location";
	public static final String START_DATE = "start_date";
	public static final String END_DATE = "end_date";
	public static final String CREATION_DATE = "creation_date";
	public static final String OWNER_EMAIL = "owner_email";
	public static final String CONTACT = "contact";
	public static final String DESCRIPTION = "description";
	public static final String CATEGORY = "category";
	public static final String CAPACITY = "capacity";
	public static final String CHAT_ID = "chat_id";
	public static final String PARTICIPANTS = "participants";
	public static final String N_PARTICIPANTS = "num_participants";
	public static final String WEBSITE = "event_website";
	public static final String FACEBOOK = "event_facebook";
	public static final String INSTAGRAM = "event_instagram";
	public static final String TWITTER = "event_twitter";
	public static final String GEOHASH = "event_geohash";
	public static final String PRESENCE_CODE = "event_presence_confirmation_code";
	public static final String PRESENCES = "event_presences";
	public static final String PICTURES = "event_pics_id";
	public static final String STATE = "event_state";
	public static final String PROFILE = "event_profile";

	private static final String REQUESTS = "participation_requests";
	private static final String N_REQUESTS = "num_participation_requests";

	public static final String MOBILE_REGEX = "([+][0-9]{2,3}\\s)?[2789][0-9]{8}";

	public static final int MAX_COMMENT_SIZE = 500;
	public static final int MAX_NAME_SIZE = 100;
	public static final int MAX_DSCRIPTION_SIZE = 500;
	public static final long DEFAULT_CAPACITY = 100;

	public static final long MAX_PARTICIPANTS_RETURN = 5;
	public static final long MAX_NUM_PICTURES = 10;

	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory eventFactory = datastore.newKeyFactory().setKind("Event");
	// private static final Logger LOG =
	// Logger.getLogger(RegisterResource.class.getName());
	
	public static Entity REWRITE(Entity event) {

		return Entity.newBuilder(event.getKey()).set(NAME, event.getString(NAME)).set(ID, event.getString(ID))
				.set(LOCATION, event.getLatLng(LOCATION)).set(START_DATE, event.getString(START_DATE))
				.set(END_DATE, event.getString(END_DATE)).set(CREATION_DATE, event.getString(CREATION_DATE))
				.set(CHAT_ID, event.getString(CHAT_ID)).set(PARTICIPANTS, event.getList(PARTICIPANTS))
				.set(N_PARTICIPANTS, event.getLong(N_PARTICIPANTS)).set(OWNER_EMAIL, event.getString(OWNER_EMAIL))
				.set(CONTACT, event.getString(CONTACT)).set(DESCRIPTION, event.getString(DESCRIPTION))
				.set(CATEGORY, event.getString(CATEGORY)).set(CAPACITY, event.getLong(CAPACITY))
				.set(STATE, event.getString(STATE)).set(PROFILE, event.getString(PROFILE))
				.set(WEBSITE, event.getString(WEBSITE)).set(FACEBOOK, event.getString(FACEBOOK))
				.set(INSTAGRAM, event.getString(INSTAGRAM)).set(TWITTER, event.getString(TWITTER))
				.set(GEOHASH, event.getString(GEOHASH)).set(PICTURES, event.getList(PICTURES))
				.set(PRESENCE_CODE, event.getString(PRESENCE_CODE))
				.set(PRESENCES, event.getList(PRESENCES))
				.set(REQUESTS, event.getList(REQUESTS))
				.set(N_REQUESTS, 0)
				// additional properties here
				.build();
	}

	public static Entity updateProperty(UpdateEventData data)
			throws InexistentEventException, ImpossibleActionException, IllegalCoordinatesException {
		Entity event = getEvent(data.event_id);
		checkIsOwner(event, data.email);
		checkIsActive(event);

		return Entity.newBuilder(event.getKey())
				.set(NAME, event.getString(NAME))
				.set(ID, event.getString(ID))
				.set(LOCATION, data.getLocation(event.getLatLng(LOCATION)))
				.set(START_DATE, data.getStartDate(event.getString(START_DATE)))
				.set(END_DATE, data.getEndDate(event.getString(END_DATE)))
				.set(CREATION_DATE, event.getString(CREATION_DATE))
				.set(CHAT_ID, event.getString(CHAT_ID))
				.set(PARTICIPANTS, event.getList(PARTICIPANTS))
				.set(N_PARTICIPANTS, event.getLong(N_PARTICIPANTS))
				.set(OWNER_EMAIL, event.getString(OWNER_EMAIL))
				.set(CONTACT, data.getContact(event.getString(CONTACT)))
				.set(DESCRIPTION, data.getDescription(event.getString(DESCRIPTION)))
				.set(CATEGORY, data.getCategory(event.getString(CATEGORY)))
				.set(CAPACITY, data.getCapacity(event.getLong(CAPACITY)))
				.set(STATE, event.getString(STATE))
				.set(PROFILE, data.getProfile(event.getString(PROFILE)))
				.set(WEBSITE, data.getWebsite(event.getString(WEBSITE)))
				.set(FACEBOOK, data.getFacebook(event.getString(FACEBOOK)))
				.set(INSTAGRAM, data.getInstagram(event.getString(INSTAGRAM)))
				.set(TWITTER, data.getTwitter(event.getString(TWITTER)))
				.set(GEOHASH, data.getGeohash(event.getString(GEOHASH)))
				.set(PICTURES, event.getList(PICTURES))
				.set(PRESENCE_CODE, event.getString(PRESENCE_CODE))
				.set(PRESENCES, event.getList(PRESENCES))
				.set(GEOHASH, data.getGeohash(event.getString(GEOHASH)))
				.set(PICTURES, event.getList(PICTURES))
				.set(REQUESTS, event.getList(REQUESTS))
				.set(N_REQUESTS, event.getLong(N_REQUESTS)).build();
	}

	public static Entity updateState(String event_id, String email, String state)
			throws ImpossibleActionException, InexistentEventException {
		Entity event = getEvent(event_id);
		checkIsOwner(event, email);
		checkIsActive(event);

		return Entity.newBuilder(event.getKey()).set(NAME, event.getString(NAME)).set(ID, event.getString(ID))
				.set(LOCATION, event.getLatLng(LOCATION)).set(START_DATE, event.getString(START_DATE))
				.set(END_DATE, event.getString(END_DATE)).set(CREATION_DATE, event.getString(CREATION_DATE))
				.set(CHAT_ID, event.getString(CHAT_ID)).set(PARTICIPANTS, event.getList(PARTICIPANTS))
				.set(N_PARTICIPANTS, event.getLong(N_PARTICIPANTS)).set(OWNER_EMAIL, event.getString(OWNER_EMAIL))
				.set(CONTACT, event.getString(CONTACT)).set(DESCRIPTION, event.getString(DESCRIPTION))
				.set(CATEGORY, event.getString(CATEGORY)).set(CAPACITY, event.getLong(CAPACITY)).set(STATE, state)
				.set(PROFILE, event.getString(PROFILE)).set(WEBSITE, event.getString(WEBSITE))
				.set(FACEBOOK, event.getString(FACEBOOK)).set(INSTAGRAM, event.getString(INSTAGRAM))
				.set(TWITTER, event.getString(TWITTER)).set(GEOHASH, event.getString(GEOHASH)).set(PICTURES, event.getList(PICTURES))
				.set(PRESENCE_CODE, event.getString(PRESENCE_CODE))
				.set(PRESENCES, event.getList(PRESENCES))
				.set(TWITTER, event.getString(TWITTER)).set(GEOHASH, event.getString(GEOHASH)).set(PICTURES, event.getList(PICTURES))
				.set(REQUESTS, event.getList(REQUESTS)).set(N_REQUESTS, event.getLong(N_REQUESTS)).build();
	}

	public static Entity updateProfile(String event_id, String email, String profile)
			throws ImpossibleActionException, InexistentEventException {
		Entity event = getEvent(event_id);
		checkIsOwner(event, email);
		checkIsActive(event);

		return Entity.newBuilder(event.getKey()).set(NAME, event.getString(NAME)).set(ID, event.getString(ID))
				.set(LOCATION, event.getLatLng(LOCATION)).set(START_DATE, event.getString(START_DATE))
				.set(END_DATE, event.getString(END_DATE)).set(CREATION_DATE, event.getString(CREATION_DATE))
				.set(CHAT_ID, event.getString(CHAT_ID)).set(PARTICIPANTS, event.getList(PARTICIPANTS))
				.set(N_PARTICIPANTS, event.getLong(N_PARTICIPANTS)).set(OWNER_EMAIL, event.getString(OWNER_EMAIL))
				.set(CONTACT, event.getString(CONTACT)).set(DESCRIPTION, event.getString(DESCRIPTION))
				.set(CATEGORY, event.getString(CATEGORY)).set(CAPACITY, event.getLong(CAPACITY))
				.set(STATE, event.getString(STATE)).set(PROFILE, profile).set(WEBSITE, event.getString(WEBSITE))
				.set(FACEBOOK, event.getString(FACEBOOK)).set(INSTAGRAM, event.getString(INSTAGRAM))
				.set(TWITTER, event.getString(TWITTER)).set(GEOHASH, event.getString(GEOHASH)).set(PICTURES, event.getList(PICTURES))
				.set(PRESENCE_CODE, event.getString(PRESENCE_CODE))
				.set(PRESENCES, event.getList(PRESENCES))
				.set(REQUESTS, event.getList(REQUESTS)).set(N_REQUESTS, event.getLong(N_REQUESTS))
				.build();
	}

	public static Triplet<List<Entity>, String, String> createNew (CreateEventData create_event_data) throws IllegalCoordinatesException {
		ListValue.Builder participants = ListValue.newBuilder();
		participants.addValue(create_event_data.email);

		ListValue.Builder requests = ListValue.newBuilder();

		Pair<List<Entity>, String> chat = DB_Chat.createNew(create_event_data.email);
		List<Entity> entities = chat.getValue0();

		Key eventKey = generateEventKey(create_event_data.event_name);
		String event_id = eventKey.getName();

		ListValue.Builder pictures = ListValue.newBuilder();
		String picture_id = generateNewPictureID(null, event_id);
		pictures.addValue(picture_id);

		EventData_Minimal data = new EventData_Minimal(create_event_data);
		LatLng event_location = LatLng.of(data.location[0], data.location[1]);

		String geohash = GeoHashUtil.convertCoordsToGeoHashHighPrecision(data.location[0], data.location[1]);
		
		String confirm_presence_code = generatePresenceCode();
		ListValue.Builder presences = ListValue.newBuilder();

		entities.add(Entity.newBuilder(eventKey).set(NAME, data.name).set(ID, event_id).set(LOCATION, event_location)
				.set(START_DATE, data.start_date).set(END_DATE, data.end_date)
				.set(CREATION_DATE, Timestamp.now().toString()).set(CHAT_ID, chat.getValue1())
				.set(PARTICIPANTS, participants.build()).set(N_PARTICIPANTS, 1).set(OWNER_EMAIL, data.owner_email)
				.set(CONTACT, data.contact).set(DESCRIPTION, data.description).set(CATEGORY, data.category)
				.set(CAPACITY, data.capacity).set(STATE, data.getState().toString())
				.set(PROFILE, data.getProfile().toString()).set(WEBSITE, data.website).set(FACEBOOK, data.facebook)
				.set(INSTAGRAM, data.instagram).set(TWITTER, data.twitter).set(GEOHASH, geohash)
				.set(PICTURES, pictures.build()).set(PRESENCE_CODE, confirm_presence_code)
				.set(PRESENCES, presences.build())
				.set(PICTURES, pictures.build()).set(REQUESTS, requests.build())
				.set(N_REQUESTS, 0).build());

		return new Triplet<>(entities, event_id, picture_id);
	}
	
	private static Entity updatePictures(Entity event, ListValue newPictures) {

		return Entity.newBuilder(event.getKey()).set(NAME, event.getString(NAME)).set(ID, event.getString(ID))
				.set(LOCATION, event.getLatLng(LOCATION)).set(START_DATE, event.getString(START_DATE))
				.set(END_DATE, event.getString(END_DATE)).set(CREATION_DATE, event.getString(CREATION_DATE))
				.set(CHAT_ID, event.getString(CHAT_ID)).set(PARTICIPANTS, event.getList(PARTICIPANTS))
				.set(N_PARTICIPANTS, event.getLong(N_PARTICIPANTS)).set(OWNER_EMAIL, event.getString(OWNER_EMAIL))
				.set(CONTACT, event.getString(CONTACT)).set(DESCRIPTION, event.getString(DESCRIPTION))
				.set(CATEGORY, event.getString(CATEGORY)).set(CAPACITY, event.getLong(CAPACITY))
				.set(STATE, event.getString(STATE)).set(PROFILE, event.getString(PROFILE))
				.set(WEBSITE, event.getString(WEBSITE)).set(FACEBOOK, event.getString(FACEBOOK))
				.set(INSTAGRAM, event.getString(INSTAGRAM)).set(TWITTER, event.getString(TWITTER))
				.set(GEOHASH, event.getString(GEOHASH)).set(PICTURES, newPictures)
				.set(PRESENCE_CODE, event.getString(PRESENCE_CODE)).set(PRESENCES, event.getList(PRESENCES))
				.set(REQUESTS, event.getList(REQUESTS)).set(N_REQUESTS, event.getLong(N_REQUESTS)).build();
	}
	
	private static Entity updatePresenceList(Entity event, ListValue presences) {

		return Entity.newBuilder(event.getKey()).set(NAME, event.getString(NAME)).set(ID, event.getString(ID))
				.set(LOCATION, event.getLatLng(LOCATION)).set(START_DATE, event.getString(START_DATE))
				.set(END_DATE, event.getString(END_DATE)).set(CREATION_DATE, event.getString(CREATION_DATE))
				.set(CHAT_ID, event.getString(CHAT_ID)).set(PARTICIPANTS, event.getList(PARTICIPANTS))
				.set(N_PARTICIPANTS, event.getLong(N_PARTICIPANTS)).set(OWNER_EMAIL, event.getString(OWNER_EMAIL))
				.set(CONTACT, event.getString(CONTACT)).set(DESCRIPTION, event.getString(DESCRIPTION))
				.set(CATEGORY, event.getString(CATEGORY)).set(CAPACITY, event.getLong(CAPACITY))
				.set(STATE, event.getString(STATE)).set(PROFILE, event.getString(PROFILE))
				.set(WEBSITE, event.getString(WEBSITE)).set(FACEBOOK, event.getString(FACEBOOK))
				.set(INSTAGRAM, event.getString(INSTAGRAM)).set(TWITTER, event.getString(TWITTER))
				.set(GEOHASH, event.getString(GEOHASH)).set(PICTURES, event.getList(PICTURES))
				.set(PRESENCE_CODE, event.getString(PRESENCE_CODE))
				.set(PRESENCES, presences).set(REQUESTS, event.getList(REQUESTS)).set(N_REQUESTS, event.getLong(N_REQUESTS)).build();
	}

	public static Pair<Entity, String> addPicture(String event_id, String req_email)
			throws InexistentEventException, ImpossibleActionException, MaximumSizeReachedException {
		Entity event = getEvent(event_id);
		checkIsOwner(event, req_email);
		checkIsActive(event);

		String new_pic_id = generateNewPictureID(event, null);

		List<Value<?>> pics = event.getList(PICTURES);
		if (pics.size() >= MAX_NUM_PICTURES)
			throw new MaximumSizeReachedException("This event cannot have anymore pictures " + event_id);

		ListValue.Builder newList = ListValue.newBuilder().set(pics);
		newList.addValue(new_pic_id);

		return new Pair<>(updatePictures(event, newList.build()), new_pic_id);
	}

	public static Entity deletePicture(String event_id, String pic_id, String req_email)
			throws ImpossibleActionException, InexistentEventException, InexistentPictureException {
		Entity event = getEvent(event_id);
		checkIsOwner(event, req_email);
		checkIsActive(event);

		List<String> pictures = getPicturesList(event);
		if (!pictures.contains(pic_id))
			throw new InexistentPictureException("12: There is no picture with id:" + pic_id + " for this event");

		pictures.remove(pic_id);

		ListValue.Builder newList = ListValue.newBuilder();
		pictures.forEach(pic -> newList.addValue(pic));

		return updatePictures(event, newList.build());
	}

	private static List<String> getPicturesList(Entity event) {
			List<Value<?>> pictures = event.getList(PICTURES);
			List<String> picture_ids = new LinkedList<>();

			pictures.forEach(pic -> picture_ids.add((String) pic.get()));

			return picture_ids;
	}

	public static List<DownloadEventPictureReturn> getPicturesURLs(String event_id) throws InexistentEventException {
		Entity event = getEvent(event_id);
		return getPicturesURLs(event);
	}

	public static List<DownloadEventPictureReturn> getPicturesURLs(Entity event) throws InexistentEventException {
		List<String> filenames = getPicturesList(event);
		List<DownloadEventPictureReturn> download_urls = new LinkedList<>();

		filenames.forEach(file -> {
			Pair<URL, Long> url = GoogleStorageUtil.signURLForDownload(file);
			DownloadSignedURLReturn dwld_url = new DownloadSignedURLReturn(url.getValue0(), url.getValue1());
			download_urls.add(new DownloadEventPictureReturn(dwld_url, file));
		});

		return download_urls;
	}

	private static Key generateEventKey(String event_name) {
		Random rand = new Random();
		String id = null;
		Key idKey = null;
		do {
			id = "Event" + event_name.toLowerCase().replace(" ", "") + Math.abs(rand.nextInt());
			idKey = eventFactory.newKey(id);
		} while (datastore.get(idKey) != null);

		return idKey;
	}

	private static Entity updateParticipants(Key eventKey, Entity event, ListValue newParticipants, boolean add) {
		Entity.Builder builder = Entity.newBuilder(eventKey);
		long n_participants = event.getLong(N_PARTICIPANTS);
		if (add)
			builder.set(N_PARTICIPANTS, n_participants + 1);
		else
			builder.set(N_PARTICIPANTS, n_participants - 1);

		return builder.set(NAME, event.getString(NAME)).set(ID, event.getString(ID))
				.set(LOCATION, event.getLatLng(LOCATION)).set(START_DATE, event.getString(START_DATE))
				.set(END_DATE, event.getString(END_DATE)).set(CREATION_DATE, event.getString(CREATION_DATE))
				.set(CHAT_ID, event.getString(CHAT_ID)).set(PARTICIPANTS, newParticipants)
				.set(OWNER_EMAIL, event.getString(OWNER_EMAIL)).set(CONTACT, event.getString(CONTACT))
				.set(DESCRIPTION, event.getString(DESCRIPTION)).set(CATEGORY, event.getString(CATEGORY))
				.set(CAPACITY, event.getLong(CAPACITY)).set(STATE, event.getString(STATE))
				.set(PROFILE, event.getString(PROFILE)).set(WEBSITE, event.getString(WEBSITE))
				.set(FACEBOOK, event.getString(FACEBOOK)).set(INSTAGRAM, event.getString(INSTAGRAM))
				.set(TWITTER, event.getString(TWITTER)).set(GEOHASH, event.getString(GEOHASH)).set(PICTURES, event.getList(PICTURES))
				.set(REQUESTS, event.getList(REQUESTS)).set(N_REQUESTS, event.getLong(N_REQUESTS)).set(PRESENCE_CODE, event.getString(PRESENCE_CODE))
				.set(PRESENCES, event.getList(PRESENCES))
				.build();
	}

	private static Entity updateRequests(Key eventKey, Entity event, ListValue newRequests, boolean add) {
		Entity.Builder builder = Entity.newBuilder(eventKey);
		long n_requests = event.getLong(N_REQUESTS);
		if (add)
			builder.set(N_REQUESTS, n_requests + 1);
		else
			builder.set(N_REQUESTS, n_requests - 1);

		return builder.set(NAME, event.getString(NAME)).set(ID, event.getString(ID))
				.set(LOCATION, event.getLatLng(LOCATION)).set(START_DATE, event.getString(START_DATE))
				.set(END_DATE, event.getString(END_DATE)).set(CREATION_DATE, event.getString(CREATION_DATE))
				.set(CHAT_ID, event.getString(CHAT_ID)).set(PARTICIPANTS, event.getList(PARTICIPANTS))
				.set(N_PARTICIPANTS, event.getLong(N_PARTICIPANTS))
				.set(OWNER_EMAIL, event.getString(OWNER_EMAIL)).set(CONTACT, event.getString(CONTACT))
				.set(DESCRIPTION, event.getString(DESCRIPTION)).set(CATEGORY, event.getString(CATEGORY))
				.set(CAPACITY, event.getLong(CAPACITY)).set(STATE, event.getString(STATE))
				.set(PROFILE, event.getString(PROFILE)).set(WEBSITE, event.getString(WEBSITE))
				.set(FACEBOOK, event.getString(FACEBOOK)).set(INSTAGRAM, event.getString(INSTAGRAM))
				.set(TWITTER, event.getString(TWITTER)).set(GEOHASH, event.getString(GEOHASH)).set(PICTURES, event.getList(PICTURES))
				.set(REQUESTS, newRequests).set(PRESENCE_CODE, event.getString(PRESENCE_CODE))
				.set(PRESENCES, event.getList(PRESENCES)).build();
	}

	public static boolean belongsToList (Entity event, String email, boolean participant) throws InexistentEventException {
		List<String> participants = getListEmails(event, participant);
		
		if (participants.contains(email))
			return true;
		return false;
	}

	public static Entity getEvent(String event_id) throws InexistentEventException{
		Key eventKey = eventFactory.newKey(event_id);
		Entity event = datastore.get(eventKey);

		if (event == null)
			throw new InexistentEventException("9: No event with id: " + event_id);

		return event;
	}
	
	public static Triplet <Entity, StatusEvent, String> getEvent (String event_id, String user_email) throws InexistentEventException{
		Entity event = getEvent(event_id);
		
		StatusEvent status = getStatus(event, user_email);

		String owner_name = getOwnerName(event);
		
		return new Triplet<>(event, status, owner_name);
	}
	
	private static StatusEvent getStatus (Entity event, String user_email) throws InexistentEventException {
		if (belongsToList(event, user_email, true))
			return StatusEvent.PARTICIPANT;
		if (isOwner(event, user_email))
			return StatusEvent.OWNER;
		if (belongsToList(event, user_email, false))
			return StatusEvent.PENDING;
		return StatusEvent.NON_PARTICIPANT;
	}
	
	private static String getOwnerName (Entity event) {
		String owner_email = event.getString(OWNER_EMAIL);
		
		return DB_User.getName(owner_email);
	}

	public static Pair<List<Entity>, Integer> postComment(String event_id, String email, String username,
			String comment) throws InexistentChatIdException, InexistentLogIdException, SomethingWrongException,
	InexistentParticipantException, ImpossibleActionException, InexistentEventException {

		Entity event = getEvent(event_id);

		checkIsActive(event);
		checkIsParticipant(event, email);

		String chat_id = event.getString(CHAT_ID);
		return DB_Chat.postMessage(chat_id, email, username, comment);
	}

	public static Entity deleteComment(String event_id, int comment_id, String email)
			throws InexistentMessageIdException, InexistentChatIdException, InexistentLogIdException,
			InexistentParticipantException, ImpossibleActionException, InexistentEventException {

		Entity event = getEvent(event_id);
		checkIsActive(event);
		String chat_id = event.getString(CHAT_ID);
		return DB_Chat.deleteMessage(chat_id, comment_id, email);
	}

	public static Entity updateComment(String event_id, int comment_id, String user_email, String comment_content)
			throws InexistentParticipantException, InexistentMessageIdException, InexistentChatIdException,
			InexistentLogIdException, ImpossibleActionException, InexistentEventException {

		Entity event = getEvent(event_id);
		checkIsActive(event);
		String chat_id = event.getString(CHAT_ID);
		return DB_Chat.editMessage(chat_id, comment_id, user_email, comment_content);
	}

	public static Entity likeComment(String event_id, int comment_id, String req_email)
			throws InexistentChatIdException, InexistentLogIdException, InexistentMessageIdException,
			InexistentParticipantException, ImpossibleActionException, InexistentEventException {

		Entity event = getEvent(event_id);
		checkIsActive(event);
		checkIsParticipant(event, req_email);

		String chat_id = event.getString(CHAT_ID);
		return DB_Chat.likeMessage(chat_id, comment_id);
	}

	public static Entity makeChatModerator(String event_id, String req_email, String target_email)
			throws InexistentChatIdException, InexistentParticipantException, ImpossibleActionException,
			InexistentEventException {

		Entity event = getEvent(event_id);
		checkIsActive(event);
		checkIsParticipant(event, target_email);
		if (event.getString(OWNER_EMAIL).equals(target_email))
			throw new ImpossibleActionException();

		String chat_id = event.getString(CHAT_ID);
		return DB_Chat.makeModerator(chat_id, target_email, req_email);
	}

	public static Entity removeChatModerator(String event_id, String req_email, String target_email)
			throws InexistentChatIdException, InexistentParticipantException, InexistentModeratorException,
			ImpossibleActionException, InexistentEventException {

		Entity event = getEvent(event_id);
		checkIsActive(event);
		checkIsParticipant(event, req_email);

		String chat_id = event.getString(CHAT_ID);
		return DB_Chat.removeModerator(chat_id, target_email, req_email);
	}

	private static List<String> getListEmails(Entity event, boolean isParticipants) {
		List<String> emails = new LinkedList<>();
		List<Value<?>> event_emails = isParticipants ? event.getList(PARTICIPANTS) : event.getList(REQUESTS);
		event_emails.forEach(email -> {
			String person_email = (String) email.get();
			emails.add(person_email);
		});

		return emails;
	}
	
	public static Triplet<List<EventParticipantData>, Integer, MoreResultsType> getRequestsList(String event_id,
			int cursor, String user_email) throws InexistentEventException, ImpossibleActionException{
		Entity event = getEvent(event_id);
		checkIsOwner(event, user_email);
		
		return null;
	}

	public static Triplet<List<EventParticipantData>, Integer, MoreResultsType> getEventLists(String event_id,
			int cursor, boolean isParticipants, String user_email) throws InexistentChatIdException,
			InexistentEventException, InexistentUserException, ImpossibleActionException {

		Entity event = getEvent(event_id);
		if(!isParticipants)
			checkIsOwner(event, user_email);

		List<String> people_emails = getListEmails(event, isParticipants);
		List<String> mods = DB_Chat.getModerators(event.getString(CHAT_ID));
		List<EventParticipantData> participant_roles = new LinkedList<>();

		int i = 0;
		int counter = 0;
		for (String participant_email : people_emails) {
			if (counter + 1 > MAX_PARTICIPANTS_RETURN)
				break;
			if (++i <= cursor)
				continue;

			++counter;

			Entity user = DB_User.getUser(participant_email);
			String encodedPicture = user.getString(DB_User.PROFILE_PICTURE_MINIATURE);
			String username = user.getString(DB_User.USERNAME);

			if (event.getString(OWNER_EMAIL).equals(participant_email)) {
				participant_roles.add(new EventParticipantData(participant_email, username, encodedPicture, StatusEvent.OWNER.toString()));
				continue;
			}

			if (mods.contains(participant_email)) {
				//if (isParticipants), esta condicao nunca sera verdade...
				participant_roles.add(new EventParticipantData(participant_email, username, encodedPicture, StatusEvent.MOD.toString()));
				continue;
			}

			if (isParticipants)
				participant_roles.add(new EventParticipantData(participant_email, username, encodedPicture, StatusEvent.PARTICIPANT.toString()));
			else
				participant_roles.add(new EventParticipantData(participant_email, username, encodedPicture, StatusEvent.PENDING.toString()));
		}

		int new_cursor = i;
		boolean more_results = new_cursor < people_emails.size();
		return new Triplet<>(participant_roles, more_results ? new_cursor : null,
				more_results ? MoreResultsType.MORE_RESULTS_AFTER_LIMIT : MoreResultsType.NO_MORE_RESULTS);
	}

	public static List<String> getChatModerators(String event_id, String req_email)
			throws InexistentChatIdException, InexistentParticipantException, InexistentEventException {

		Entity event = getEvent(event_id);
		String chat_id = event.getString(CHAT_ID);

		checkIsParticipant(event, req_email);

		return DB_Chat.getModerators(chat_id);
	}

	public static List<Entity> participateInEvent(String event_id, String user_email)
			throws ImpossibleActionException, InexistentEventException, InexistentUserException {

		Entity event = getEvent(event_id);
		checkIsActive(event);
		checkNotFull(event);
		checkNotEnded(event);

		boolean isParticipant = belongsToList(event, user_email, true);	//verifica se e participante
		
		List<Entity> ents = new LinkedList<>();
		
		if (!isParticipant && !isPublic(event)) {
			ents.add(requestParticipation (event, user_email));
			return ents;
		}

		List<Value<?>> participants = event.getList(DB_Event.PARTICIPANTS);

		ListValue.Builder newParticipants = ListValue.newBuilder().set(participants);

		if (participants.contains(StringValue.of(user_email))) {
			ents.add(event);
			return ents;
		}

		newParticipants.addValue(user_email);

		ents.add(updateParticipants(event.getKey(), event, newParticipants.build(), true));
		
		Entity user = DB_User.getUser(user_email);
		user = DB_User.participateEvent(user.getKey(), user, event_id);
		ents.add(user);
		
		return ents;
	}

	public static Entity acceptRequest (String event_id, String target_user, String user_email)
			throws ImpossibleActionException, InexistentEventException, InexistentUserException {

		Entity event = getEvent(event_id);
		checkIsOwner(event, user_email);

		List<Entity> updated_event;
		updated_event = participateInEvent (event_id, target_user);

		return removeRequest(updated_event.get(0), target_user);
	}

	public static Entity declineRequest (String event_id, String target_user, String user_email) 
			throws ImpossibleActionException, InexistentEventException {

		Entity event = getEvent(event_id);

		checkIsOwner (event, user_email);

		return removeRequest (event, target_user);
	}

	private static Entity requestParticipation (Entity event, String user_email) {
		List<Value<?>> requests = event.getList(REQUESTS);

		ListValue.Builder newRequests = ListValue.newBuilder().set(requests);

		if (requests.contains(StringValue.of(user_email)))
			return event;

		newRequests.addValue(user_email);

		return updateRequests(event.getKey(), event, newRequests.build(), true);
	}

	private static Entity removeRequest (Entity event, String user_email) throws ImpossibleActionException {
		//nao precisa de mais verificacoes visto que chegar aqui implica ja ter passado por todas as barreiras

		List<String> requests = getListEmails (event, false);		//false significa que retorna lista de requests
		ListValue.Builder newRequestsList = ListValue.newBuilder();

		if (requests.contains(user_email)) {
			requests.remove(user_email);

			requests.forEach(request -> newRequestsList.addValue(request));

			return updateRequests(event.getKey(), event, newRequestsList.build(), false);
		}
		else throw new ImpossibleActionException("User does not belong to the list");
	}

	public static List<Entity> removeParticipant(String event_id, String target_email, String req_email)
			throws ImpossibleActionException, InexistentChatIdException, InexistentModeratorException,
			InexistentEventException {

		Entity event = getEvent(event_id);
		checkIsActive(event);

		List<String> participants = getListEmails(event, true);
		List<String> moderators = DB_Chat.getModerators(event.getString(CHAT_ID));
		ListValue.Builder newParticipantsList = ListValue.newBuilder();

		String owner = event.getString(OWNER_EMAIL);

		if (participants.contains(target_email) && !target_email.equals(owner)
				&& (owner.equals(req_email) || (moderators.contains(req_email) && !moderators.contains(target_email))
						|| target_email.equals(req_email))) {

			participants.remove(target_email);

			participants.forEach(participant -> newParticipantsList.addValue(participant));

			List<Entity> ents = new LinkedList<>();
			ents.add(updateParticipants(event.getKey(), event, newParticipantsList.build(), false));
			if (moderators.contains(target_email))
				ents.add(DB_Chat.removeModerator(event.getString(CHAT_ID), target_email, req_email));

			return ents;
		}
		throw new ImpossibleActionException();
	}

	public static Triplet<List<MessageData>, Integer, MoreResultsType> getChat(String event_id, Integer cursor,
			boolean lastest_first, String req_email) throws InexistentChatIdException, InvalidCursorException,
	InexistentLogIdException, InexistentParticipantException, InexistentEventException {

		Entity event = getEvent(event_id);
		checkIsParticipant(event, req_email);

		String chat_id = event.getString(CHAT_ID);
		return DB_Chat.getChat(chat_id, cursor == null ? 0 : cursor, lastest_first);
	}
	
	public static String getPresenceConfirmationCode(String event_id, String req_email)
			throws ImpossibleActionException, InexistentEventException {
		
		Entity event = getEvent(event_id);
		checkIsOwner(event, req_email);
		
		return event.getString(PRESENCE_CODE);
	}
	
	public static Entity confirmPresence(String event_id, String user_email)
			throws InexistentEventException, InexistentParticipantException, ImpossibleActionException {
		
		Entity event = getEvent(event_id);
		checkIsParticipant(event, user_email);
		checkNotOwner(event, user_email); // owners should not need to confirm their own presence
		checkHasStarted(event);

		List<Value<?>> confirmed_presences = event.getList(PRESENCES);
		if(confirmed_presences.contains(StringValue.of(user_email)))
			return event;

		ListValue.Builder newList = ListValue.newBuilder().set(confirmed_presences);
		newList.addValue(user_email);
		return updatePresenceList(event, newList.build());
	}
	
	private static String generateNewPictureID(Entity event, String event_id) {
		String id;
		int number;

		if (event != null) {
			id = event.getString(ID);
			List<Value<?>> pictures = event.getList(PICTURES);
			if (pictures.size() > 0)
				number = (Integer.parseInt(((String) pictures.get(pictures.size() - 1).get()).split(id + "-")[1]) + 1);
			else
				number = 1;
		} else {
			id = event_id;
			number = 1;
		}

		return id + "-" + number;
	}
	
	private static String generatePresenceCode() {
		Random rand = new Random();
		return UUID.randomUUID().toString() + Math.abs(rand.nextInt());
	}

	public static void checkIsParticipant(Entity event, String email) throws InexistentParticipantException {
		List<String> participants = getListEmails(event, true);
		if (!participants.contains(email))
			throw new InexistentParticipantException("1: User " + email + " does not participate in this event: " + event.getString(ID));
	}

	public static void checkIsActive(Entity event) throws ImpossibleActionException {
		if (!isActive(event))
			throw new ImpossibleActionException("2: Event not active: " + event.getString(ID));
	}

	public static void checkIsOwner(Entity event, String email) throws ImpossibleActionException {
		if (!isOwner(event, email))
			throw new ImpossibleActionException("3: " + email + " not owner");
	}
	
	public static void checkNotOwner(Entity event, String email) throws ImpossibleActionException {
		if (isOwner(event, email))
			throw new ImpossibleActionException("4: " + email + " is the owner");
	}

	public static void checkIsPublic(Entity event) throws ImpossibleActionException {
		if (!isPublic(event))
			throw new ImpossibleActionException("5: Event not public: " + event.getString(ID));
	}

	public static void checkNotFull(Entity event) throws ImpossibleActionException {
		if (isFull(event))
			throw new ImpossibleActionException("6: Event full: " + event.getString(ID));
	}
	
	public static void checkNotEnded(Entity event) throws ImpossibleActionException {
		if (hasEnded(event))
			throw new ImpossibleActionException("7: Event has already ended: " + event.getString(ID));
	}
	
	public static void checkHasStarted(Entity event) throws ImpossibleActionException {
		if (!hasStarted(event))
			throw new ImpossibleActionException("8: Event has not yet started: " + event.getString(ID));
	}

	public static boolean isOwner(Entity event, String owner_email) {
		return event.getString(OWNER_EMAIL).equals(owner_email);
	}

	public static boolean isActive(Entity event) {
		return event.getString(STATE).equals(State.ENABLED.toString());
	}

	public static boolean isPublic(Entity event) {
		return event.getString(PROFILE).equals(Profile.PUBLIC.toString());
	}

	public static boolean isFull(Entity event) {
		return event.getLong(N_PARTICIPANTS) >= event.getLong(CAPACITY);
	}
	
	public static boolean hasEnded(Entity event) {
		Timestamp t = Timestamp.parseTimestamp(event.getString(END_DATE));
		return Timestamp.now().compareTo(t) > 0;
	}
	
	public static boolean hasStarted(Entity event) {
		Timestamp t = Timestamp.parseTimestamp(event.getString(START_DATE));
		return Timestamp.now().compareTo(t) >= 0;
	}

}
