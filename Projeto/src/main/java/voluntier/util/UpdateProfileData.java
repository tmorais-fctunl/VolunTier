package voluntier.util;

public class UpdateProfileData extends RequestData {
	public String email;
	public String password;
	public String confirmation_password;
	public String old_password;
	public String profile;
	public String landline;
	public String mobile;
	public String address;
	public String address2;
	public String region;
	public String pc;

	public UpdateProfileData() {}
	public UpdateProfileData(String user_id, String token, 
			String email, String password, String confirmation_password, 
			String profile, String landline, String mobile, 
			String address, String address2, String region, String pc) {
		super(user_id, token);
		this.email = email;
		this.password = password;
		this.confirmation_password = confirmation_password;
		this.landline = landline;
		this.mobile = mobile;
		this.address = address;
		this.address2 = 
		this.region = address2;
		this.pc = pc;
	}

	public String getPassword(String a_default) {
		return password == null ? a_default : UserData.hashPassword(password);
	}
	public String getEmail(String a_default) {
		return email == null ? a_default : email;
	}
	public String  getProfile(String a_default) {
		return profile == null ? a_default : profile;
	}
	public String getLandline(String a_default) {
		return landline == null ? a_default : landline;
	}
	public String  getMobile(String a_default) {
		return mobile == null ? a_default : mobile;
	}
	public String  getAddress(String a_default) {
		return address == null ? a_default : address;
	}
	public String  getAddress2(String a_default) {
		return address2 == null ? a_default : address2;
	}
	public String  getRegion(String a_default) {
		return region == null ? a_default : region;
	}
	public String  getPc(String a_default) {
		return pc == null ? a_default : pc;
	}

	public boolean isValid() {	
		if(!super.isValid()) return false;

		return super.isValid() && (password == null || (UserData.passwordValid(password) && confirmation_password.equals(password) && old_password != null))
				&& (email == null || UserData.emailValid(email))
				&& (pc == null || pc.equals("") || UserData.pcValid(pc))
				&& (mobile == null || mobile.equals("") || UserData.mobileValid(mobile))
				&& (landline == null || landline.equals("") || UserData.landlineValid(landline))
				&& (address == null || address.equals("") || UserData.addressValid(address))
				&& (address2 == null || address2.equals("") || UserData.addressValid(address2))
				&& (region == null || region.equals("") || UserData.regionValid(region))
				&& (profile == null || profile.equals("") || UserData.profileValid(profile));
	}

	private static final String S = " ";	
	public String toString() {
		return password + S + email + S + pc + S + mobile + S + landline + S + address + S + address2 + S + region + S + profile;
	}
}
