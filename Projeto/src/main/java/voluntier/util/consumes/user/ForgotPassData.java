package voluntier.util.consumes.user;

import voluntier.util.data.user.UserData_Modifiable;

public class ForgotPassData {
	public String email;
	
	public ForgotPassData() {}
	
	public ForgotPassData(String email) {
		this.email = email;
	}
	
	public boolean isValid() {
		return UserData_Modifiable.emailValid(email);
	}
}
