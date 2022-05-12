package es.arnaugris.proxy;


import es.arnaugris.utils.MailData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;

public class SMTProtocol {

    private final BufferedReader in;
    private final BufferedWriter out;
    private final MailData mail;
    private boolean Ehlo = false;

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
        // TODO TIMEOUT (5 minutes)
        String opcode = split_message(message);

        if (opcode.equalsIgnoreCase("EHLO")) {
            if (this.Ehlo) {
                mail.clear();
            }
            //this.send("250 ubmail");

            this.send("250-smtp.example.com Hello client.example.com");
            this.send("250 AUTH GSSAPI DIGEST-MD5 PLAIN");
            this.Ehlo = true;
        } else if (!this.Ehlo) {
            this.send("503 Invalid secuence of commands");
        } else if (opcode.equalsIgnoreCase("VRFY")) {
            this.send("250 OK");
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

            System.out.println("----------------- DATA -----------------");
            System.out.println(mail.extractMessage());
            System.out.println("----------------- URLS -----------------");
            System.out.println(mail.getURLs());

            // do the checks
            mail.checkAll();

            System.out.println("----------------- BLACKLIST -----------------");

            Map<String, Boolean> black_list = mail.getBlacklist();

            for (Map.Entry<String, Boolean> entry : black_list.entrySet()) {
                if (entry.getValue()) {
                    System.out.println("URL " + entry.getKey() + " is inside a blacklist!");
                }
            }

            System.out.println("----------------- SIMILAR -----------------");

            Map<String, String> similar = mail.getSimilarityDomains();

            for (Map.Entry<String, String> entry : similar.entrySet()) {
                System.out.println("" + entry.getKey() + " similar to " + entry.getValue());
            }

            System.out.println("----------------- SHORTEN -----------------");

            Map<String, Boolean> shorten = mail.getShorten();

            System.out.println("SHORTEN URL CAN BE DANGEROUS!");

            for (Map.Entry<String, Boolean> entry : shorten.entrySet()) {
                if (entry.getValue()) {
                    System.out.println("URL " + entry.getKey() + " is using URL-SHORTEN service.");
                }
            }

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
