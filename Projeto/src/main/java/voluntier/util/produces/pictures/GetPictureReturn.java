package voluntier.util.produces.pictures;

import java.net.URL;

public class GetPictureReturn extends DownloadSignedURLReturn {
	//public String pic;
	
	public GetPictureReturn(URL signedURL, long size/*, String encodedMiniature*/) {
		super(signedURL, size);
		//this.pic = encodedMiniature;
	}
}
