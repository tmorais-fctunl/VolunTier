package util.email;

import javax.mail.MessagingException;

public class ConfirmationEmail {
	public static void sendConfirmationEmail(String From, String To) throws MessagingException {
		GmailUtil.sendEmail(From, To, "Please confirm your email", "<h1>This is your confirmation email<h1>");
	}
}
