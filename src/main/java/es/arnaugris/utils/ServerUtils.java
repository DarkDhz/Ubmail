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


}
