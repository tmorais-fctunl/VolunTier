package voluntier.util.email;

import javax.mail.MessagingException;

import voluntier.util.ConfirmationURL;
import voluntier.util.RegisterData;

public class ConfirmationEmail {
	public static ConfirmationURL sendConfirmationEmail(String From, RegisterData data) throws MessagingException {
		ConfirmationURL confirmation = new ConfirmationURL(data.user_id, data.email, data.password);
		String url = "https://voluntier-312115.ew.r.appspot.com/rest/register/" + confirmation.code + "/confirm";
		GmailUtil.sendEmail(From, data.email, "Please confirm your email", "<a href=\"" + url + "\">" + url + "</a>");
		return confirmation;
	}
}
