package es.arnaugris.sslproxy;

import es.arnaugris.proxy.SocketThread;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public class SSLProxy implements Runnable{
    private ServerSocket server;

    public SSLProxy(String host, int port) throws IOException {
        System.setProperty("javax.net.ssl.keyStore", "C:\\Program Files\\Java\\jdk1.8.0_191\\bin\\keystore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "papazer0");

        SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        try {
            server  = ssf.createServerSocket(port);
            System.out.println("Server iniciado");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new IOException("Cannot open server");
            //System.exit(0);
        }
    }

    // https://www.plusplushosting.net/es/blog/starttls-ssl-y-tls-cual-es-la-diferencia/
    // https://docs.oracle.com/javaee/5/tutorial/doc/bnbxw.html
    // https://stackoverflow.com/questions/34793660/how-to-create-a-secured-tcp-connection-via-tls-v-1-2-in-java

    public void run() {
        while (true) {
            try {
                System.out.println("awaiting to accept..");
                Socket s = server.accept();

                SSLSession session = ((SSLSocket) s).getSession();
                Certificate[] cchain2 = session.getLocalCertificates();
                for (int i = 0; i < cchain2.length; i++) {
                    System.out.println(((X509Certificate) cchain2[i]).getSubjectDN());
                }
                System.out.println("Peer host is " + session.getPeerHost());
                System.out.println("Cipher is " + session.getCipherSuite());
                System.out.println("Protocol is " + session.getProtocol());
                System.out.println("ID is " + new BigInteger(session.getId()));
                System.out.println("Session created in " + session.getCreationTime());
                System.out.println("Session accessed in " + session.getLastAccessedTime());
            } catch (IOException ex) {

            }

        }
    }
}
