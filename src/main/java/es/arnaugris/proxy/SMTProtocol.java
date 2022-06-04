package es.arnaugris.proxy;


import es.arnaugris.utils.smtp.ConsoleLogger;
import es.arnaugris.utils.MailData;
import es.arnaugris.utils.smtp.MailSender;

import javax.mail.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class SMTProtocol {

    private final BufferedReader in;
    private final BufferedWriter out;
    private final MailData mail;
    private boolean Ehlo = false;

    private final ConsoleLogger consoleLogger;

    public SMTProtocol(BufferedReader reader, BufferedWriter writer) {
        this.in = reader;
        this.out = writer;
        this.mail = new MailData();
        this.consoleLogger = new ConsoleLogger();



    }

    public void handle() throws IOException {
        String readed;
        consoleLogger.printStart();
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
        /*} else if (opcode.equalsIgnoreCase("AUTH")) {
            mail.auth(message);
            this.send("235 2.7.0 Authentication successful");*/
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

                consoleLogger.printReceived(mail.getMailFrom());
                performPostMail();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

            throw new IOException("close socket");
        } else {
            mail.addData(message);
        }
    }

    private void performPostMail() throws MessagingException {
        MailSender.getInstance().sendReport(this.mail);
        consoleLogger.printSended(mail.getMailFrom());

    }


    private void send(String message) throws IOException {
        out.write(message + "\n");
        out.flush();
    }

    private String read() throws IOException {
        return in.readLine();
    }
}
