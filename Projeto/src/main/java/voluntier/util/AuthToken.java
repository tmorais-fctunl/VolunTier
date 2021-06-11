package voluntier.util;

import java.util.UUID;
public class AuthToken {

	public static final long EXPIRATION_TIME = 1000*60*15; //15min
	public static final long REFRESH_TOKEN_EXPIRATION_TIME = 1000*60*60*24*10; //10days
	
	public String email;
	public String accessToken;
	public String refreshToken;
	public long creationDate;
	public long expirationDate;
	public long refresh_expirationDate;
	
	public AuthToken() {}
	
	public AuthToken(String email) {
		this.email = email;
		this.accessToken = UUID.randomUUID().toString();
		this.refreshToken = UUID.randomUUID().toString();
		this.creationDate = System.currentTimeMillis();
		this.expirationDate = this.creationDate + AuthToken.EXPIRATION_TIME;
		this.refresh_expirationDate = this.creationDate + AuthToken.REFRESH_TOKEN_EXPIRATION_TIME;
	}
}