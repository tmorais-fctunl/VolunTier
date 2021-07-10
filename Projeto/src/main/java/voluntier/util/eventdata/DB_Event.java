package voluntier.util.eventdata;

import java.util.LinkedList;
import java.util.List;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.LatLng;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.Value;
import com.google.datastore.v1.QueryResultBatch;

import voluntier.exceptions.ImpossibleActionException;
import voluntier.exceptions.InexistentChatIdException;
import voluntier.exceptions.InexistentLogIdException;
import voluntier.exceptions.InexistentMessageIdException;
import voluntier.exceptions.InexistentModeratorException;
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
	
	//private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());

	public static Entity updateProperty (UpdateEventData data, Key eventKey, Entity event) {
		return Entity.newBuilder(eventKey)
				.set(NAME, event.getString(NAME))
				.set(ID, event.getString(ID))
				.set(LOCATION, data.getLocation(event.getLatLng(LOCATION)))
				.set(START_DATE, data.getStartDate(event.getString(START_DATE)))
				.set(END_DATE, data.getEndDate(event.getString(END_DATE)))
				.set(CREATION_DATE, event.getString(CREATION_DATE))
				.set(CHAT_ID, event.getString(CHAT_ID))
				.set(PARTICIPANTS, event.getList(PARTICIPANTS))
				.set(N_PARTICIPANTS, event.getLong(N_PARTICIPANTS))
				.set(OWNER_EMAIL, data.getOwnerEmail(event.getString(OWNER_EMAIL)))
				.set(CONTACT, data.getContact(event.getString(CONTACT)))
				.set(DESCRIPTION, data.getDescription(event.getString(DESCRIPTION)))
				.set(CATEGORY, data.getCategory(event.getString(CATEGORY)))
				.set(CAPACITY, data.getCapacity(event.getLong(CAPACITY)))
				.set(STATE, event.getString(STATE))
				.set(PROFILE, event.getString(PROFILE))
				.set(WEBSITE, data.getWebsite(event.getString(WEBSITE)))
				.set(FACEBOOK, data.getFacebook(event.getString(FACEBOOK)))
				.set(INSTAGRAM, data.getInstagram(event.getString(INSTAGRAM)))
				.set(TWITTER, data.getTwitter(event.getString(TWITTER)))
				.build();
	}

	public static Entity updateState (Key eventKey, Entity event, String state ) {
		return Entity.newBuilder(eventKey)
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

	public static Entity updateProfile (Key eventKey, Entity event, String profile) {
		return Entity.newBuilder(eventKey)
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

	public static List<Entity> createNew (CreateEventData event_data, Key eventKey, String user_email) {
		EventData_Minimal data = new EventData_Minimal(event_data);
		LatLng event_location = LatLng.of(data.location[0], data.location[1]);
		Pair<List<Entity>, String> chat = DB_Chat.createNew(user_email);
		ListValue.Builder participants = ListValue.newBuilder();
		participants.addValue(user_email);
		List<Entity> entities = chat.getValue0();

		entities.add(Entity.newBuilder(eventKey)
				.set(NAME, data.name)
				.set(ID, data.id)
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
		
		return entities;
	}
	
	private static void checkIsParticipant(Key eventKey, Entity event, String req_email) throws ImpossibleActionException {
		List<String> participants = getParticipants(eventKey, event);
		if(!participants.contains(req_email))
			throw new ImpossibleActionException("Non particpants cannot see chat moderators");
	}

	public static Pair<List<Entity>, Integer> postComment (Key eventKey, Entity event, String email, String comment)
			throws InexistentChatIdException, InexistentLogIdException,
				SomethingWrongException, ImpossibleActionException {

		checkIsParticipant(eventKey, event, email);
		
		String chat_id = event.getString(CHAT_ID);
		return DB_Chat.postMessage(chat_id, email, comment);
	}
	
	public static Entity deleteComment(Key eventKey, Entity event, int comment_id, String email)
			throws InexistentMessageIdException, InexistentChatIdException,
				InexistentLogIdException, ImpossibleActionException {

		checkIsParticipant(eventKey, event, email);
		
		String chat_id = event.getString(CHAT_ID);
		return DB_Chat.deleteMessage(chat_id, comment_id, email);
	}
	
	public static Entity updateComment (Key eventKey, Entity event, int comment_id, String user_email, String comment_content) 
			throws ImpossibleActionException, InexistentMessageIdException,
				InexistentChatIdException, InexistentLogIdException {

		checkIsParticipant(eventKey, event, user_email);
		
		String chat_id = event.getString(CHAT_ID);
		return DB_Chat.editMessage(chat_id, comment_id, user_email, comment_content);
	}
	
	public static Entity likeComment (Key eventKey, Entity event, int comment_id, String req_email)
			throws InexistentChatIdException, InexistentLogIdException, 
			InexistentMessageIdException, ImpossibleActionException {

		checkIsParticipant(eventKey, event, req_email);
		
		String chat_id = event.getString(CHAT_ID);
		return DB_Chat.likeMessage(chat_id, comment_id);
	}
	
	public static Entity makeChatModerator (Key eventKey, Entity event, String req_email, String target_email) 
			throws InexistentChatIdException, ImpossibleActionException {

		checkIsParticipant(eventKey, event, req_email);
		
		String chat_id = event.getString(CHAT_ID);
		return DB_Chat.makeModerator(chat_id, target_email, req_email);
	}
	
	public static Entity removeChatModerator (Key eventKey, Entity event, String req_email, String target_email) 
			throws InexistentChatIdException, ImpossibleActionException, InexistentModeratorException {

		checkIsParticipant(eventKey, event, req_email);
		
		String chat_id = event.getString(CHAT_ID);
		return DB_Chat.removeModerator(chat_id, target_email, req_email);
	}
	
	public static List<String> getParticipants(Key eventKey, Entity event) {
		List<String> participants = new LinkedList<>();
		List<Value<?>> event_participants = event.getList(PARTICIPANTS);
		event_participants.forEach(participant -> {
			String participant_email = (String) participant.get();
			participants.add(participant_email);
		});
		
		return participants;
	}
	
	public static List<String> getChatModerators(Key eventKey, Entity event, String req_email) 
			throws InexistentChatIdException, ImpossibleActionException {
		String chat_id = event.getString(CHAT_ID);
		
		checkIsParticipant(eventKey, event, req_email);
		
		return DB_Chat.getModerators(chat_id);
	}

	public static Entity addParticipant (Key eventKey, Entity event, String user_email) {
		List<Value<?>> participants = event.getList(DB_Event.PARTICIPANTS);

		ListValue.Builder newParticipants = ListValue.newBuilder().set(participants);

		if(participants.contains(StringValue.of(user_email)))
			return event;
		
		newParticipants.addValue(user_email);

		return updateParticipants(eventKey, event, newParticipants.build(), true);
	}
	
	public static Triplet<List<MessageData>, Integer, QueryResultBatch.MoreResultsType> getChat(Key eventKey, 
			Entity event, Integer cursor, boolean lastest_first, String req_email) 
			throws InexistentChatIdException, InvalidCursorException,
				InexistentLogIdException, ImpossibleActionException{

		checkIsParticipant(eventKey, event, req_email);
		
		String chat_id = event.getString(CHAT_ID);
		return DB_Chat.getChat(chat_id, cursor == null ? 0 : cursor, lastest_first);
	}

	public static Entity updateParticipants (Key eventKey, Entity event, ListValue newParticipants, boolean add) {
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
