package voluntier.util.produces.user;

import java.util.List;

import voluntier.util.produces.event.SearchEventReturn;

public class UserEventsReturn {
	
	public List<SearchEventReturn> events;

	public UserEventsReturn () {
	}
	
	public UserEventsReturn (List<SearchEventReturn> events) {
		this.events = events;
	}
}
