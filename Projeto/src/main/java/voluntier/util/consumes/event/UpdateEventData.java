package voluntier.util.consumes.event;

import com.google.cloud.datastore.LatLng;

import voluntier.exceptions.IllegalCoordinatesException;
import voluntier.util.GeoHashUtil;
import voluntier.util.eventdata.EventData_Minimal;

public class UpdateEventData extends EventData {
	
	public double[] location;
	public String start_date;
	public String end_date;
	public String profile;
	
	public String event_name;
	
	public String contact;
	
	public String description;
	public String category;
	public Long capacity;
	
	public String website;
	public String facebook;
	public String instagram;
	public String twitter;
	
	public UpdateEventData () {
	}
	
	public UpdateEventData (String email, String token, String event_id, double[] location, String start_date, 
			String end_date, String owner_email, String contact, String description, String category, 
			long capacity, String website, String facebook, String instagram,String twitter, String profile,
			String event_name) {
		super (email, token, event_id);
		this.event_name = event_name;
		this.location = location;
		this.start_date = start_date;
		this.end_date = end_date;
		this.contact = contact;
		this.description = description;
		this.category = category;
		this.capacity = capacity;
		
		this.website = website;
		this.facebook = facebook;
		this.instagram = instagram;
		this.twitter = twitter;
		
		this.profile = profile;
	}
	
	public LatLng getLocation (LatLng a_default) {
		return location == null ? a_default : LatLng.of(location[0], location[1]);
	}
	
	public String getStartDate (String a_default) {
		return start_date == null ? a_default :  start_date;
	}
	
	public String getEndDate (String a_default) {
		return end_date == null ? a_default : end_date;
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
		return capacity == null ? a_default : capacity;
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
	
	public String getProfile(String a_default) {
		return profile == null ? a_default : profile;
	}
	
	public String getGeohash(String a_default) throws IllegalCoordinatesException {
		return location == null ? a_default : GeoHashUtil.convertCoordsToGeoHashHighPrecision(location[0], location[1]);
	}
	
	public boolean isValid () {
		return super.isValid()
				&& (event_name == null || EventData_Minimal.nameValid(event_name))
				&& (location == null || EventData_Minimal.locationValid(location))
				&& (profile == null || EventData_Minimal.profileValid(profile))
				&& (start_date == null || EventData_Minimal.startDateValid(start_date))
				&& (end_date == null || EventData_Minimal.endDateValid(end_date))
				&& (contact == null || EventData_Minimal.contactValid(contact))
				&& (description == null || EventData_Minimal.descriptionValid(description))
				&& (category == null || EventData_Minimal.categoryValid(category))
				&& (capacity == null || EventData_Minimal.capacityValid(capacity))
				&& (website == null || EventData_Minimal.websiteValid(website))
				&& (facebook == null || EventData_Minimal.facebookValid(facebook))
				&& (instagram == null || EventData_Minimal.instagramValid(instagram))
				&& (twitter == null || EventData_Minimal.twitterValid(twitter));
	}

}
