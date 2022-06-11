package es.arnaugris.proxy;

import es.arnaugris.factory.ProxyType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Proxy implements Runnable {

    private final ServerSocket server;
    private final ProxyType proxyType;

    public Proxy(String host, int port, ProxyType type) throws IOException {
        this.proxyType = type;
        try {
            server = this.createServerSocket(host, port);
            System.out.println("Open server on " + host + ":" + port);
        } catch (IOException e) {
            throw new IOException("Cannot open server");
        }
    }

    private ServerSocket createServerSocket(String ip, int port) throws IOException {
        InetSocketAddress isa = new InetSocketAddress(ip, port);
        ServerSocket server = new ServerSocket();
        server.bind(isa, 50);
        return server;
    }

    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                Socket socket = server.accept();

                Runnable st = new SocketThread(socket, this.proxyType);

                Thread t = new Thread(st);
                t.start();
            } catch (IOException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
