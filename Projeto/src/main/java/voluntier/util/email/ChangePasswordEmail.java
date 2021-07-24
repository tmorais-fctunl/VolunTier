package voluntier.util.email;

import javax.mail.MessagingException;

import voluntier.util.consumes.ForgotPassData;

public class ChangePasswordEmail {

	public static ForgotData sendConfirmationEmail(String From, ForgotPassData data) throws MessagingException {
		ForgotData confirmation = new ForgotData(data.email);
		
		String url = "https://voluntier-317915.ew.r.appspot.com/rest/forgotpassword/confirm?t=" + confirmation.code;
		String subject = "Please change your password";
		//String content = "<a href=\"" + url + "\">" + url + "</a>";
		String body = "Click here to change your password";
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
