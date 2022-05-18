package es.arnaugris;

import es.arnaugris.external.DomainList;
import es.arnaugris.proxy.Proxy;
import es.arnaugris.sslproxy.SSLProxy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

// https://metamug.com/article/java/build-run-java-maven-project-command-line.html
// https://github.com/bcoe/secure-smtpd

public class main {

    public static void main(String[] args) {
        // java location C:\Program Files\Java\jdk1.8.0_301\bin

        // https://docs.oracle.com/cd/E19509-01/820-3503/6nf1il6er/index.html
        // https://stackoverflow.com/questions/2138574/java-path-to-truststore-set-property-doesnt-work
        System.setProperty("javax.net.ssl.keyStore", "C:\\Users\\DarkDhz\\IdeaProjects\\Ubmail\\crtf\\ubmail.jks");
        //System.setProperty("javax.net.ssl.trustStore", "C:\\Users\\DarkDhz\\IdeaProjects\\Ubmail\\crtf\\ubmail_trust.jts");
        //System.setProperty("javax.net.ssl.trustStorePassword", "ubmail");
        System.setProperty("javax.net.ssl.keyStorePassword", "ubmail");
        //System.setProperty("javax.net.debug", "all");

        DomainList domains = DomainList.getInstance();

        try {
            domains.load();
        } catch (FileNotFoundException ex) {
            System.out.printf("Domains can't be loaded");
        }

        try {
            Proxy proxy = new Proxy("127.0.0.1", 25);
            Thread t = new Thread(proxy);
            t.start();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("cannot open the server on port 25");
        }


        try {
            SSLProxy sslProxy = new SSLProxy("127.0.0.1", 465);
            Thread t2 = new Thread(sslProxy);
            t2.start();
        } catch (IOException ex) {
            System.out.println("cannot open the server on port 465");
        }



    }
}
