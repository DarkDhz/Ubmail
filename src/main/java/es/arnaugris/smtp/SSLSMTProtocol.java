package es.arnaugris.smtp;

import es.arnaugris.smtp.ProtocolUtils;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.net.Socket;

public class SSLSMTProtocol extends ProtocolUtils {
    private Socket socket;

    public SSLSMTProtocol(Socket socket) throws IOException {
        super(new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)), new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)));
        this.socket = socket;
    }

    @Override
    protected void response(String message) throws IOException {
        String opcode = split_message(message);

        if (opcode.equalsIgnoreCase("EHLO")) {
            if (super.Ehlo) {
                mail.clear();
                this.send("250 ubmail");
            } else {
                //USE TLS
                this.send("250-smtp.arnaugris.es Hello client");
                this.send("250-SIZE 52428800");
                this.send("250-8BITMIME");
                this.send("250-PIPELINING");
                this.send("250-STARTTLS");
                this.send("250 HELP");
                this.Ehlo = true;
            }
        } else if (!this.Ehlo) {
            this.send("503 Invalid sequence of commands");
        } else if (opcode.equalsIgnoreCase("STARTTLS")) {
            if (socket instanceof SSLSocket) {
                this.send("454 TLS ALREADY ACTIVE");
                return;
            }
            this.send("220 TLS go ahead");
            SSLSocket s = super.createSSLSocket(socket);
            s.startHandshake();

            socket = s;

            super.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            super.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

            super.Ehlo = true;
            //sess.setTlsStarted(true);

            if (s.getNeedClientAuth())
            {
                try
                {
                    Certificate[] peerCertificates = s.getSession().getPeerCertificates();
                    //sess.setTlsPeerCertificates(peerCertificates);
                }
                catch (SSLPeerUnverifiedException ignored) {}
            }
        /*} else if (opcode.equalsIgnoreCase("AUTH")) {
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
            try {
                consoleLogger.printReceived(mail.getMailFrom());
                performPostMail();
            } catch (Exception ignored) {
            }

            throw new IOException("close socket");
        } else {
            mail.addData(message);
        }
    }

}
