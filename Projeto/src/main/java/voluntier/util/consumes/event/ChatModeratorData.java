package voluntier.util.consumes.event;

public class ChatModeratorData extends EventData {

	public String mod;

	public ChatModeratorData() {
	}

	public ChatModeratorData (String mod){
		this.mod = mod;
	}


	public boolean isValid () {
		return super.isValid() && mod != null;
	}
}
