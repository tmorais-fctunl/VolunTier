package voluntier.util.userdata;

import com.google.cloud.datastore.Entity;

import voluntier.util.consumes.RegisterData;

public class UserData_Minimal {

	public static final String EMAIL_REGEX = ".+@.+[.].+";
	public static final String POSTAL_CODE_REGEX = "[0-9]{4}-[0-9]{3}";
	public static final String MOBILE_REGEX = "([+]351\\s)?[789][0-9]{8}";
	
	public String user_id;
	public String email;
	
	public String profile;
	
	public String landline;
	public String mobile;
	public String address;
	public String address2;
	public String region;
	public String pc;
	
	public UserData_Minimal(){}
	
	public UserData_Minimal(RegisterData data){
		user_id = data.user_id;
		email = data.email;
		profile = Profile.PUBLIC.toString();
		landline = "";
		mobile = "";
		address = "";
		address2 = "";
		region = "";
		pc = "";
	}
	
	public UserData_Minimal(Entity user) {
		this.user_id = user.getString(DB_User.ID);
		this.email = user.getString(DB_User.EMAIL);
		this.profile = user.getString(DB_User.PROFILE);
		this.landline = user.getString(DB_User.LANDLINE);
		this.mobile = user.getString(DB_User.MOBILE);
		this.address = user.getString(DB_User.ADDRESS);
		this.address2 = user.getString(DB_User.ADDRESS2);
		this.region = user.getString(DB_User.REGION);
		this.pc = user.getString(DB_User.POSTAL_CODE);
	}
	
	public static boolean emailValid(String email) {
		return (email != null && email.matches(EMAIL_REGEX));
	}
	
	public static boolean idValid(String id) {
		return (id != null && id.length() > 4);
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
	
	public static boolean profileValid(String profile) {
		return (profile != null && (profile.equals(Profile.PRIVATE.toString()) || profile.equals(Profile.PUBLIC.toString())));
	}
	
	boolean isValid() {
		return idValid(user_id) && emailValid(email) 
				&& pcValid(pc) && mobileValid(mobile) 
				&& landlineValid(landline) && addressValid(address)
				&& addressValid(address2) && regionValid(region) 
				&& profileValid(profile);
	}
}
