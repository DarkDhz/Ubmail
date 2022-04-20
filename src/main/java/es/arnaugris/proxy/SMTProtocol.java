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
            //this.send("250 ubmail");

            /*//USE TLS
            this.send("250-smtp.server.com");
            this.send("250-SIZE 52428800");
            this.send("250-8BITMIME");
            this.send("250-PIPELINING");
            this.send("250-STARTTLS");
            this.send("250 HELP");
            */

            this.send("250-smtp.example.com Hello client.example.com");
            this.send("250 AUTH GSSAPI DIGEST-MD5 PLAIN");

        } else if (opcode.equalsIgnoreCase("STARTTLS")) {
            this.send("220 TLS go ahead");
        } else if (opcode.equalsIgnoreCase("AUTH")) {
            mail.auth(message);
            this.send("235 2.7.0 Authentication successful");
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
            System.out.println("----------------- DATA -----------------");
            System.out.println(mail.extractMessage());
            System.out.println("----------------- URLS -----------------");
            System.out.println(mail.getURLs());
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
