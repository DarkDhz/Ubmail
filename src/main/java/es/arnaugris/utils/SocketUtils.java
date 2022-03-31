package es.arnaugris.utils;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketUtils {

    protected SSLSocket createSSLSocket(Socket socket) throws IOException {
        SSLSocketFactory sf = ((SSLSocketFactory) SSLSocketFactory.getDefault());
        InetSocketAddress remoteAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
        SSLSocket s = (SSLSocket) (sf.createSocket(socket, remoteAddress.getHostName(), socket.getPort(), true));

        s.setUseClientMode(false);
        s.setEnabledCipherSuites(s.getSupportedCipherSuites());

        return s;
    }


}
