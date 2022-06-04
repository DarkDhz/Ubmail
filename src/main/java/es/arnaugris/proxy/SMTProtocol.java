package es.arnaugris.proxy;


import es.arnaugris.utils.IDManager;
import es.arnaugris.utils.MailData;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class SMTProtocol {

    private final BufferedReader in;
    private final BufferedWriter out;
    private final MailData mail;
    private boolean Ehlo = false;

    private final int id;

    public SMTProtocol(BufferedReader reader, BufferedWriter writer) {
        this.in = reader;
        this.out = writer;
        this.mail = new MailData();

        IDManager idManager = IDManager.getInstance();
        this.id = idManager.getNextID();

    }

    public void handle() throws IOException {
        String readed;
        System.out.println("(" + this.getActualTime() +  " ID " + this.id + ") STARTED HANDLING NEW REQUEST");
        send("220 Hola buenas soy el servidor");

        while (true) {
            readed = this.read();

            if (readed == null) {
                throw new IOException("close socket");
            }

            response(readed);
        }

    }

    private String split_message(String message) { return message.split(" ")[0]; }

    private void response(String message) throws IOException {
        // TODO TIMEOUT (5 minutes)
        String opcode = split_message(message);

        if (opcode.equalsIgnoreCase("EHLO")) {
            if (this.Ehlo) {
                mail.clear();
            }
            this.send("250 ubmail");

            //this.send("250-smtp.arnaugris.es Hello client");
            //this.send("250 AUTH GSSAPI DIGEST-MD5 PLAIN");
            this.Ehlo = true;
        } else if (!this.Ehlo) {
            this.send("503 Invalid secuence of commands");
        } else if (opcode.equalsIgnoreCase("VRFY")) {
            this.send("250 OK");
        } else if (opcode.equalsIgnoreCase("AUTH")) {
            mail.auth(message);
            this.send("235 2.7.0 Authentication successful");
        } else if (opcode.equalsIgnoreCase("MAIL")) {
            mail.clearAndSetMail_from(message);
            this.send("250 OK");
        } else if (opcode.equalsIgnoreCase("RCPT")) {
            mail.clearAndAddMail_to(message);
            this.send("250 OK");
        } else if (opcode.equalsIgnoreCase("RSET")) {
            this.send("250 OK");
            mail.clear();
        } else if (opcode.equalsIgnoreCase("NOOP")) {
            this.send("250 OK");
        } else if (opcode.equalsIgnoreCase("DATA")) {
            this.send("354 OK");
        } else if (opcode.equalsIgnoreCase(".")) {
            this.send("250 OK");
        } else if (opcode.equalsIgnoreCase("QUIT")) {
            this.send("221 Bye");
            // do the checks
            try {

                System.out.println("(" + this.getActualTime() +  " ID " + this.id + ") MAIL RECEIVED FROM " + mail.getMailFrom());
                performPostMail();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

            throw new IOException("close socket");
        } else {
            mail.addData(message);
        }
    }

    private String getActualTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    private void performPostMail() throws MessagingException {
        String report = this.mail.getReport();

        sendReport(report);
    }

    private void sendReport(String report) throws MessagingException {
        Properties prop = new Properties();

        prop.put("mail.smtp.host", "ssl0.ovh.net");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.ssl.protocols", "TLSv1.2");
        prop.put("mail.smtp.ssl.trust", "ssl0.ovh.net");

        String from = "postmaster@darkhorizon.es";
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, "ubmail12345");
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse(this.mail.getMailFrom()));
        message.setSubject("MAIL REPORT FROM " + mail.getMailFrom());

        report += "<p> Original Message RAW </p>" + mail.getMessage();
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(report, "text/html; charset=utf-8");


        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
        System.out.println("(" + this.getActualTime() +  " ID " + this.id + ") REPORT SENT TO " + mail.getMailFrom());
    }

    private void send(String message) throws IOException {
        out.write(message + "\n");
        out.flush();
    }

    private String read() throws IOException {
        return in.readLine();
    }
}
