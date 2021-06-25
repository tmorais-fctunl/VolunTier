package voluntier.util.email;

import javax.mail.MessagingException;

import voluntier.util.consumes.RegisterData;

public class ConfirmRegistrationEmail {
	public static ConfirmationData sendConfirmationEmail(String From, RegisterData data) throws MessagingException {
		ConfirmationData confirmation = new ConfirmationData(data.email, data.username, data.password);
		
		String url = "https://voluntier-317915.ew.r.appspot.com/rest/register/" + confirmation.code + "/confirm";
		String subject = "Please confirm your email";
		String content = "<a href=\"" + url + "\">" + url + "</a>";
		
		GmailUtil.sendEmail(From, data.email, subject, content);
		return confirmation;
	}
}
