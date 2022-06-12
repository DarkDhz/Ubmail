package es.arnaugris.smtp;

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
    protected boolean readingData = false;

    public ProtocolUtils(BufferedReader reader, BufferedWriter writer) {
        this.in = reader;
        this.out = writer;
        this.consoleLogger = new ConsoleLogger();
        this.mail = new MailData();
    }

    /**
     * Method to convert regular socket to SSL
     * @param socket The original socket
     * @return The SSL socket
     * @throws IOException Error converting the socket
     */
    protected SSLSocket createSSLSocket(Socket socket) throws IOException {
        SSLSocketFactory sf = ((SSLSocketFactory) SSLSocketFactory.getDefault());
        InetSocketAddress remoteAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
        SSLSocket s = (SSLSocket) (sf.createSocket(socket, remoteAddress.getHostName(), socket.getPort(), true));

        s.setUseClientMode(false);

        //s.setEnabledProtocols(new String[] {"TLSv1", "TLSv1.1", "TLSv1.2"});

        s.setEnabledCipherSuites(s.getSupportedCipherSuites());

        return s;
    }

    /**
     * Method to split line sentence
     * @param message The message to split
     * @return Split message
     */
    protected String split_message(String message) { return message.split(" ")[0]; }

    protected void performPostMail() throws MessagingException {
        MailSender.getInstance().sendReport(this.mail);
        consoleLogger.printSent(mail.getMailFrom());

    }

    /**
     * Method to handle SMTP in data
     * @throws IOException Cannot read data
     */
    public void handle() throws IOException {
        String readed;
        consoleLogger.printStart();
        send("220 Hola buenas soy el servidor");

        while (!Thread.currentThread().isInterrupted()) {
            readed = this.read();

            if (readed == null) {
                throw new IOException("close socket");
            }

            response(readed);
        }

    }

    /**
     * Method to response the command
     * @param message Command to response
     * @throws IOException Cannot response the message
     */
    protected abstract void response(String message) throws IOException;

    /**
     * Method to send data through the buffer
     * @param message Message to send
     * @throws IOException Cannot send data
     */
    protected void send(String message) throws IOException {
        out.write(message + "\n");
        out.flush();
    }

    /**
     * Method to read data from the buffer
     * @return Readed data
     * @throws IOException Cannot read data
     */
    private String read() throws IOException {
        return in.readLine();
    }


}
