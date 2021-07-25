package voluntier.util.consumes.generic;

public class RequestUploadDownloadData extends RequestData {
	
	public String filename;
	
	public RequestUploadDownloadData() {}
	public RequestUploadDownloadData(String email, String token, String filename) {
		super(email, token);
		this.filename = filename;
	}
	
	public boolean isValid() {
		return super.isValid() && filename != null && !filename.equals("");
	}
}
