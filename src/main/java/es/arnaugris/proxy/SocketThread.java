package es.arnaugris.proxy;

import es.arnaugris.protocol.SMTProtocol;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class SocketThread implements Runnable {

    private final Socket socket;

    public SocketThread(Socket soc) {
        socket = soc;

    }

    public void run() {
        try {
            final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

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
