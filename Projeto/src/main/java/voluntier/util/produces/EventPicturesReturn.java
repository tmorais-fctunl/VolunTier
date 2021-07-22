package voluntier.util.produces;

import java.util.List;

public class EventPicturesReturn {
	public List<DownloadEventPictureReturn> pics;
	
	public EventPicturesReturn() {
	}
	
	public EventPicturesReturn(List<DownloadEventPictureReturn> pics) {
		this.pics = pics;
	}
}
