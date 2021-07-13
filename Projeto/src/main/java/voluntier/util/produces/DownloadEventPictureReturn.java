package voluntier.util.produces;

public class DownloadEventPictureReturn {
	public DownloadSignedURLReturn dwld_url;
	public String pic_id;
	
	public DownloadEventPictureReturn(DownloadSignedURLReturn signedURL, String pic_id) {
		this.dwld_url = signedURL;
		this.pic_id = pic_id;
	}
}
