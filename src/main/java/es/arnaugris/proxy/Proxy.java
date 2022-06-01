package es.arnaugris.proxy;

import es.arnaugris.utils.ServerUtils;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Proxy extends ServerUtils implements Runnable{

    private ServerSocket server;

    public Proxy(String host, int port) throws IOException {
        try {
            server = super.createServerSocket(host, port);
            System.out.println("Open server on " + host + ":" + port);
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
                break;
            }


        }

    }
}
