package es.arnaugris.proxy;


import es.arnaugris.utils.smtp.ProtocolUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class SMTProtocol extends ProtocolUtils {

    public SMTProtocol(BufferedReader reader, BufferedWriter writer) {
        super(reader, writer);
    }

    @Override
    protected void response(String message) throws IOException {
        String opcode = super.split_message(message);

        if (opcode.equalsIgnoreCase("EHLO")) {
            if (this.Ehlo) {
                mail.clear();
            }
            this.send("250 smtp.arnaugris.es Hello client");

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
                super.performPostMail();
            } catch (Exception ignored) {
            }

            throw new IOException("close socket");
        } else {
            mail.addData(message);
        }
    }





}
