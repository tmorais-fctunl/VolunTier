package voluntier.util;

import java.util.UUID;

public class AuthToken {

	//public static final long EXPIRATION_TIME = 1000*60*60; //1h
	//public static final long REFRESH_TOKEN_EXPIRATION_TIME = 1000*60*60*24*10; //10days
	
	public String email;
	public String accessToken;
	public String refreshToken;
	public long creationDate;
	public long expirationDate;
	public long refresh_expirationDate;
	
	public AuthToken() {}
	
	public AuthToken(AuthToken other) {
		this.email = other.email;
		this.accessToken = other.accessToken;
		this.refreshToken = other.refreshToken;
		this.creationDate = other.creationDate;
		this.expirationDate = other.expirationDate;
		this.refresh_expirationDate = other.refresh_expirationDate;
	}
	
	public AuthToken(String email) {
		this.email = email;
		this.accessToken = UUID.randomUUID().toString();
		this.refreshToken = UUID.randomUUID().toString();
		this.creationDate = System.currentTimeMillis();
		this.expirationDate = this.creationDate + DB_Variables.getAccessExpiration();
		this.refresh_expirationDate = this.creationDate + DB_Variables.getRefreshExpiration();
	}
}