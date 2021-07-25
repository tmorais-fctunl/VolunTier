package voluntier.util.produces.event;

import java.util.List;

import voluntier.util.produces.pictures.DownloadEventPictureReturn;

public class EventPicturesReturn {
	public List<DownloadEventPictureReturn> pics;
	
	public EventPicturesReturn() {
	}
	
	public EventPicturesReturn(List<DownloadEventPictureReturn> pics) {
		this.pics = pics;
	}
}
