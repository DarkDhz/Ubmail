package es.arnaugris.utils.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

public class ServerUtils {

    public ServerUtils() {}

    protected ServerSocket createServerSocket(String ip, int port) throws IOException {
        InetSocketAddress isa = new InetSocketAddress(ip, port);
        ServerSocket server = new ServerSocket();
        server.bind(isa, 50);
        return server;
    }


}
