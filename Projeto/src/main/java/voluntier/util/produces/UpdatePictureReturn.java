package voluntier.util.produces;

import java.net.URL;

public class UpdatePictureReturn {
	public URL upload_url;
	public Integer pic_id;
	
	public UpdatePictureReturn(URL signedURL, Integer pic_id) {
		this.upload_url = signedURL;
		this.pic_id = pic_id;
	}
}
