package voluntier.util.produces;

import voluntier.util.AuthToken;

public class LoginReturn extends AuthToken {
	public String username;
	
	public LoginReturn(AuthToken authToken, String username) {
		super(authToken);
		this.username = username;
	}
}
