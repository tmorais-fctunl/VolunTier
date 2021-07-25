package voluntier.util.consumes.event;

import voluntier.util.consumes.generic.RequestData;

public class QRCodeData extends RequestData {
	public String code;
	
	public QRCodeData () {
	}
	
	public QRCodeData(String token, String email, String code) {
		super(token, email);
		this.code = code;
	}
	
	public boolean isValid() {
		return super.isValid() && code != null;
	}
}
