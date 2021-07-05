package voluntier.util.consumes.event;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.LatLng;

import voluntier.util.eventdata.EventData_Minimal;

public class UpdateEventData extends EventData {
	
	public double[] location;
	public String start_date;
	public String end_date;
	
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
	
	public UpdateEventData () {
	}
	
	public UpdateEventData (String email, String token, String event_id, double[] location, String start_date, 
			String end_date, String owner_email, String contact, String description, String category, 
			long capacity, String website, String facebook, String instagram,String twitter) {
		super (email, token, event_id);
		this.location = location;
		this.start_date = start_date;
		this.end_date = end_date;
		this.owner_email = owner_email;
		this.contact = contact;
		this.description = description;
		this.category = category;
		this.capacity = capacity;
		
		this.website = website;
		this.facebook = facebook;
		this.instagram = instagram;
		this.twitter = twitter;
	}
	
	public LatLng getLocation (LatLng a_default) {
		return location == null ? a_default : LatLng.of(location[0], location[1]);
	}
	
	public Timestamp getStartDate (Timestamp a_default) {
		return start_date == null ? a_default :  Timestamp.parseTimestamp(start_date);
	}
	
	public Timestamp getEndDate (Timestamp a_default) {
		return end_date == null ?a_default : Timestamp.parseTimestamp(end_date);
	}
	
	public String getOwnerEmail (String a_default) {
		return owner_email == null ? a_default : owner_email;
	}
	
	public String getContact (String a_default) {
		return contact == null ? a_default : contact;
	}
	
	public String getDescription (String a_default) {
		return description == null ? a_default : description;
	}
	
	public String getCategory (String a_default) {
		return category == null ? a_default : category;
	}
	
	public long getCapacity (long a_default) {
		return capacity == -1 ? a_default : capacity;
	}
	
	public String getWebsite(String a_default) {
		return website == null ? a_default : website;
	}

	public String getFacebook(String a_default) {
		return facebook == null ? a_default : facebook;
	}

	public String getInstagram(String a_default) {
		return instagram == null ? a_default : instagram;
	}

	public String getTwitter(String a_default) {
		return twitter == null ? a_default : twitter;
	}
	
	public boolean isValid () {
		return super.isValid()
				//&& (name == null || EventData_Minimal.nameValid(name))
				&& (location == null || EventData_Minimal.locationValid(location))
				&& (start_date == null || EventData_Minimal.startDateValid(start_date))
				&& (end_date == null || EventData_Minimal.endDateValid(end_date))
				&& (contact == null || EventData_Minimal.contactValid(contact))
				&& (description == null || EventData_Minimal.descriptionValid(description))
				&& (category == null || EventData_Minimal.categoryValid(category))
				&& (capacity == -1 || EventData_Minimal.capacityValid(capacity))
				&& (website == null || website.equals("") || EventData_Minimal.websiteValid(website))
				&& (facebook == null || facebook.equals("") || EventData_Minimal.facebookValid(facebook))
				&& (instagram == null || instagram.equals("") || EventData_Minimal.instagramValid(instagram))
				&& (twitter == null || twitter.equals("") || EventData_Minimal.twitterValid(twitter));
	}

}
