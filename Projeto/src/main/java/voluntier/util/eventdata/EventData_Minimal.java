package voluntier.util.eventdata;

import voluntier.util.consumes.event.CreateEventData;
import voluntier.util.userdata.Profile;
import voluntier.util.userdata.State;

public class EventData_Minimal {
	
	private static final long DEFAUL_CAPACITY = 100;
	
	private State state;
	private Profile profile;
	
	public String name;
	public String id;
	public double[] location;
	public String date;
	
	public String owner_email;
	public String contact;
	
	//chat and participants...
	
	public String description;
	public String category;
	public long capacity;
	//public int number_participants;
	//public String creation_date;
	
	public String website;
	public String facebook;
	public String instagram;
	public String twitter;
	
	public EventData_Minimal() {
	}
	
	public EventData_Minimal (CreateEventData data) {
		this.setState(State.ENABLED);
		this.setProfile(Profile.PRIVATE);
		
		name = data.event_name;
		id = data.event_id;
		location = data.location;
		date = data.date;
		owner_email = data.email;
		
		contact = "";
		description = "";
		category = "";
		capacity = DEFAUL_CAPACITY;
		//this.creation_date = System.currentTimeMillis();
		
		website = "";
		facebook = "";
		instagram = "";
		twitter = "";
	}
	
	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}
	
	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	
	public static boolean nameValid (String name) {
		return name != null && name.length() < 100;
	}
	
	public static boolean locationValid (double[] location) {
		return location != null;
	}
	
	public static boolean dateValid (String date) {
		return date != null;
	}
	
	public static boolean descriptionValid (String description) {
		return description != null && description.length() < 500;
	}
	
	public static boolean categoryValid (String category) {
		return category != null && category.length() < 30;	//posteriormente, ideia será ter categorias pre definidas...
	}
	
	public static boolean contactValid (String contact) {
		return (contact != null && (contact.matches(DB_Event.MOBILE_REGEX)));
	}
	
	public static boolean capacityValid (long capacity) {
		return capacity > 0;
	}
	
	public static boolean websiteValid(String website) {
		return website != null && website.length() < 120;
	}

	public static boolean facebookValid (String facebook) {
		return facebook != null && facebook.length() < 120;
	}

	public static boolean instagramValid (String instagram) {
		return instagram != null && instagram.length() < 120;
	}

	public static boolean twitterValid (String twitter) {
		return twitter != null && twitter.length() < 120;
	}
	
	public static boolean stateValid(String state) {
		return state != null && (state.equals(State.BANNED.toString()) || state.equals(State.ENABLED.toString()));
	}

	public static boolean profileValid(String account) {
		return account != null && (account.equals(Profile.PRIVATE.toString()) || account.equals(Profile.PUBLIC.toString()));
	}

	boolean isValid() {
		return nameValid(name) && locationValid(location) && dateValid(date) && descriptionValid(description)
				&& categoryValid(category) && contactValid(contact) && websiteValid(website) 
				&& facebookValid(facebook) && instagramValid(instagram) && twitterValid(twitter);
	}
}
