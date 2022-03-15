package es.arnaugris.proxy;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class SMTProtocol {

    private final BufferedReader in;
    private final BufferedWriter out;
    private MailData mail;

    public SMTProtocol(BufferedReader reader, BufferedWriter writer) {
        this.in = reader;
        this.out = writer;
        mail = new MailData();
    }

    public void handle() throws IOException {
        String readed = "nothing";
        send("220 Hola buenas soy el servidor");

        while (true) {
            readed = this.read();

            if (readed == null) {
                throw new IOException("close socket");
            }

            response(readed);
        }



        /*System.out.println(in.readLine());

        out.write("250 Hola buenas soy el servidor " + "\n");
        out.flush();

        System.out.println(in.readLine());

        out.write("250 OK " + "\n");
        out.flush();

        System.out.println(in.readLine());

        out.write("250 OK " + "\n");
        out.flush();*/

    }

    private String split_message(String message) {
        return message.split(" ")[0];

    }

    private void response(String message) throws IOException {
        System.out.println(message);
        String header = split_message(message);

        if (header.equalsIgnoreCase("EHLO")) {
            this.send("250 HELLO");
        } else if (header.equalsIgnoreCase("MAIL")) {
            this.send("250 OK");
            //TODO
        } else if (header.equalsIgnoreCase("RCPT")) {
            this.send("250 OK");
            //TODO
        } else if (header.equalsIgnoreCase("DATA")) {
            this.send("354 OK");
            //TODO
        } else if (header.equalsIgnoreCase(".")) {
            this.send("250 OK");
            //TODO
        } else if (header.equalsIgnoreCase("QUIT")) {
            this.send("221 Bye");
            throw new IOException("close socket");
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
