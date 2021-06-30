package voluntier.util.produces;

import java.net.URL;

public class GetPictureReturn extends DownloadSignedURLReturn {
	public String pic;
	
	public GetPictureReturn(URL signedURL, long size, String encodedMiniature) {
		super(signedURL, size);
		this.pic = encodedMiniature;
	}
}
