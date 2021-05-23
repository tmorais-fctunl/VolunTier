package voluntier.util.consumes;

import voluntier.util.userdata.UserData_Modifiable;

public class ForgotPassData {

	public String user_id;
	public String email;
	
	public ForgotPassData() {}
	
	public ForgotPassData(String user_id, String email) {
		this.user_id = user_id;
		this.email = email;
	}
	
	public boolean isValid() {
		return UserData_Modifiable.idValid(user_id) && UserData_Modifiable.emailValid(email);
	}
}
