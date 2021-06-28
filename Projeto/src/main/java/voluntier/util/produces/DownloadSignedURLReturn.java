package voluntier.util.produces;

import java.net.URL;

public class DownloadSignedURLReturn {
	public URL url;
	public long size;
	
	public DownloadSignedURLReturn(URL signedURL, long size) {
		this.url = signedURL;
		this.size = size;
	}
}
