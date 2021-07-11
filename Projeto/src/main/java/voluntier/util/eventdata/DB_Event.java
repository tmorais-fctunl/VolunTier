package voluntier.util.eventdata;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
import com.google.datastore.v1.QueryResultBatch;

import voluntier.exceptions.ImpossibleActionException;
import voluntier.exceptions.InexistentChatIdException;
import voluntier.exceptions.InexistentEventException;
import voluntier.exceptions.InexistentLogIdException;
import voluntier.exceptions.InexistentMessageIdException;
import voluntier.exceptions.InexistentModeratorException;
import voluntier.exceptions.InexistentParticipantException;
import voluntier.exceptions.InvalidCursorException;
import voluntier.exceptions.SomethingWrongException;
import voluntier.util.consumes.event.CreateEventData;
import voluntier.util.consumes.event.UpdateEventData;
import voluntier.util.eventdata.chatdata.DB_Chat;
import voluntier.util.userdata.Profile;
import voluntier.util.userdata.State;

public class DB_Event {

	public static final String NAME = "event_name";
	public static final String ID = "event_id";
	public static final String LOCATION = "location";
	public static final String START_DATE = "start_date";
	public static final String END_DATE = "end_date";
	public static final String CREATION_DATE = "creation_date";

	public static final String OWNER_EMAIL = "owner_email";
	public static final String CONTACT = "contact";

	public static final String CHAT_ID = "chat_id";
	public static final String PARTICIPANTS = "participants";
	public static final String N_PARTICIPANTS = "num_participants";

	public static final String DESCRIPTION = "description";
	public static final String CATEGORY = "category";
	public static final String CAPACITY = "capacity";

	public static final String WEBSITE = "event_website";
	public static final String FACEBOOK = "event_facebook";
	public static final String INSTAGRAM = "event_instagram";
	public static final String TWITTER = "event_twitter";

	public static final String STATE = "event_state";
	public static final String PROFILE = "event_profile";

	public static final String MOBILE_REGEX = "([+][0-9]{2,3}\\s)?[2789][0-9]{8}";
	
	public static final int DEFALUT_COMMENT_SIZE = 500;
	public static final long DEFAULT_CAPACITY = 100;
	
	private static Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private static KeyFactory eventFactory = datastore.newKeyFactory().setKind("Event");
	//private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());

	public static Entity updateProperty (UpdateEventData data) throws InexistentEventException, ImpossibleActionException {
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
				.build();
	}

	public static Entity updateState (String event_id, String email, String state ) throws ImpossibleActionException, InexistentEventException {
		Entity event = getEvent(event_id);
		checkIsOwner(event, email);
		checkIsActive(event);
		
		return Entity.newBuilder(event.getKey())
				.set(NAME, event.getString(NAME))
				.set(ID, event.getString(ID))
				.set(LOCATION, event.getLatLng(LOCATION))
				.set(START_DATE, event.getString(START_DATE))
				.set(END_DATE, event.getString(END_DATE))
				.set(CREATION_DATE, event.getString(CREATION_DATE))
				.set(CHAT_ID, event.getString(CHAT_ID))
				.set(PARTICIPANTS, event.getList(PARTICIPANTS))
				.set(N_PARTICIPANTS, event.getLong(N_PARTICIPANTS))
				.set(OWNER_EMAIL, event.getString(OWNER_EMAIL))
				.set(CONTACT, event.getString(CONTACT))
				.set(DESCRIPTION, event.getString(DESCRIPTION))
				.set(CATEGORY, event.getString(CATEGORY))
				.set(CAPACITY, event.getLong(CAPACITY))
				.set(STATE, state)
				.set(PROFILE, event.getString(PROFILE))
				.set(WEBSITE, event.getString(WEBSITE))
				.set(FACEBOOK, event.getString(FACEBOOK))
				.set(INSTAGRAM, event.getString(INSTAGRAM))
				.set(TWITTER, event.getString(TWITTER))
				.build();
	}

