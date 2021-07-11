package voluntier.util.consumes.event;

public class KickParticipantData extends EventData {

	public String participant;

	public KickParticipantData() {
	}

	public KickParticipantData (String participant){
		this.participant = participant;
	}


	public boolean isValid () {
		return super.isValid() && participant != null;
	}
}
