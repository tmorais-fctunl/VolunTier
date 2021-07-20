package voluntier.util.produces;

import java.util.List;

public class UserEventsReturn {
	
	public List<SearchEventReturn> events;

	public UserEventsReturn () {
	}
	
	public UserEventsReturn (List<SearchEventReturn> events) {
		this.events = events;
	}
}
