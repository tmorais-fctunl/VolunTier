package voluntier.util.userdata;

import com.google.cloud.datastore.Entity;

import voluntier.util.RegisterData;

public class UserData_Minimal {
	
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
		this.user_id = user.getString("user_id");
		this.email = user.getString("user_email");
		this.profile = user.getString("user_profile");
		this.landline = user.getString("user_landline");
		this.mobile = user.getString("user_mobile");
		this.address = user.getString("user_address");
		this.address2 = user.getString("user_address2");
		this.region = user.getString("user_region");
		this.pc = user.getString("user_pc");
	}
	
	public static boolean passwordValid(String password) {
		return (password != null && password.length() >= 8 && password.length() < 20);
	}
	
	public static boolean emailValid(String email) {
		return (email != null && email.matches(".+@.+[.].+"));
	}
	
	public static boolean pcValid(String pc) {
		return (pc != null && (pc.matches("[0-9]{4}-[0-9]{3}") || pc.equals("")));
	}
	
	public static boolean mobileValid(String mobile) {
		return (mobile != null && (mobile.matches("([+]351\\s)?[789][0-9]{8}") || mobile.equals("")));
	}
	
	public static boolean landlineValid(String landline) {
		return (landline != null && (landline.matches("([+]351\\s)?[789][0-9]{8}") || landline.equals("")));
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
		return emailValid(email) 
				&& pcValid(pc) && mobileValid(mobile) 
				&& landlineValid(landline) && addressValid(address)
				&& addressValid(address2) && regionValid(region) 
				&& profileValid(profile);
	}
}
