package es.arnaugris;

import es.arnaugris.proxy.Proxy;
import es.arnaugris.sslproxy.SSLProxy;

import java.io.IOException;

public class main {

    public static void main(String[] args) {
        try {
            Proxy proxy = new Proxy("localhost", 25);
            Thread t = new Thread(proxy);
            t.start();
        } catch (IOException ex) {
            System.out.println("cannot open the server on port 25");
        }

        try {
            SSLProxy sslProxy = new SSLProxy("localhost", 465);
            Thread t2 = new Thread(sslProxy);
            t2.start();
        } catch (IOException ex) {
            System.out.println("cannot open the server on port 465");
        }



    }
}
