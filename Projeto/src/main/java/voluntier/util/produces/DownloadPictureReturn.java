package voluntier.util.produces;

public class DownloadPictureReturn {
	public DownloadSignedURLReturn dwld_url;
	public String pic_id;
	public String timestamp;
	
	public DownloadPictureReturn(DownloadSignedURLReturn signedURL, String pic_id, String timestamp) {
		this.dwld_url = signedURL;
		this.pic_id = pic_id;
		this.timestamp = timestamp;
	}
}
