package voluntier.util.produces;

import java.util.List;

import com.google.datastore.v1.QueryResultBatch.MoreResultsType;

import voluntier.util.eventdata.MessageData;

public class UserEventsReturn {
	
	public List<String> events;

	public UserEventsReturn () {
	}
	
	public UserEventsReturn (List<String> events) {
		this.events = events;
	}
}
