package es.arnaugris.proxy;

import es.arnaugris.factory.ProxyType;
import es.arnaugris.smtp.SMTProtocol;
import es.arnaugris.smtp.SSLSMTProtocol;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class SocketThread implements Runnable {

    private final Socket socket;
    private final ProxyType proxyType;

    public SocketThread(Socket soc, ProxyType type) {
        socket = soc;
        this.proxyType = type;

    }

    /**
     * Run the SMTP thread
     */
    public void run() {
        try {
            switch (this.proxyType) {
                case NORMAL:
                    final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                    final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

                    SMTProtocol protocol_handler = new SMTProtocol(in, out);
                    protocol_handler.handle();
                    break;
                case TLS:
                case SSL:
                    SSLSMTProtocol ssl_protocol_handler = new SSLSMTProtocol(socket);
                    ssl_protocol_handler.handle();
                    break;
            }
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
