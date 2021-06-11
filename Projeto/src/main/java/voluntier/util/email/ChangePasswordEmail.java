package voluntier.util.email;

import javax.mail.MessagingException;

import voluntier.util.consumes.ForgotPassData;

public class ChangePasswordEmail {

	public static ForgotData sendConfirmationEmail(String From, ForgotPassData data) throws MessagingException {
		ForgotData confirmation = new ForgotData(data.email);
		
		String url = "https://voluntier-312115.ew.r.appspot.com/rest/forgotpassword/confirm?t=" + confirmation.code;
		String subject = "Please change your password";
		String content = "<a href=\"" + url + "\">" + url + "</a>";
		
		GmailUtil.sendEmail(From, data.email, subject, content);
		return confirmation;
	}
}
