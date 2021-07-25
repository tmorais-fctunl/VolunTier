package voluntier.util.consumes.user;

import voluntier.util.consumes.generic.RequestData;
import voluntier.util.data.user.UserData_Modifiable;

public class UpdateProfileData extends RequestData {
	public String password;
	public String full_name;
	public String confirmation_password;
	public String old_password;
	public String profile;
	public String landline;
	public String mobile;
	public String address;
	public String address2;
	public String region;
	public String pc;
	public String description;

	public String website;
	public String facebook;
	public String instagram;
	public String twitter;
	
	public String target;

	public UpdateProfileData() {
	}

	public UpdateProfileData(String token, String email, String password, String full_name,
			String confirmation_password, String profile, String landline, String mobile, String address,
			String address2, String region, String pc, String state, String description, String website, String facebook, 
			String instagram, String twitter, String target) {
		super(email, token);
		this.full_name = full_name;
		this.password = password;
		this.confirmation_password = confirmation_password;
		this.landline = landline;
		this.mobile = mobile;
		this.address = address;
		this.address2 = this.region = address2;
		this.pc = pc;
		this.profile = profile;
		this.description = description;

		this.website = website;
		this.facebook = facebook;
		this.instagram = instagram;
		this.twitter = twitter;
		
		this.target = target;
	}

	public String getPassword(String a_default) {
		return password == null ? a_default : UserData_Modifiable.hashPassword(password);
	}

	public String getFullName(String a_default) {
		return full_name == null ? a_default : full_name;
	}

	public String getProfile(String a_default) {
		return profile == null ? a_default : profile;
	}

	public String getLandline(String a_default) {
		return landline == null ? a_default : landline;
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

	public String getMobile(String a_default) {
		return mobile == null ? a_default : mobile;
	}

	public String getAddress(String a_default) {
		return address == null ? a_default : address;
	}

	public String getAddress2(String a_default) {
		return address2 == null ? a_default : address2;
	}

	public String getRegion(String a_default) {
		return region == null ? a_default : region;
	}

	public String getPc(String a_default) {
		return pc == null ? a_default : pc;
	}

	public String getState(String a_default) {
		return profile == null ? a_default : profile;
	}
	
	public String getDescription (String a_default) {
		return description == null ? a_default : description;
	}

	public boolean isValid() {
		return super.isValid() && target != null
				&& (password == null || (UserData_Modifiable.passwordValid(password)
						&& confirmation_password.equals(password) && old_password != null))
				&& (full_name == null || UserData_Modifiable.fullNameValid(full_name))
				&& (profile == null || UserData_Modifiable.profileValid(profile))
				&& (pc == null || pc.equals("") || UserData_Modifiable.pcValid(pc))
				&& (mobile == null || mobile.equals("") || UserData_Modifiable.mobileValid(mobile))
				&& (landline == null || landline.equals("") || UserData_Modifiable.landlineValid(landline))
				&& (address == null || address.equals("") || UserData_Modifiable.addressValid(address))
				&& (address2 == null || address2.equals("") || UserData_Modifiable.addressValid(address2))
				&& (region == null || region.equals("") || UserData_Modifiable.regionValid(region))
				&& (description == null || description.equals("") || UserData_Modifiable.descriptionValid(description))
				&& (website == null || website.equals("") || UserData_Modifiable.websiteValid(website))
				&& (facebook == null || facebook.equals("") || UserData_Modifiable.facebookValid(facebook))
				&& (instagram == null || instagram.equals("") || UserData_Modifiable.instagramValid(instagram))
				&& (twitter == null || twitter.equals("") || UserData_Modifiable.twitterValid(twitter));
	}

	private static final String S = " ";

	public String toString() {
		return full_name + S + password + S + email + S + pc + S + mobile + S + landline + S + address + S + address2
				+ S + region + S + profile + S + website + S + facebook + S + instagram + S + twitter;
	}
}
