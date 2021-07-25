package voluntier.util.produces.pictures;

import java.util.List;

public class PicturesReturn {
	public List<DownloadPictureReturn> pics;
	
	public PicturesReturn() {
	}
	
	public PicturesReturn(List<DownloadPictureReturn> pics) {
		this.pics = pics;
	}
}
