package es.arnaugris.proxy;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
// https://stackoverflow.com/questions/66534689/how-do-i-code-my-own-smtp-server-using-java
// https://es.wikipedia.org/wiki/Protocolo_para_transferencia_simple_de_correo

public class SocketThread implements Runnable {

    private final Socket socket;
    private SSLSocket mail_service;

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
