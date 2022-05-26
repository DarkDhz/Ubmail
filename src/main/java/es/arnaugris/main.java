package es.arnaugris;

import es.arnaugris.external.DomainList;
import es.arnaugris.external.ServerYaml;
import es.arnaugris.proxy.Proxy;
import es.arnaugris.sslproxy.SSLProxy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

// https://metamug.com/article/java/build-run-java-maven-project-command-line.html
// https://github.com/bcoe/secure-smtpd

public class main {

    // mvn compile
    // mvn exec:java -Dexec.mainClass=es.arnaugris.main

    public static void main(String[] args) {
        System.setProperty("javax.net.ssl.keyStore", "C:\\Users\\DarkDhz\\IdeaProjects\\Ubmail\\crtf\\ubmail.jks");
        //System.setProperty("javax.net.ssl.trustStore", "C:\\Users\\DarkDhz\\IdeaProjects\\Ubmail\\crtf\\ubmail_trust.jts");
        //System.setProperty("javax.net.ssl.trustStorePassword", "ubmail");
        System.setProperty("javax.net.ssl.keyStorePassword", "ubmail");
        //System.setProperty("javax.net.debug", "all");

        ServerYaml server = null;

        try {
            DomainList domains = DomainList.getInstance();
            server = ServerYaml.getInstance();
            domains.load();
        } catch (FileNotFoundException ex) {
            System.out.printf("Domains can't be loaded");
            System.exit(0);
        }


        try {
            Proxy proxy = new Proxy(server.getIP(), server.getPort());
            Thread t = new Thread(proxy);
            t.start();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("cannot open the server on port 25");
        }


        try {
            SSLProxy sslProxy = new SSLProxy(server.getIP(), server.getSSlPort());
            Thread t2 = new Thread(sslProxy);
            t2.start();
        } catch (IOException ex) {
            System.out.println("cannot open the server on port 465");
        }



    }
}
