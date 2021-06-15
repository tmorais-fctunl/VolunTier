package voluntier.util.userdata;

import java.util.Random;

import com.google.cloud.datastore.Entity;

import voluntier.util.consumes.RegisterData;

public class UserData_Minimal {

	public static final String EMAIL_REGEX = ".+@.+[.].+";
	public static final String POSTAL_CODE_REGEX = "[0-9]{4}-[0-9]{3}";
	public static final String MOBILE_REGEX = "([+]351\\s)?[789][0-9]{8}";
	public static final String USERNAME_REGEX = "[a-zA-Z][a-zA-Z0-9]*([.][a-zA-Z0-9]+|[a-zA-Z0-9]*)";

	public String username;
	public String email;

	public String profile;

	public String full_name;
	public String landline;
	public String mobile;
	public String address;
	public String address2;
	public String region;
	public String pc;

	public String website;
	public String facebook;
	public String instagram;
	public String twitter;

	public UserData_Minimal() {
	}

	public UserData_Minimal(RegisterData data) {
		email = data.email;
		username = data.username;
		profile = Profile.PUBLIC.toString();
		Random rand = new Random();
		this.full_name = "User" + rand.nextInt(1000000);
		landline = "";
		mobile = "";
		address = "";
		address2 = "";
		region = "";
		pc = "";
		website = "";
		facebook = "";
		instagram = "";
		twitter = "";
	}

	public UserData_Minimal(Entity user) {
		this.username = user.getString(DB_User.USERNAME);
		this.email = user.getString(DB_User.EMAIL);
		this.full_name = user.getString(DB_User.FULL_NAME);
		this.profile = user.getString(DB_User.PROFILE);
		this.landline = user.getString(DB_User.LANDLINE);
		this.mobile = user.getString(DB_User.MOBILE);
		this.address = user.getString(DB_User.ADDRESS);
		this.address2 = user.getString(DB_User.ADDRESS2);
		this.region = user.getString(DB_User.REGION);
		this.pc = user.getString(DB_User.POSTAL_CODE);

		this.website = user.getString(DB_User.WEBSITE);
		this.facebook = user.getString(DB_User.FACEBOOK);
		this.instagram = user.getString(DB_User.INSTAGRAM);
		this.twitter = user.getString(DB_User.TWITTER);

	}

	public static boolean emailValid(String email) {
		return (email != null && email.matches(EMAIL_REGEX));
	}

	public static boolean fullNameValid(String fullName) {
		return fullName != null;
	}

	public static boolean usernameValid(String username) {
		return (username != null && username.length() > 4 && username.length() < 30 && username.matches(USERNAME_REGEX));
	}
	
	public static boolean pcValid(String pc) {
		return (pc != null && (pc.matches(POSTAL_CODE_REGEX) || pc.equals("")));
	}

	public static boolean mobileValid(String mobile) {
		return (mobile != null && (mobile.matches(MOBILE_REGEX) || mobile.equals("")));
	}

	public static boolean landlineValid(String landline) {
		return (landline != null && (landline.matches(MOBILE_REGEX) || landline.equals("")));
	}

	public static boolean addressValid(String address) {
		return (address != null && (address.length() > 5 || address.equals("")));
	}

	public static boolean regionValid(String region) {
		return (region != null && (region.length() > 3 || region.equals("")));
	}

	public static boolean websiteValid(String website) {
		return website != null;
	}

	public static boolean facebookValid(String facebook) {
		return facebook != null;
	}

	public static boolean instagramValid(String instagram) {
		return instagram != null;
	}

	public static boolean twitterValid(String twitter) {
		return twitter != null;
	}

	public static boolean profileValid(String profile) {
		return (profile != null
				&& (profile.equals(Profile.PRIVATE.toString()) || profile.equals(Profile.PUBLIC.toString())));
	}

	boolean isValid() {
		return usernameValid(username) && emailValid(email) && fullNameValid(full_name) && pcValid(pc)
				&& mobileValid(mobile) && landlineValid(landline) && addressValid(address) && addressValid(address2)
				&& regionValid(region) && profileValid(profile) && websiteValid(website) && facebookValid(facebook)
				&& instagramValid(instagram) && twitterValid(twitter);
	}
}
