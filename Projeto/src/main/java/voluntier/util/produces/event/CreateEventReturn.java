package voluntier.util.produces.event;

import java.net.URL;

public class CreateEventReturn {
	
	public String event_id;
	public URL upload_url;

	public CreateEventReturn () {
	}
	
	public CreateEventReturn (String id, URL url) {
		this.event_id = id;
		this.upload_url = url;
	}
}
