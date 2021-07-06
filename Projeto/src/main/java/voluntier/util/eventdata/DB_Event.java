package voluntier.util.eventdata;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.javatuples.Pair;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.LatLng;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.Value;

import voluntier.exceptions.ImpossibleActionException;
import voluntier.exceptions.InexistentCommentIdException;
import voluntier.util.JsonUtil;
import voluntier.util.consumes.event.CreateEventData;
import voluntier.util.consumes.event.UpdateEventData;

public class DB_Event {

	public static final String NAME = "event_name";
	public static final String ID = "event_id";
	public static final String LOCATION = "location";
	public static final String START_DATE = "start_date";
	public static final String END_DATE = "end_date";
	public static final String CREATION_DATE = "creation_date";

	public static final String OWNER_EMAIL = "owner_email";
	public static final String CONTACT = "contact";
	//public static final String MODS = "mods";

	public static final String CHAT = "chat";
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


	public static Entity updateProperty (UpdateEventData data, Key eventKey, Entity event) {
		return Entity.newBuilder(eventKey)
				.set(NAME, event.getString(NAME))
				.set(ID, event.getString(ID))
				.set(LOCATION, data.getLocation(event.getLatLng(LOCATION)))
				.set(START_DATE, data.getStartDate(event.getString(START_DATE)))
				.set(END_DATE, data.getEndDate(event.getString(END_DATE)))
				.set(CREATION_DATE, event.getString(CREATION_DATE))
				.set(CHAT, event.getList(CHAT))
				.set(PARTICIPANTS, event.getList(PARTICIPANTS))
				.set(N_PARTICIPANTS, event.getLong(N_PARTICIPANTS))
				.set(OWNER_EMAIL, data.getOwnerEmail(event.getString(OWNER_EMAIL)))
				.set(CONTACT, data.getContact(event.getString(CONTACT)))
				.set(DESCRIPTION, data.getDescription(event.getString(DESCRIPTION)))
				.set(CATEGORY, data.getCategory(event.getString(CATEGORY)))
				.set(CAPACITY, data.getCapacity(event.getLong(CAPACITY)))
				.set(STATE, event.getString(STATE))
				.set(PROFILE, event.getString(PROFILE))
				//.set(REGION, data.region)
				//.set(POSTAL_CODE, data.pc)
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
				.set(CHAT, event.getList(CHAT))
				.set(PARTICIPANTS, event.getList(PARTICIPANTS))
				.set(N_PARTICIPANTS, event.getLong(N_PARTICIPANTS))
				.set(OWNER_EMAIL, event.getString(OWNER_EMAIL))
				.set(CONTACT, event.getString(CONTACT))
				.set(DESCRIPTION, event.getString(DESCRIPTION))
				.set(CATEGORY, event.getString(CATEGORY))
				.set(CAPACITY, event.getLong(CAPACITY))
				.set(STATE, state)
				.set(PROFILE, event.getString(PROFILE))
				//.set(REGION, data.region)
				//.set(POSTAL_CODE, data.pc)
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
				.set(CHAT, event.getList(CHAT))
				.set(PARTICIPANTS, event.getList(PARTICIPANTS))
				.set(N_PARTICIPANTS, event.getLong(N_PARTICIPANTS))
				.set(OWNER_EMAIL, event.getString(OWNER_EMAIL))
				.set(CONTACT, event.getString(CONTACT))
				.set(DESCRIPTION, event.getString(DESCRIPTION))
				.set(CATEGORY, event.getString(CATEGORY))
				.set(CAPACITY, event.getLong(CAPACITY))
				.set(STATE, event.getString(STATE))
				.set(PROFILE, profile)
				//.set(REGION, data.region)
				//.set(POSTAL_CODE, data.pc)
				.set(WEBSITE, event.getString(WEBSITE))
				.set(FACEBOOK, event.getString(FACEBOOK))
				.set(INSTAGRAM, event.getString(INSTAGRAM))
				.set(TWITTER, event.getString(TWITTER))
				.build();
	}

