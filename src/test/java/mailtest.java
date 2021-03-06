import org.junit.Test;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;


public class mailtest {

    private final String host = "localhost";
    //private final String host = "54.36.191.29";

    @Test
    public void long_test() throws IOException, MessagingException {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        //prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.host", host);
        prop.put("mail.smtp.port", "25");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("arnau.gris@gmail.com", "unkopwn1223s");
            }
        });

        Message message = new MimeMessage(session);


        message.setFrom(new InternetAddress("arnau.gris@gmail.com"));

        InternetAddress[] myToList = InternetAddress.parse("gopi.mani@xyz.com,Maimsa.SF@xyz.com");
        InternetAddress[] myBccList = InternetAddress.parse("Usha.B@xyz.com");
        InternetAddress[] myCcList = InternetAddress.parse("NEHA.SIVA@xyz.com");

        message.setRecipients(Message.RecipientType.TO,myToList);
        message.setRecipients(Message.RecipientType.BCC,myBccList);
        message.setRecipients(Message.RecipientType.CC,myCcList);

        message.setSubject("Mail Subject");

        String msg = "Esto es cár`sact`èr un correo de prueba \n https://www.ub.edu/cosas \n https://www.upc.edu \n https://www.ub.com/cosas \n https://www.universitat.edu/cosas" +
                "\n https://u17063761.ct.sendgrid.net/ls/click?upn=WBLqlOEcPJ4MT-2F1w9AVnTnRtUo0Ef0nx3MYAC-2BPj0wcu8QBYyyzkg1HcWZRO2i58juMGF2wYjpG4Q9gKcpUkOfyo6t1wNYIwRHVkVoLuw4s-3DoiLZ_MQ-2BdZzAEi-2B85suYdHipVjQUhSVM9KUh87N6vp-2FntwVpLzxzCsYBmwxXUb3Zir1Vhv9HO-2F-2B5z-2FfgRT6molGBJ4URymcqEFB9EkmQPbPFqFJ3V-2FRE-2FPdHVjVyLdA3rzoXxlPQIYu3RO7Mny4Us-2F16dWKYvlwWZWg1Zfc5GxMqttDUOB48-2Ba-2Bmlri3oHAQ647IkkXFI9Cf-2FqLSSigIE-2BNdPk3nme2tzUJ-2BrW-2F70129T2Po-3D" +
                " https://bit.ly/3mwmHfY " + "<a href=\"https://www.upc.edu\">Link oculto</a>\n";

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
        return;
    }

    @Test
    public void test() throws IOException, MessagingException {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        //prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.host", host);
        prop.put("mail.smtp.port", "25");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("arnau.gris@gmail.com", "unkopwn1223s");
            }
        });

        Message message = new MimeMessage(session);


        message.setFrom(new InternetAddress("arnau.gris@gmail.com"));

        InternetAddress[] myToList = InternetAddress.parse("gopi.mani@xyz.com,Maimsa.SF@xyz.com");
        InternetAddress[] myBccList = InternetAddress.parse("Usha.B@xyz.com");
        InternetAddress[] myCcList = InternetAddress.parse("NEHA.SIVA@xyz.com");

        message.setRecipients(Message.RecipientType.TO,myToList);
        message.setRecipients(Message.RecipientType.BCC,myBccList);
        message.setRecipients(Message.RecipientType.CC,myCcList);

        message.setSubject("Mail Subject");

        String msg = "Esto es cár`sact`èr un correo de prueba \n https://www.ub.edu/cosas \n https://bit.ly/3mwmHfY \n https://www.ub.com/cosas \n https://www.universitat.edu/cosas" +
                "\n " +
                "<a href=\"https://www.upc.edu\">Link oculto</a>\n";

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
        prop.put("mail.smtp.host", host);
        prop.put("mail.smtp.port", "25");
        prop.put("mail.smtp.ssl.trust", "localhost");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("test@gmail.com", "unkopwn1223s");
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("from@gmail.com"));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse("to@gmail.com"));
        message.setSubject("Mail Subject");

        String msg = "This is my first email using JavaMailer \n hola guarro";

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html; charset=utf-8");


        /*String file = "path of file to be attached";
        String fileName = "attachmentName";
        DataSource source = new FileDataSource(file);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(fileName);
        multipart.addBodyPart(messageBodyPart);*/

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
        //prop.put("mail.smtp.ssl.trust", "smtp.mailtrap.io");
    }

    @Test
    public void testwithSSL() throws IOException, MessagingException {
        System.setProperty("javax.net.ssl.trustStore", "C:\\Users\\DarkDhz\\IdeaProjects\\Ubmail\\crtf\\ubmail_trust.jts");
        System.setProperty("javax.net.ssl.trustStorePassword", "ubmail");

        Properties prop = new Properties();

        prop.put("mail.smtp.host", host);
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.starttls.required", "true");
        prop.put("mail.smtp.ssl.protocols", "TLSv1.2");
        //prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("test@gmail.com", "unkopwn1223s");
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("arnau.gris@gmail.com"));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse("to@gmail.com"));
        message.setSubject("Mail Subject");

        String msg = "This is my first email using JavaMailer \n hola guarro";

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html; charset=utf-8");


        /*String file = "path of file to be attached";
        String fileName = "attachmentName";
        DataSource source = new FileDataSource(file);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(fileName);
        multipart.addBodyPart(messageBodyPart);*/

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
        //prop.put("mail.smtp.ssl.trust", "smtp.mailtrap.io");
    }

    @Test
    public void blacklistcheck() throws IOException, MessagingException {
        StringBuilder result = new StringBuilder();
        URL url = new URL("https://www.blacklistmaster.com/restapi/v1/blacklistcheck/domain/sndsllarodgift.casacam.net?apikey=7jL0GedmCBjSBNro5Vcjxy3Udffdg660");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            for (String line; (line = reader.readLine()) != null; ) {
                result.append(line);
            }
        }
        System.out.println(result.toString());
    }

}
