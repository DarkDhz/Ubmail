import org.junit.Test;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Properties;

public class mailtest {

    @Test
    public void test() throws IOException, MessagingException {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", false);
        prop.put("mail.smtp.starttls.enable", "false");
        prop.put("mail.smtp.host", "localhost");
        prop.put("mail.smtp.port", "25");

        Message message = new MimeMessage(Session.getDefaultInstance(prop));
        message.setFrom(new InternetAddress("from@gmail.com"));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse("to@gmail.com"));
        message.setSubject("Mail Subject");

        String msg = "This is my first email using JavaMailer";

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
        return;
    }

}
