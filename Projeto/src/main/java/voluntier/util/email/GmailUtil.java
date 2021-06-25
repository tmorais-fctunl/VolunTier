package voluntier.util.email;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

// From https://netcorecloud.com/tutorials/send-email-in-java-using-gmail-smtp/
public class GmailUtil {

	public static void sendEmail(String From, String To, String subject, String content)
			throws MessagingException {
	    Properties props = new Properties();
	    Session session = Session.getDefaultInstance(props, null);

	    try {
	      Message msg = new MimeMessage(session);
	      msg.setFrom(new InternetAddress(From, "Voluntier Devs"));
	      msg.addRecipient(Message.RecipientType.TO,
	                       new InternetAddress(To, "Mr. User"));
	      msg.setSubject(subject);
	      msg.setContent(content, "text/html");
	      Transport.send(msg);
	    } catch (MessagingException | UnsupportedEncodingException e) {
	      throw new MessagingException();
	    }
	}
}
