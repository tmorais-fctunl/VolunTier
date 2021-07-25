package voluntier.util.consumes.user;

import voluntier.util.data.user.UserData_Modifiable;

public class ChangePassData {

	public String password;
	public String confirmation_password;

	public ChangePassData() {
	}

	public ChangePassData(String password, String confirmation_password) {
		this.password = password;
		this.confirmation_password = confirmation_password;
	}

	public boolean isValid() {
		return password != null && confirmation_password != null && password.equals(confirmation_password)
				&& UserData_Modifiable.passwordValid(password);
	}
}
