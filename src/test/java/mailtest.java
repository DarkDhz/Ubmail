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
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        //prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.host", "localhost");
        prop.put("mail.smtp.port", "25");

        Message message = new MimeMessage(Session.getDefaultInstance(prop));


        message.setFrom(new InternetAddress("from@gmail.com"));

        InternetAddress[] myToList = InternetAddress.parse("gopi.mani@xyz.com,Maimsa.SF@xyz.com");
        InternetAddress[] myBccList = InternetAddress.parse("Usha.B@xyz.com");
        InternetAddress[] myCcList = InternetAddress.parse("NEHA.SIVA@xyz.com");

        message.setRecipients(Message.RecipientType.TO,myToList);
        message.setRecipients(Message.RecipientType.BCC,myBccList);
        message.setRecipients(Message.RecipientType.CC,myCcList);

        message.setSubject("Mail Subject");

        String msg = "Esto es un correo de prueba";

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
        return;
    }

    @Test
    public void testwithAutentication() throws IOException, MessagingException {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "localhost");
        prop.put("mail.smtp.port", "25");
        prop.put("mail.smtp.ssl.trust", "localhost");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("roberto", "eric duque");
            }
        });

        Message message = new MimeMessage(session);
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
        //prop.put("mail.smtp.ssl.trust", "smtp.mailtrap.io");
    }

}