	public static Entity updateProfile (String event_id, String email, String profile) throws ImpossibleActionException, InexistentEventException {
		Entity event = getEvent(event_id);
		checkIsOwner(event, email);
		checkIsActive(event);
		
		return Entity.newBuilder(event.getKey())
				.set(NAME, event.getString(NAME))
				.set(ID, event.getString(ID))
				.set(LOCATION, event.getLatLng(LOCATION))
				.set(START_DATE, event.getString(START_DATE))
				.set(END_DATE, event.getString(END_DATE))
				.set(CREATION_DATE, event.getString(CREATION_DATE))
				.set(CHAT_ID, event.getString(CHAT_ID))
				.set(PARTICIPANTS, event.getList(PARTICIPANTS))
				.set(N_PARTICIPANTS, event.getLong(N_PARTICIPANTS))
				.set(OWNER_EMAIL, event.getString(OWNER_EMAIL))
				.set(CONTACT, event.getString(CONTACT))
				.set(DESCRIPTION, event.getString(DESCRIPTION))
				.set(CATEGORY, event.getString(CATEGORY))
				.set(CAPACITY, event.getLong(CAPACITY))
				.set(STATE, event.getString(STATE))
				.set(PROFILE, profile)
				.set(WEBSITE, event.getString(WEBSITE))
				.set(FACEBOOK, event.getString(FACEBOOK))
				.set(INSTAGRAM, event.getString(INSTAGRAM))
				.set(TWITTER, event.getString(TWITTER))
				.build();
	}

	public static Pair<List<Entity>, String> createNew (CreateEventData event_data) {
		Pair<List<Entity>, String> chat = DB_Chat.createNew(event_data.email);
		ListValue.Builder participants = ListValue.newBuilder();
		participants.addValue(event_data.email);
		List<Entity> entities = chat.getValue0();

		Key eventKey = generateEventID(event_data.event_name);
				
		EventData_Minimal data = new EventData_Minimal(event_data);
		LatLng event_location = LatLng.of(data.location[0], data.location[1]);

		entities.add(Entity.newBuilder(eventKey)
				.set(NAME, data.name)
				.set(ID, eventKey.getName())
				.set(LOCATION, event_location)
				.set(START_DATE, data.start_date)
				.set(END_DATE, data.end_date)
				.set(CREATION_DATE, Timestamp.now().toString())
				.set(CHAT_ID, chat.getValue1())
				.set(PARTICIPANTS, participants.build())
				.set(N_PARTICIPANTS, 1)
				.set(OWNER_EMAIL, data.owner_email)
				.set(CONTACT, data.contact)
				.set(DESCRIPTION, data.description)
				.set(CATEGORY, data.category)
				.set(CAPACITY, data.capacity)
				.set(STATE, data.getState().toString())
				.set(PROFILE, data.getProfile().toString())
				.set(WEBSITE, data.website)
				.set(FACEBOOK, data.facebook)
				.set(INSTAGRAM, data.instagram)
				.set(TWITTER, data.twitter)
				.build());
		
		return new Pair<>(entities, eventKey.getName());
	}
	
	private static Key generateEventID(String event_name) {
		Random rand = new Random();
		String id = null;
		Key idKey = null;
		do {
			id = "Event" + event_name.toLowerCase().replace(" ",  "") + Math.abs(rand.nextInt());
			idKey = eventFactory.newKey(id);
		} while (datastore.get(idKey) != null);

		return idKey;
	}
	
	private static void checkIsParticipant(Entity event, String email) throws InexistentParticipantException {
		List<String> participants = getParticipantEmails(event);
		if(!participants.contains(email))
			throw new InexistentParticipantException("User " + email + " does not participate in this event");
	}
	
	private static void checkIsActive(Entity event) throws ImpossibleActionException {
		if(!isActive(event))
			throw new ImpossibleActionException();
	}
	
	private static void checkIsOwner(Entity event, String email) throws ImpossibleActionException {
		if(!isOwner(event, email))
			throw new ImpossibleActionException();
	}
	
	private static void checkIsPublic(Entity event) throws ImpossibleActionException {
		if(!isPublic(event))
			throw new ImpossibleActionException();
	}
	
	private static void checkNotFull(Entity event) throws ImpossibleActionException {
		if(isFull(event))
			throw new ImpossibleActionException();
	}
	
	
	public static Entity getEvent(String event_id) throws InexistentEventException {
		Key eventKey = eventFactory.newKey(event_id);
		Entity event = datastore.get(eventKey);

		if (event == null)
			throw new InexistentEventException("No event with id: " + event_id);
		
		return event;
	}

