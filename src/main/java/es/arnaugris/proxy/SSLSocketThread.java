package es.arnaugris.proxy;

import es.arnaugris.smtp.SSLSMTProtocol;

import java.io.*;
import java.net.Socket;

public class SSLSocketThread implements Runnable {

    private final Socket socket;

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