	public static Entity createNew (CreateEventData event_data, Key eventKey) {
		EventData_Minimal data = new EventData_Minimal(event_data);
		LatLng event_location = LatLng.of(data.location[0], data.location[1]);
		ListValue.Builder chat = ListValue.newBuilder();
		ListValue.Builder participants = ListValue.newBuilder();

		return Entity.newBuilder(eventKey)
				.set(NAME, data.name)
				.set(ID, data.id)
				.set(LOCATION, event_location)
				.set(START_DATE, data.start_date)
				.set(END_DATE, data.end_date)
				.set(CREATION_DATE, Timestamp.now().toString())
				.set(CHAT, chat.build())
				.set(PARTICIPANTS, participants.build())
				.set(N_PARTICIPANTS, 1)
				.set(OWNER_EMAIL, data.owner_email)
				.set(CONTACT, data.contact)
				.set(DESCRIPTION, data.description)
				.set(CATEGORY, data.category)
				.set(CAPACITY, data.capacity)
				.set(STATE, data.getState().toString())
				.set(PROFILE, data.getProfile().toString())
				//.set(REGION, data.region)
				//.set(POSTAL_CODE, data.pc)
				.set(WEBSITE, data.website)
				.set(FACEBOOK, data.facebook)
				.set(INSTAGRAM, data.instagram)
				.set(TWITTER, data.twitter)
				/*.set(PROFILE_PICTURE_MINIATURE, StringValue.newBuilder(data.profile_pic)
						.setExcludeFromIndexes(true)
						.build())*/
				.build();
	}

	public static Pair<Entity, String> postComment (Key eventKey, Entity event, String email, String comment) {
		List<Value<?>> chat = event.getList(DB_Event.CHAT);

		ListValue.Builder newChat = ListValue.newBuilder().set(chat);
		
		String comment_id = setId(chat.size());
		
		CommentData comment_data = new CommentData(email, comment, Timestamp.now().toString(), comment_id);
		
		newChat.addValue(JsonUtil.json.toJson(comment_data));

		return new Pair<Entity, String>(updateChat (eventKey, event, newChat.build()), comment_id);
	}
	
	private static String setId(int n_comment) {
		Random rand = new Random ();
		String comment_id = "Comment" + n_comment + rand.nextInt(10000);
		return comment_id;
	}

	public static Entity deleteComment(Key eventKey, Entity event, String comment_id) throws InexistentCommentIdException {
		List<Value<?>> chat = event.getList(DB_Event.CHAT);

		ListValue.Builder newChat = ListValue.newBuilder();

		Iterator<Value<?>> it = chat.iterator();

		boolean changed = false;
		while (it.hasNext()) {
			Value<?> comment = it.next();
			CommentData comment_data = JsonUtil.json.fromJson((String) comment.get(), CommentData.class);
			if (!comment_id.equals(comment_data.comment_id)) {
				newChat.addValue(comment);
			} else
				changed = true;

		}
		if (!changed)
			throw new InexistentCommentIdException("There is no such comment as " + comment_id + ".");
		
		return updateChat(eventKey, event, newChat.build());
	}
	
	public static Entity updateComment (Key eventKey, Entity event, String comment_id, String user_email, String comment_content) 
			throws ImpossibleActionException, InexistentCommentIdException {
		List<Value<?>> chat = event.getList(DB_Event.CHAT);
		
		ListValue.Builder newChat = ListValue.newBuilder();
		
		Iterator<Value<?>> it = chat.iterator();
		boolean changed = false;
		
		while (it.hasNext()) {
			Value<?> comment = it.next();
			CommentData comment_data = JsonUtil.json.fromJson((String) comment.get(), CommentData.class);

			if (!comment_id.equals(comment_data.comment_id)) 
				newChat.addValue(comment);

			else {
				if (comment_data.email.equals(user_email)) {
					comment_data.comment = comment_content;
					newChat.addValue(JsonUtil.json.toJson(comment_data));
					changed = true;
				}
				else 
					throw new ImpossibleActionException(user_email + "can't update this comment.");
			}
			
		}
		if (!changed)
			throw new InexistentCommentIdException("There is no such comment as " + comment_id + "." );

		return updateChat(eventKey, event, newChat.build());
	}

