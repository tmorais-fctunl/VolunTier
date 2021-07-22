package voluntier.util.produces;

public class DownloadEventPictureReturn {
	public DownloadSignedURLReturn dwld_url;
	public Integer pic_id;
	
	public DownloadEventPictureReturn(DownloadSignedURLReturn signedURL, Integer pic_id) {
		this.dwld_url = signedURL;
		this.pic_id = pic_id;
	}
}
