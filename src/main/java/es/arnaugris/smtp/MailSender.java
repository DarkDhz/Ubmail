package es.arnaugris.smtp;

import es.arnaugris.external.MailYaml;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

public class MailSender {

    // Singleton Instance
    private static volatile MailSender instance = null;

    private MailSender() {

    }

    public static MailSender getInstance() {
        // To ensure only one instance is created
        if (instance == null) {
            synchronized (MailSender.class) {
                if (instance == null) {
                    instance = new MailSender();
                }
            }
        }
        return instance;
    }

    /**
     * Method to send report to client
     * @param mail The result data
     * @throws MessagingException Cannot send the report
     */
    public void sendReport(MailData mail) throws MessagingException {
        String report = mail.getReport();

        Properties prop = new Properties();

        prop.put("mail.smtp.host", "ssl0.ovh.net");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.ssl.protocols", "TLSv1.2");
        prop.put("mail.smtp.ssl.trust", "ssl0.ovh.net");

        MailYaml mailYaml = MailYaml.getInstance();
        String from = mailYaml.getUsername();
        String pass = mailYaml.getPassword();

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, pass);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse(mail.getMailFrom()));
        message.setSubject("MAIL REPORT FROM " + mail.getMailFrom());

        report += "<p> Original Message RAW </p>" + mail.getMessage();
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(report, "text/html; charset=utf-8");


        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }
}
