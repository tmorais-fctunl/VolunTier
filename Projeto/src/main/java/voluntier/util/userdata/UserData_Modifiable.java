package voluntier.util.userdata;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.datastore.Entity;

import voluntier.util.consumes.RegisterData;

public class UserData_Modifiable extends UserData_Minimal{

	public String password;

	public UserData_Modifiable(){}
	
	public UserData_Modifiable(RegisterData data){
		super(data);
		password = data.password;
	}
	
	public UserData_Modifiable(Entity user) {
		super(user);
		this.password = user.getString(DB_User.PASSWORD);
	}

	public String getHashedPassword() {
		return hashPassword(password);
	}
	
	public static String hashPassword(String password) {
		return DigestUtils.sha512Hex(password);
	}
	
	public static boolean passwordValid(String password) {
		return (password != null && password.length() >= 8 && password.length() < 64);
	}
	
	boolean isValid() {
		return passwordValid(password) && super.isValid();
	}
}
