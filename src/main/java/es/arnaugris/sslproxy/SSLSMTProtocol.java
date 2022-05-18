package es.arnaugris.sslproxy;

import es.arnaugris.utils.MailData;
import es.arnaugris.utils.SocketUtils;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.security.cert.Certificate;
import java.net.Socket;

public class SSLSMTProtocol extends SocketUtils {

    private final BufferedReader in;
    private final BufferedWriter out;
    private Socket socket;
    private final MailData mail;
    private boolean reset = false;

    public SSLSMTProtocol(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "8859_1"));;
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "8859_1"));;
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
            if (reset) {
                this.send("250 ubmail");
            } else {
                //USE TLS
                this.send("250-smtp.arnaugris.es Hello client");
                this.send("250-SIZE 52428800");
                this.send("250-8BITMIME");
                this.send("250-PIPELINING");
                this.send("250-STARTTLS");
                this.send("250 HELP");
            }
        } else if (opcode.equalsIgnoreCase("STARTTLS")) {
            if (socket instanceof SSLSocket) {
                this.send("454 TLS ALREADY ACTIVE");
                return;
            }
            this.send("220 TLS go ahead");
            SSLSocket s = super.createSSLSocket(socket);
            s.startHandshake();

            socket = s;

            reset = true;
            //sess.setTlsStarted(true);

            if (s.getNeedClientAuth())
            {
                try
                {
                    Certificate[] peerCertificates = s.getSession().getPeerCertificates();
                    //sess.setTlsPeerCertificates(peerCertificates);
                }
                catch (SSLPeerUnverifiedException e)
                {
                    // IGNORE, just leave the certificate chain null
                }
            }
        } else if (opcode.equalsIgnoreCase("AUTH")) {
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
            System.out.println(mail.getMail_from());
            System.out.println(mail.getMailTo());
            System.out.println(mail.getData());
            throw new IOException("close socket");
        } else {
            //System.out.println("hola");
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

    private void updateSocket(Socket socket) throws IOException {
        /*this.socket = socket;
        this.input = this.socket.getInputStream();
        this.out = new CRLFTerminatedReader(this.input);
        this.writer = new PrintWriter(this.socket.getOutputStream());

        this.socket.setSoTimeout(this.server.getConnectionTimeout());*/
    }
}
