package util;

import org.apache.commons.codec.digest.DigestUtils;

public class UserData {
	
	public String user_id;
	public String email;
	public String password;
	
	public String profile;
	
	public String landline;
	public String mobile;
	public String address;
	public String address2;
	public String region;
	public String pc;
	
	public UserData(){}
	
	public UserData(RegisterData data){
		user_id = data.user_id;
		email = data.email;
		password = hashPassword(data.password);
		
		profile = Profile.PUBLIC.toString();
		
		landline = "";
		mobile = "";
		address = "";
		address2 = "";
		region = "";
		pc = "";
	}

	public String getHashedPassword() {
		return hashPassword(password);
	}
	
	public static String hashPassword(String password) {
		return DigestUtils.sha512Hex(password);
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
		return passwordValid(password) && emailValid(email) 
				&& pcValid(pc) && mobileValid(mobile) 
				&& landlineValid(landline) && addressValid(address)
				&& addressValid(address2) && regionValid(region) 
				&& profileValid(profile);
	}
}
