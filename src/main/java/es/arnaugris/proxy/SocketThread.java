package es.arnaugris.proxy;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
// https://stackoverflow.com/questions/66534689/how-do-i-code-my-own-smtp-server-using-java
// https://es.wikipedia.org/wiki/Protocolo_para_transferencia_simple_de_correo

public class SocketThread implements Runnable {

    private final Socket socket;
    private SSLSocket mail_service;

    public SocketThread(Socket soc) {
        socket = soc;
        /*try {
            Socket s = new Socket("smtp.email.host", 25);
            mail_service = ProxyMethods.convertSocketToSSL(s);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        }*/

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
