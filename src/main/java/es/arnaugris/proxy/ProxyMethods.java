package es.arnaugris.proxy;


import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.Socket;

public class ProxyMethods {

    public static SSLSocket convertSocketToSSL(Socket socket) throws IOException {
        return (SSLSocket) ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket(
                socket,
                socket.getInetAddress().getHostAddress(),
                socket.getPort(),
                true);

    }
}
