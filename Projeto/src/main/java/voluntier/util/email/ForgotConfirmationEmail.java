package voluntier.util.email;

import javax.mail.MessagingException;

import voluntier.util.ForgotPassData;

public class ForgotConfirmationEmail {

	public static ForgotData sendConfirmationEmail(String From, ForgotPassData data) throws MessagingException {
		ForgotData confirmation = new ForgotData(data.user_id, data.email);
		String url = "https://voluntier-312115.ew.r.appspot.com/rest/forgotPass/" + confirmation.code + "/confirm";
		GmailUtil.sendEmail(From, data.email, "Please change your password", "<a href=\"" + url + "\">" + url + "</a>");
		return confirmation;
	}
}
