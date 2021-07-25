package voluntier.util.email;

import javax.mail.MessagingException;

import voluntier.util.consumes.user.RegisterData;

public class ConfirmRegistrationEmail {
	public static ConfirmationData sendConfirmationEmail(String From, RegisterData data) throws MessagingException {
		ConfirmationData confirmation = new ConfirmationData(data.email, data.username, data.password);
		
		String url = "https://voluntier-317915.ew.r.appspot.com/rest/register/" + confirmation.code + "/confirm";
		String subject = "Please confirm your email";
		String body = "Please click here to confirm your email";
		//String content = "<a href=\"" + url + "\">" + url + "</a>";
		String content = "<div style=\"outline:none;" +
			    " border-style: solid;" + 
			    " border-color: white;" +
			    " border-width: 1px;" +
			    " border-radius: 12px;" +
			    " background-color: lightgray\">" +
			    " <a href=\"" + url +"\" style=\"text-align: center; font-size:140%;\">" + body + "</a>" +
			    " </div>";
		
		GmailUtil.sendEmail(From, data.email, subject, content);
		return confirmation;
	}
}
