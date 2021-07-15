package voluntier.util.consumes.event;

public class ParticipantData extends EventData {

	public String participant;

	public ParticipantData() {
	}

	public ParticipantData (String participant){
		this.participant = participant;
	}


	public boolean isValid () {
		return super.isValid() && participant != null;
	}
}
