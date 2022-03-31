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
            final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "8859_1"));
            final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "8859_1"));

            SMTProtocol protocol_handler = new SMTProtocol(in, out);
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
