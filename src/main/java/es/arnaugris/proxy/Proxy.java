package es.arnaugris.proxy;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Proxy implements Runnable{

    private ServerSocket server;

    public Proxy(String host, int port) throws IOException {
        try {
            server = new ServerSocket(port, 90, InetAddress.getByName(host));
            System.out.println("Server iniciado");
        } catch (IOException e) {
            throw new IOException("Cannot open server");
            //System.exit(0);
        }
    }

    public void run() {
        while(true) {
            try {
                Socket socket = server.accept();
                //SSLSocket sslSocket = ProxyMethods.convertSocketToSSL(socket);

                SocketThread st = new SocketThread(socket);

                Thread t = new Thread(st);
                t.start();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.exit(0);
            }


        }

    }
}
