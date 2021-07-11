package voluntier.util.consumes.event;

public class RemoveParticipantData extends EventData {

	public String participant;

	public RemoveParticipantData() {
	}

	public RemoveParticipantData (String participant){
		this.participant = participant;
	}


	public boolean isValid () {
		return super.isValid() && participant != null;
	}
}
