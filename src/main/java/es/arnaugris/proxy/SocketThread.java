package es.arnaugris.proxy;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketThread implements Runnable {

    SSLSocket socket;
    SSLSocket mail_service;

    public SocketThread(SSLSocket soc) {
        socket = soc;
        try {
            Socket s = new Socket("smtp.email.host", 25);
            mail_service = ProxyMethods.convertSocketToSSL(s);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(mail_service.getOutputStream(), true);

            System.out.println("hpla");
            System.out.println(reader.readLine());
            writer.println("HOLA");

        } catch (IOException e) {
            // todo
        }
    }
}
