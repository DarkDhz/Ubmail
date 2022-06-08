package es.arnaugris;

import es.arnaugris.external.*;
import es.arnaugris.factory.ProxyFactory;
import es.arnaugris.factory.ProxyType;
import es.arnaugris.proxy.Proxy;
import es.arnaugris.sql.SQLoader;
import es.arnaugris.sslproxy.SSLProxy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class main {

    // mvn compile
    // mvn exec:java -Dexec.mainClass=es.arnaugris.main

    public static void main(String[] args) {

        //System.setProperty("javax.net.ssl.trustStore", "C:\\Users\\DarkDhz\\IdeaProjects\\Ubmail\\crtf\\ubmail_trust.jts");
        //System.setProperty("javax.net.ssl.trustStorePassword", "ubmail");

        //System.setProperty("javax.net.debug", "all");


        try {
            ArrayList<YamlFile> configuration_files = new ArrayList<>();

            configuration_files.add(DomainYaml.getInstance());
            configuration_files.add(ServerYaml.getInstance());
            configuration_files.add(BlacklistYaml.getInstance());
            configuration_files.add(CertificateYaml.getInstance());

            for (YamlFile file : configuration_files) {
                file.load();
            }


        } catch (FileNotFoundException ex) {
            System.out.println("Configuration files can't be loaded");
            System.exit(0);
        }

        CertificateYaml certificateYaml = CertificateYaml.getInstance();
        System.setProperty("javax.net.ssl.keyStore", certificateYaml.getPath());
        System.setProperty("javax.net.ssl.keyStorePassword", certificateYaml.getPassword());


        // https://www.sparkpost.com/blog/what-smtp-port/#:~:text=IANA%20initially%20assigned%20port%20465,Secure%20Sockets%20Layer%20(SSL).

        try {


            ArrayList<Runnable> servers = new ArrayList<>();
            ProxyFactory proxyFactory = ProxyFactory.getInstance();
            // SMTP Server
            servers.add(proxyFactory.createProxy(ProxyType.NORMAL));
            // SMTP SSL Server
            servers.add(proxyFactory.createProxy(ProxyType.TLS));
            // FILE TIMER LOADER
            servers.add(new SQLoader());

            for (Runnable threadServer : servers) {
                Thread t = new Thread(threadServer);
                t.start();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("ERROR OPENING SERVERS, CHECK CONFIGURATION!");
        }

    }
}
