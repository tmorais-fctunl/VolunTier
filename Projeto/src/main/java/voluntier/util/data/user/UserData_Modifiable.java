package voluntier.util.data.user;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.datastore.Entity;

import voluntier.util.Argon2Util;
import voluntier.util.consumes.user.RegisterData;

public class UserData_Modifiable extends UserData_Minimal {

	public String password;
	public String profile_pic;

	public UserData_Modifiable() {
	}

	public UserData_Modifiable(RegisterData data) {
		super(data);
		password = data.password;
		profile_pic = "";
	}

	public UserData_Modifiable(Entity user) {
		super(user);
		this.password = user.getString(DB_User.PASSWORD);
		this.profile_pic = user.getString(DB_User.PROFILE_PICTURE_MINIATURE);
	}

	public String getHashedPassword() {
		return hashPassword(password);
	}

	public static String hashPasswordOLD(String password) {
		return DigestUtils.sha512Hex(password);
	}

	public static String hashPassword(String password) {
		return Argon2Util.hashPassword(password);
	}

	public static boolean passwordValid(String password) {
		return (password != null && password.length() >= 8 && password.length() <= 128);
	}

	public static boolean profilePictureValid(String data) {
		ProfilePicture p = new ProfilePicture(data);
		return p.isValid();
	}

	boolean isValid() {
		return passwordValid(password) && profilePictureValid(profile_pic) && super.isValid();
	}
}
