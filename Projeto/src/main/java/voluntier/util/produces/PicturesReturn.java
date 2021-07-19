package voluntier.util.produces;

import java.util.List;

public class PicturesReturn {
	public List<DownloadEventPictureReturn> pics;
	
	public PicturesReturn() {
	}
	
	public PicturesReturn(List<DownloadEventPictureReturn> pics) {
		this.pics = pics;
	}
}
