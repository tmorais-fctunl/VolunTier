package voluntier.util.email;

import java.util.UUID;

import voluntier.util.AuthToken;
import voluntier.util.consumes.ForgotPassData;

public class ForgotData extends ForgotPassData{

public static final long EXPIRATION_TIME = 1000*60*15; //15min
	
	public String code;
	public long creationDate;
	public long expirationDate;
	
	public ForgotData() {}
	
	public ForgotData(String email/*, String password*/) {
		super(email/*, UserData_Modifiable.hashPassword(password)*/);
		this.code = UUID.randomUUID().toString();
		this.creationDate = System.currentTimeMillis();
		this.expirationDate = this.creationDate + AuthToken.EXPIRATION_TIME;
	}
}
