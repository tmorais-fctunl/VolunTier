package voluntier.util.eventdata;

import java.util.Iterator;
import java.util.List;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.LatLng;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.Value;
import com.google.gson.Gson;

import voluntier.util.consumes.event.CreateEventData;
import voluntier.util.consumes.event.UpdateEventData;
import voluntier.util.produces.EventReturn;

public class DB_Event {
	
	private static final Gson g = new Gson();
	
	public static final String NAME = "event_name";
	public static final String ID = "event_id";
	public static final String LOCATION = "location";
	public static final String DATE = "date";		//timestamp
	//public static final String CREATION_DATE = "creation_date";
	
	public static final String OWNER_EMAIL = "owner_email";
	public static final String CONTACT = "contact";
	//public static final String MODS = "mods";
	
	public static final String CHAT = "chat";
	public static final String PARTICIPANTS = "participants";
	
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
	
	
	public static Entity updateProperty (UpdateEventData data, Key eventKey, Entity event) {
		return Entity.newBuilder(eventKey)
				.set(NAME, event.getString(NAME))
				.set(ID, event.getString(ID))
				.set(LOCATION, data.getLocation(event.getLatLng(LOCATION)))
				.set(DATE, data.getDate(event.getTimestamp(DATE)))
				.set(CHAT, event.getList(CHAT))
				.set(PARTICIPANTS, event.getList(PARTICIPANTS))
				//.set(CREATION_DATE, data.creation_date)
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
				.set(DATE, event.getTimestamp(DATE))
				.set(CHAT, event.getList(CHAT))
				.set(PARTICIPANTS, event.getList(PARTICIPANTS))
				//.set(CREATION_DATE, data.creation_date)
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
				.set(DATE, event.getTimestamp(DATE))
				.set(CHAT, event.getList(CHAT))
				.set(PARTICIPANTS, event.getList(PARTICIPANTS))
				//.set(CREATION_DATE, data.creation_date)
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
		Timestamp event_date = Timestamp.parseTimestamp(data.date);
		LatLng event_location = LatLng.of(data.location[0], data.location[1]);
		ListValue.Builder chat = ListValue.newBuilder();
		ListValue.Builder participants = ListValue.newBuilder();
		
		return Entity.newBuilder(eventKey)
				.set(NAME, data.name)
				.set(ID, data.id)
				.set(LOCATION, event_location)
				.set(DATE, event_date)
				.set(CHAT, chat.build())
				.set(PARTICIPANTS, participants.build())
				//.set(CREATION_DATE, data.creation_date)
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
	
	public static Entity postComment (Key eventKey, Entity event, EventReturn comment) {
		List<Value<?>> chat = event.getList(DB_Event.CHAT);
		
		ListValue.Builder newChat = ListValue.newBuilder().set(chat);
		
		newChat.addValue(g.toJson(comment));
		
		return updateChat (eventKey, event, newChat.build());
	}
	
	public static Entity deleteComment (Key eventKey, Entity event, int index) {
		List<Value<?>> chat = event.getList(DB_Event.CHAT);
		
		ListValue.Builder newChat = ListValue.newBuilder();
		
		Iterator<Value<?>> it = chat.iterator();
		int i = 0;
		
		while (it.hasNext())
			if (i++ != index)
				newChat.addValue(it.next());
		
		return updateChat (eventKey, event, newChat.build());
	}
	
	public static Entity addParticipant (Key eventKey, Entity event, String user_email) {
		List<Value<?>> participants = event.getList(DB_Event.PARTICIPANTS);
		
		ListValue.Builder newParticipants = ListValue.newBuilder().set(participants);
		
		newParticipants.addValue(user_email);
		
		return updateParticipants(eventKey, event, newParticipants.build());
	}
	
	//podemos fazer a funcionalidade de update comment com o postComment
	
	public static Entity updateChat (Key eventKey, Entity event, ListValue newChat) {
		return Entity.newBuilder(eventKey)
				.set(NAME, event.getString(NAME))
				.set(ID, event.getString(ID))
				.set(LOCATION, event.getLatLng(LOCATION))
				.set(DATE, event.getTimestamp(DATE))
				.set(CHAT, newChat)
				.set(PARTICIPANTS, event.getList(PARTICIPANTS))
				//.set(CREATION_DATE, data.creation_date)
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
	
	public static Entity updateParticipants (Key eventKey, Entity event, ListValue newParticipants) {
		return Entity.newBuilder(eventKey)
				.set(NAME, event.getString(NAME))
				.set(ID, event.getString(ID))
				.set(LOCATION, event.getLatLng(LOCATION))
				.set(DATE, event.getTimestamp(DATE))
				.set(CHAT, event.getList(CHAT))
				.set(PARTICIPANTS, newParticipants)
				//.set(CREATION_DATE, data.creation_date)
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
