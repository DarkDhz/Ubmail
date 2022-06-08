package es.arnaugris.sslproxy;

import es.arnaugris.utils.server.ServerUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SSLProxy extends ServerUtils implements Runnable{
    private final ServerSocket server;

    public SSLProxy(String host, int port) throws IOException {

        try {
            server = super.createServerSocket(host, port);
            System.out.println("Open SSL server on " + host + ":" + port);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new IOException("Cannot open server");
        }
    }


    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Socket socket = server.accept();

                SSLSocketThread st = new SSLSocketThread(socket);

                Thread t = new Thread(st);
                t.start();
            } catch (IOException ex) {
                Thread.currentThread().interrupt();
                break;
            }

        }
    }
}
