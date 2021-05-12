package voluntier.util;

import java.util.UUID;

public class ConfirmationURL extends RegisterData {
	public static final long EXPIRATION_TIME = 1000*60*15; //15min
	
	public String code;
	public long creationDate;
	public long expirationDate;
	
	public ConfirmationURL() {}
	
	public ConfirmationURL(String user_id, String email, String password) {
		super(user_id, email, password);
		this.code = UUID.randomUUID().toString();
		this.creationDate = System.currentTimeMillis();
		this.expirationDate = this.creationDate + AuthToken.EXPIRATION_TIME;
	}
}
