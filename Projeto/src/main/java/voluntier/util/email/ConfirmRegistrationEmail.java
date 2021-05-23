package voluntier.util.email;

import javax.mail.MessagingException;

import voluntier.util.consumes.RegisterData;

public class ConfirmRegistrationEmail {
	public static ConfirmationData sendConfirmationEmail(String From, RegisterData data) throws MessagingException {
		ConfirmationData confirmation = new ConfirmationData(data.user_id, data.email, data.password);
		
		String url = "https://voluntier-312115.ew.r.appspot.com/rest/register/" + confirmation.code + "/confirm";
		String subject = "Please confirm your email";
		String content = "<a href=\"" + url + "\">" + url + "</a>";
		
		GmailUtil.sendEmail(From, data.email, subject, content);
		return confirmation;
	}
}
