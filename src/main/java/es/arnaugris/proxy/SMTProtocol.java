package es.arnaugris.proxy;


import es.arnaugris.utils.MailData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class SMTProtocol {

    private final BufferedReader in;
    private final BufferedWriter out;
    private final MailData mail;

    public SMTProtocol(BufferedReader reader, BufferedWriter writer) {
        this.in = reader;
        this.out = writer;
        this.mail = new MailData();
    }

    public void handle() throws IOException {
        String readed;
        send("220 Hola buenas soy el servidor");

        while (true) {
            readed = this.read();
            System.out.println(readed);

            if (readed == null) {
                throw new IOException("close socket");
            }

            response(readed);
        }

    }

    private String split_message(String message) { return message.split(" ")[0]; }

    private void response(String message) throws IOException {
        String opcode = split_message(message);

        if (opcode.equalsIgnoreCase("EHLO")) {
            this.send("250 ubmail");
            //this.send("250-smtp.server.com");
            //this.send("250-AUTH GSSAPI DIGEST-MD5");
            //this.send("250-ENHANCEDSTATUSCODES");
            //this.send("250 STARTTLS");
        } else if (opcode.equalsIgnoreCase("STARTTLS")) {
            this.send("220 Ready to start TLS");
        } else if (opcode.equalsIgnoreCase("MAIL")) {
            this.send("250 OK");
            mail.clearAndSetMail_from(message);
        } else if (opcode.equalsIgnoreCase("RCPT")) {
            this.send("250 OK");
            mail.clearAndAddMail_to(message);
        } else if (opcode.equalsIgnoreCase("DATA")) {
            this.send("354 OK");
        } else if (opcode.equalsIgnoreCase(".")) {
            this.send("250 OK");
        } else if (opcode.equalsIgnoreCase("QUIT")) {
            this.send("221 Bye");
            System.out.println(mail.getMail_from());
            System.out.println(mail.getMailTo());
            System.out.println(mail.getData());
            throw new IOException("close socket");
        } else {
            mail.addData(message);
        }
    }


    private void send(String message) throws IOException {
        out.write(message + "\n");
        out.flush();
    }

    private String read() throws IOException {
        return in.readLine();
    }
}
