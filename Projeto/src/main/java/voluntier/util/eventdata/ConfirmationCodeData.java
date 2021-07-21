package voluntier.util.eventdata;

public class ConfirmationCodeData {
	
	public String email;
	public String start_date;
	
	public String end_date;
	
	public ConfirmationCodeData() {
	}
	
	public ConfirmationCodeData(String email, String start_date) {
		this.email = email;
		this.start_date = start_date;
		this.end_date = "undefined";
	}
	
	public ConfirmationCodeData (String email, String start_date, String end_date) {
		this.email = email;
		this.start_date = start_date;
		this.end_date = end_date;
	}

}
