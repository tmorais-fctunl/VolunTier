package voluntier.util.data.event;

import java.time.format.DateTimeParseException;
import java.util.List;

import com.google.cloud.Timestamp;

import voluntier.util.DB_Variables;
import voluntier.util.GeoHashUtil;
import voluntier.util.consumes.event.CreateEventData;
import voluntier.util.data.user.Profile;
import voluntier.util.data.user.State;

public class EventData_Minimal {
	
	//private static final long DEFAUL_CAPACITY = 100;
	
	private State state;
	private Profile profile;
	
	public String name;
	public String id;
	public double[] location;
	public String start_date;
	public String end_date;
	
	public String owner_email;
	public String contact;
	
	public int difficulty;
	
	//chat and participants...
	
	public String description;
	public String category;
	public long capacity;
	public String creation_date;
	
	public String website;
	public String facebook;
	public String instagram;
	public String twitter;
	
	public EventData_Minimal() {
	}
	
	public EventData_Minimal (CreateEventData data) {
		this.setState(State.ENABLED);
		//this.setProfile(Profile.PRIVATE);
		
		name = data.event_name;
		location = data.location;
		start_date = data.start_date;
		end_date = data.end_date;
		owner_email = data.email;
		description = data.description;
		category = data.category;
		setProfile (Profile.valueOf(data.profile));
		
		contact = data.contact;
		capacity = data.capacity;
		difficulty = data.difficulty;
		
		creation_date = Timestamp.now().toString();
		
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
		return name != null && name.length() <= DB_Event.MAX_NAME_SIZE && !name.equals("");
	}
	
	public static boolean locationValid (double[] location) {
		return location != null && location.length == 2 && GeoHashUtil.isValidCoords(location[0], location[1]);
	}
	
	public static boolean datesValid(String start_date, String end_date) {
		if (end_date.equalsIgnoreCase("undefined"))
			return startDateValid(start_date);
		try {
			Timestamp start = Timestamp.parseTimestamp(start_date);
			Timestamp end = Timestamp.parseTimestamp(end_date);
			return start.compareTo(end) < 0;
		} catch (DateTimeParseException e) {
			return false;
		}
	}
	
	public static boolean startDateValid (String start_date) {
		try {
			Timestamp.parseTimestamp(start_date);
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}
		
	}
	
	public static boolean endDateValid (String end_date) {
		if (end_date.equalsIgnoreCase("undefined"))
			return true;
		try {
			Timestamp.parseTimestamp(end_date);
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}
		
	}
	
	public static boolean descriptionValid (String description) {
		return description != null && description != "" && description.length() <= DB_Event.MAX_DESCRIPTION_SIZE;
	}
	
	public static boolean categoryValid (String category) {
		if (category == null)
			return false;
		List<String> categories = DB_Variables.getCategoriesList();
		for (String c : categories)
			if (category.equals(c))
				return true;
		return false;
	}
	
	public static boolean contactValid (String contact) {
		return (contact != null && (contact.equals("") || contact.matches(DB_Event.MOBILE_REGEX)));
	}
	
	public static boolean capacityValid (long capacity) {
		return capacity > 0;
	}
	
	public static boolean difficultyValid (int difficulty) {
		return difficulty > 0 && difficulty <= 5;
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
		return nameValid(name) && locationValid(location) && startDateValid(start_date) 
				&& endDateValid(end_date) && descriptionValid(description)
				&& categoryValid(category) && contactValid(contact) && websiteValid(website) 
				&& facebookValid(facebook) && instagramValid(instagram) && twitterValid(twitter);
	}
}
