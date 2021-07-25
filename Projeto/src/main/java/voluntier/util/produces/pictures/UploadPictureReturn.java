package voluntier.util.produces.pictures;

import java.net.URL;

public class UploadPictureReturn {
	public URL upload_url;
	public String pic_id;
	
	public UploadPictureReturn(URL signedURL, String pic_id) {
		this.upload_url = signedURL;
		this.pic_id = pic_id;
	}
}
