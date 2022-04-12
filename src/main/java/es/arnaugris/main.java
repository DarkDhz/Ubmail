package es.arnaugris;

import es.arnaugris.proxy.Proxy;
import es.arnaugris.sslproxy.SSLProxy;

import java.io.IOException;

// https://metamug.com/article/java/build-run-java-maven-project-command-line.html
// https://github.com/bcoe/secure-smtpd

public class main {

    public static void main(String[] args) {
        try {
            Proxy proxy = new Proxy("127.0.0.1", 25);
            Thread t = new Thread(proxy);
            t.start();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("cannot open the server on port 25");
        }

        /*try {
            SSLProxy sslProxy = new SSLProxy("54.36.191.29", 465);
            Thread t2 = new Thread(sslProxy);
            t2.start();
        } catch (IOException ex) {
            System.out.println("cannot open the server on port 465");
        }*/



    }
}
