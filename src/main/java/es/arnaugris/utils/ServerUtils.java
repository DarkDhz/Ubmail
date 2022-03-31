package es.arnaugris.utils;

import com.sun.security.ntlm.Server;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerUtils {

    public ServerUtils() {}

    protected ServerSocket createServerSocket(String ip, int port) throws IOException {
        InetSocketAddress isa = new InetSocketAddress(ip, port);
        ServerSocket server = new ServerSocket();
        server.bind(isa, 50);
        return server;
    }

    protected SSLSocket createSSLSocket(Socket socket) throws IOException {
        SSLSocketFactory sf = ((SSLSocketFactory) SSLSocketFactory.getDefault());
        InetSocketAddress remoteAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
        SSLSocket s = (SSLSocket) (sf.createSocket(socket, remoteAddress.getHostName(), socket.getPort(), true));

        s.setUseClientMode(false);
        s.setEnabledCipherSuites(s.getSupportedCipherSuites());

        return s;
    }
}