	public static Entity addParticipant (Key eventKey, Entity event, String user_email) {
		List<Value<?>> participants = event.getList(DB_Event.PARTICIPANTS);

		ListValue.Builder newParticipants = ListValue.newBuilder().set(participants);

		if(participants.contains(StringValue.of(user_email)))
			return event;
		
		newParticipants.addValue(user_email);

		return updateParticipants(eventKey, event, newParticipants.build(), true);
	}

	//podemos fazer a funcionalidade de update comment com o postComment

	public static Entity updateChat (Key eventKey, Entity event, ListValue newChat) {
		return Entity.newBuilder(eventKey)
				.set(NAME, event.getString(NAME))
				.set(ID, event.getString(ID))
				.set(LOCATION, event.getLatLng(LOCATION))
				.set(START_DATE, event.getString(START_DATE))
				.set(END_DATE, event.getString(END_DATE))
				.set(CREATION_DATE, event.getString(CREATION_DATE))
				.set(CHAT, newChat)
				.set(PARTICIPANTS, event.getList(PARTICIPANTS))
				.set(N_PARTICIPANTS, event.getLong(N_PARTICIPANTS))
				.set(OWNER_EMAIL, event.getString(OWNER_EMAIL))
				.set(CONTACT, event.getString(CONTACT))
				.set(DESCRIPTION, event.getString(DESCRIPTION))
				.set(CATEGORY, event.getString(CATEGORY))
				.set(CAPACITY, event.getLong(CAPACITY))
				.set(STATE, event.getString(STATE))
				.set(PROFILE, event.getString(PROFILE))
				//.set(REGION, data.region)
				//.set(POSTAL_CODE, data.pc)
				.set(WEBSITE, event.getString(WEBSITE))
				.set(FACEBOOK, event.getString(FACEBOOK))
				.set(INSTAGRAM, event.getString(INSTAGRAM))
				.set(TWITTER, event.getString(TWITTER))
				.build();
	}

	public static Entity updateParticipants (Key eventKey, Entity event, ListValue newParticipants, boolean add) {
		Entity.Builder builder = Entity.newBuilder(eventKey);
		long n_participants = event.getLong(N_PARTICIPANTS);
		if (add)
			builder.set(N_PARTICIPANTS, n_participants + 1) ;
		else
			builder.set(N_PARTICIPANTS, n_participants - 1);
		return builder
				.set(NAME, event.getString(NAME))
				.set(ID, event.getString(ID))
				.set(LOCATION, event.getLatLng(LOCATION))
				.set(START_DATE, event.getString(START_DATE))
				.set(END_DATE, event.getString(END_DATE))
				.set(CREATION_DATE, event.getString(CREATION_DATE))
				.set(CHAT, event.getList(CHAT))
				.set(PARTICIPANTS, newParticipants)
				.set(OWNER_EMAIL, event.getString(OWNER_EMAIL))
				.set(CONTACT, event.getString(CONTACT))
				.set(DESCRIPTION, event.getString(DESCRIPTION))
				.set(CATEGORY, event.getString(CATEGORY))
				.set(CAPACITY, event.getLong(CAPACITY))
				.set(STATE, event.getString(STATE))
				.set(PROFILE, event.getString(PROFILE))
				//.set(REGION, data.region)
				//.set(POSTAL_CODE, data.pc)
				.set(WEBSITE, event.getString(WEBSITE))
				.set(FACEBOOK, event.getString(FACEBOOK))
				.set(INSTAGRAM, event.getString(INSTAGRAM))
				.set(TWITTER, event.getString(TWITTER))
				.build();
	}

}
