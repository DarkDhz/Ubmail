package es.arnaugris.utils.smtp;

import es.arnaugris.utils.MailData;
import es.arnaugris.utils.smtp.ConsoleLogger;
import es.arnaugris.utils.smtp.MailSender;

import javax.mail.MessagingException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public abstract class ProtocolUtils {

    protected BufferedReader in;
    protected BufferedWriter out;

    protected final MailData mail;
    protected final ConsoleLogger consoleLogger;
    protected boolean Ehlo = false;

    public ProtocolUtils(BufferedReader reader, BufferedWriter writer) {
        this.in = reader;
        this.out = writer;
        this.consoleLogger = new ConsoleLogger();
        this.mail = new MailData();
    }

    protected SSLSocket createSSLSocket(Socket socket) throws IOException {
        SSLSocketFactory sf = ((SSLSocketFactory) SSLSocketFactory.getDefault());
        InetSocketAddress remoteAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
        SSLSocket s = (SSLSocket) (sf.createSocket(socket, remoteAddress.getHostName(), socket.getPort(), true));

        s.setUseClientMode(false);

        //s.setEnabledProtocols(new String[] {"TLSv1", "TLSv1.1", "TLSv1.2"});

        s.setEnabledCipherSuites(s.getSupportedCipherSuites());

        return s;
    }

    protected String split_message(String message) { return message.split(" ")[0]; }

    protected void performPostMail() throws MessagingException {
        MailSender.getInstance().sendReport(this.mail);
        consoleLogger.printSended(mail.getMailFrom());

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

    protected abstract void response(String message) throws IOException;

    protected void send(String message) throws IOException {
        out.write(message + "\n");
        out.flush();
    }

    private String read() throws IOException {
        return in.readLine();
    }


}
