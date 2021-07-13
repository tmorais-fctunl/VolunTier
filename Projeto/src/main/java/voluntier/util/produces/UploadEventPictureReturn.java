package voluntier.util.produces;

import java.net.URL;

public class UploadEventPictureReturn {
	public URL upload_url;
	public String pic_id;
	
	public UploadEventPictureReturn(URL signedURL, String pic_id) {
		this.upload_url = signedURL;
		this.pic_id = pic_id;
	}
}
