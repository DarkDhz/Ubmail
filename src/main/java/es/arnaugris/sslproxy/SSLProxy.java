package es.arnaugris.sslproxy;

import es.arnaugris.proxy.SocketThread;
import es.arnaugris.utils.ServerUtils;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public class SSLProxy extends ServerUtils implements Runnable{
    private final ServerSocket server;

    public SSLProxy(String host, int port) throws IOException {

        try {
            server = super.createServerSocket(host, port);
            System.out.println("Server iniciado");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new IOException("Cannot open server");
        }
    }

    // https://www.plusplushosting.net/es/blog/starttls-ssl-y-tls-cual-es-la-diferencia/
    // https://docs.oracle.com/javaee/5/tutorial/doc/bnbxw.html
    // https://stackoverflow.com/questions/34793660/how-to-create-a-secured-tcp-connection-via-tls-v-1-2-in-java

    public void run() {
        while (true) {
            try {
                Socket socket = server.accept();
                //SSLSocket sslSocket = ProxyMethods.convertSocketToSSL(socket);

                SSLSocketThread st = new SSLSocketThread(socket);

                Thread t = new Thread(st);
                t.start();
            } catch (IOException ex) {
                break;
            }

        }
    }
}
