package voluntier.util.consumes.route;

public class RouteParticipantData extends RouteData {

	public String participant;

	public RouteParticipantData() {
	}

	public RouteParticipantData (String participant){
		this.participant = participant;
	}


	public boolean isValid () {
		return super.isValid() && participant != null;
	}
}
