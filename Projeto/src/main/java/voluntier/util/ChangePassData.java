package voluntier.util;

public class ChangePassData {

	public String user_id;
	public String password;
	public String confirmation_password;
	
	public ChangePassData() {
	}
	
	public ChangePassData (String password, String confirmation_password) {
		this.password = password;
		this.confirmation_password = confirmation_password;
	}
	
	public boolean isValid () {
		return password.equals(confirmation_password);
	}
}
