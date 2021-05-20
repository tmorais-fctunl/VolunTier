package voluntier.util;

public class ForgotPassData {

	public String user_id;
	public String email;
	
	public ForgotPassData() {}
	
	public ForgotPassData(String user_id, String email) {
		this.user_id = user_id;
		this.email = email;
	}
	
	public boolean isValid() {
		return user_id.length() > 4 && email.matches(".+@.+[.].+");
	}
}
