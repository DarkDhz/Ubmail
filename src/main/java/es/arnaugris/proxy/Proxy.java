package es.arnaugris.proxy;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Proxy {
    ServerSocket server;
    public Proxy(String host, int port) {
        try {
            server = new ServerSocket(port);
            System.out.println("Server iniciado");
        } catch (IOException e) {
            System.out.println("Cannot open server");
            System.exit(0);
        }
        while(true) {
            try {
                Socket socket = server.accept();
                SSLSocket sslSocket = ProxyMethods.convertSocketToSSL(socket);

                SocketThread st = new SocketThread(sslSocket);

                Thread t = new Thread(st);
                t.start();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.exit(0);
            }


        }
    }

}
