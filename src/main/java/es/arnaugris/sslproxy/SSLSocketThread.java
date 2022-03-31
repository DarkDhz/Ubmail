package es.arnaugris.sslproxy;

import es.arnaugris.proxy.SMTProtocol;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.Socket;

public class SSLSocketThread implements Runnable {

    private final Socket socket;
    private SSLSocket mail_service;

    public SSLSocketThread(Socket soc) {
        socket = soc;
    }

    public void run() {
        try {

            SSLSMTProtocol protocol_handler = new SSLSMTProtocol(socket);
            protocol_handler.handle();

        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