	public static Pair<List<Entity>, Integer> postComment (String event_id, String email, String username, String comment)
			throws InexistentChatIdException, InexistentLogIdException,
				SomethingWrongException, InexistentParticipantException, 
				ImpossibleActionException, InexistentEventException {

		Entity event = getEvent(event_id);
		
		checkIsActive(event);
		checkIsParticipant(event, email);
		
		String chat_id = event.getString(CHAT_ID);
		return DB_Chat.postMessage(chat_id, email, username, comment);
	}
	
	public static Entity deleteComment(String event_id, int comment_id, String email)
			throws InexistentMessageIdException, InexistentChatIdException,
				InexistentLogIdException, InexistentParticipantException, 
				ImpossibleActionException, InexistentEventException {

		Entity event = getEvent(event_id);
		checkIsActive(event);
		String chat_id = event.getString(CHAT_ID);
		return DB_Chat.deleteMessage(chat_id, comment_id, email);
	}
	
	public static Entity updateComment (String event_id, int comment_id, String user_email, String comment_content) 
			throws InexistentParticipantException, InexistentMessageIdException,
				InexistentChatIdException, InexistentLogIdException, 
				ImpossibleActionException, InexistentEventException {

		Entity event = getEvent(event_id);
		checkIsActive(event);
		String chat_id = event.getString(CHAT_ID);
		return DB_Chat.editMessage(chat_id, comment_id, user_email, comment_content);
	}
	
	public static Entity likeComment (String event_id, int comment_id, String req_email)
			throws InexistentChatIdException, InexistentLogIdException, 
			InexistentMessageIdException, InexistentParticipantException, 
			ImpossibleActionException, InexistentEventException {

		Entity event = getEvent(event_id);
		checkIsActive(event);
		checkIsParticipant(event, req_email);
		
		String chat_id = event.getString(CHAT_ID);
		return DB_Chat.likeMessage(chat_id, comment_id);
	}
	
	public static Entity makeChatModerator (String event_id, String req_email, String target_email) 
			throws InexistentChatIdException, InexistentParticipantException, 
			ImpossibleActionException, InexistentEventException {

		Entity event = getEvent(event_id);
		checkIsActive(event);
		checkIsParticipant(event, target_email);
		if(event.getString(OWNER_EMAIL).equals(target_email))
			throw new ImpossibleActionException();
		
		String chat_id = event.getString(CHAT_ID);
		return DB_Chat.makeModerator(chat_id, target_email, req_email);
	}
	
	public static Entity removeChatModerator (String event_id, String req_email, String target_email) 
			throws InexistentChatIdException, InexistentParticipantException,
			InexistentModeratorException, ImpossibleActionException, 
			InexistentEventException {

		Entity event = getEvent(event_id);
		checkIsActive(event);
		checkIsParticipant(event, req_email);
		
		String chat_id = event.getString(CHAT_ID);
		return DB_Chat.removeModerator(chat_id, target_email, req_email);
	}
	
	private static List<String> getParticipantEmails(Entity event) {
		List<String> participants = new LinkedList<>();
		List<Value<?>> event_participants = event.getList(PARTICIPANTS);
		event_participants.forEach(participant -> {
			String participant_email = (String) participant.get();
			participants.add(participant_email);
		});
		
		return participants;
	}
	
	public static List<EventParticipantData> getParticipantRoles(String event_id)
			throws InexistentChatIdException, InexistentEventException {
		
		Entity event = getEvent(event_id);
		List<String> participant_emails = getParticipantEmails(event);
		List<String> mods = DB_Chat.getModerators(event.getString(CHAT_ID));
		List<EventParticipantData> participant_roles = new LinkedList<>();
		
		participant_emails.forEach(participant_email -> {
			if(event.getString(OWNER_EMAIL).equals(participant_email)) {
				participant_roles.add(new EventParticipantData(participant_email, "ADMIN"));
				return;
			}
			
			if(mods.contains(participant_email)) {
				participant_roles.add(new EventParticipantData(participant_email, "MOD"));
				return;
			}
			
			participant_roles.add(new EventParticipantData(participant_email, "PARTICIPANT"));
		});
		
		return participant_roles;
	}
	
	public static List<String> getChatModerators(String event_id, String req_email) 
			throws InexistentChatIdException, InexistentParticipantException, 
			InexistentEventException {
		
		Entity event = getEvent(event_id);
		String chat_id = event.getString(CHAT_ID);
		
		checkIsParticipant(event, req_email);
		
		return DB_Chat.getModerators(chat_id);
	}

