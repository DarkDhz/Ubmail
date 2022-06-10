package es.arnaugris.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Proxy extends ServerUtils implements Runnable {

    private final ServerSocket server;

    public Proxy(String host, int port) throws IOException {
        try {
            server = super.createServerSocket(host, port);
            System.out.println("Open server on " + host + ":" + port);
        } catch (IOException e) {
            throw new IOException("Cannot open server");
        }
    }

    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                Socket socket = server.accept();

                SocketThread st = new SocketThread(socket);

                Thread t = new Thread(st);
                t.start();
            } catch (IOException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
