package voluntier.util.produces;

import java.net.URL;

public class GetPictureReturn {
	public URL url;
	public String pic;
	
	public GetPictureReturn(URL signedURL, String encodedMiniature) {
		this.url = signedURL;
		this.pic = encodedMiniature;
	}
}