	public static Entity participateInEvent (String event_id, String user_email) 
			throws ImpossibleActionException, InexistentEventException {

		Entity event = getEvent(event_id);
		checkIsActive(event);
		checkIsPublic(event);
		checkNotFull(event);
		
		List<Value<?>> participants = event.getList(DB_Event.PARTICIPANTS);

		ListValue.Builder newParticipants = ListValue.newBuilder().set(participants);

		if(participants.contains(StringValue.of(user_email)))
			return event;
		
		newParticipants.addValue(user_email);

		return updateParticipants(event.getKey(), event, newParticipants.build(), true);
	}
	
	public static List<Entity> removeParticipant (String event_id, String target_email, String req_email)
			throws ImpossibleActionException, InexistentChatIdException, 
			InexistentModeratorException, InexistentEventException {

		Entity event = getEvent(event_id);
		checkIsActive(event);
		
		List<String> participants = getParticipantEmails(event);
		List<String> moderators = DB_Chat.getModerators(event.getString(CHAT_ID));
		ListValue.Builder newParticipantsList = ListValue.newBuilder();
		
		String owner = event.getString(OWNER_EMAIL);
		if((!owner.equals(req_email) && !moderators.contains(req_email))
				|| !participants.contains(target_email) || target_email.equals(owner))
			throw new ImpossibleActionException();
		
		participants.remove(target_email);
		
		participants.forEach(participant -> newParticipantsList.addValue(participant));
		
		List<Entity> ents = new LinkedList<>();
		ents.add(updateParticipants(event.getKey(), event, newParticipantsList.build(), false));
		if(moderators.contains(target_email))
			ents.add(DB_Chat.removeModerator(event.getString(CHAT_ID), target_email, req_email));

		return ents;
	}
	
	public static Triplet<List<MessageData>, Integer, QueryResultBatch.MoreResultsType> getChat(String event_id, 
			Integer cursor, boolean lastest_first, String req_email) 
			throws InexistentChatIdException, InvalidCursorException,
				InexistentLogIdException, InexistentParticipantException,
				InexistentEventException {

		Entity event = getEvent(event_id);
		checkIsParticipant(event, req_email);
		
		String chat_id = event.getString(CHAT_ID);
		return DB_Chat.getChat(chat_id, cursor == null ? 0 : cursor, lastest_first);
	}

	private static Entity updateParticipants (Key eventKey, Entity event, ListValue newParticipants, boolean add) {
		Entity.Builder builder = Entity.newBuilder(eventKey);
		long n_participants = event.getLong(N_PARTICIPANTS);
		if (add)
			builder.set(N_PARTICIPANTS, n_participants + 1);
		else
			builder.set(N_PARTICIPANTS, n_participants - 1);
		return builder
				.set(NAME, event.getString(NAME))
				.set(ID, event.getString(ID))
				.set(LOCATION, event.getLatLng(LOCATION))
				.set(START_DATE, event.getString(START_DATE))
				.set(END_DATE, event.getString(END_DATE))
				.set(CREATION_DATE, event.getString(CREATION_DATE))
				.set(CHAT_ID, event.getString(CHAT_ID))
				.set(PARTICIPANTS, newParticipants)
				.set(OWNER_EMAIL, event.getString(OWNER_EMAIL))
				.set(CONTACT, event.getString(CONTACT))
				.set(DESCRIPTION, event.getString(DESCRIPTION))
				.set(CATEGORY, event.getString(CATEGORY))
				.set(CAPACITY, event.getLong(CAPACITY))
				.set(STATE, event.getString(STATE))
				.set(PROFILE, event.getString(PROFILE))
				.set(WEBSITE, event.getString(WEBSITE))
				.set(FACEBOOK, event.getString(FACEBOOK))
				.set(INSTAGRAM, event.getString(INSTAGRAM))
				.set(TWITTER, event.getString(TWITTER))
				.build();
	}
	
	public static boolean isOwner (Entity event, String owner_email) {
		return event.getString(OWNER_EMAIL).equals(owner_email);
	}
	
	public static boolean isActive (Entity event) {
		return event.getString(DB_Event.STATE).equals(State.ENABLED.toString());
	}
	
	public static boolean isPublic (Entity event) {
		return event.getString(DB_Event.PROFILE).equals(Profile.PUBLIC.toString());
	}
	
	public static boolean isFull (Entity event) {
		return event.getLong(DB_Event.N_PARTICIPANTS) >= event.getLong(DB_Event.CAPACITY);
	}
	
}
