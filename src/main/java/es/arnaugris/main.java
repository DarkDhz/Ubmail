package es.arnaugris;

import es.arnaugris.external.BlacklistYaml;
import es.arnaugris.external.DomainYaml;
import es.arnaugris.external.ServerYaml;
import es.arnaugris.external.YamlFile;
import es.arnaugris.proxy.Proxy;
import es.arnaugris.sslproxy.SSLProxy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class main {

    // mvn compile
    // mvn exec:java -Dexec.mainClass=es.arnaugris.main

    public static void main(String[] args) {
        System.setProperty("javax.net.ssl.keyStore", "C:\\Users\\DarkDhz\\IdeaProjects\\Ubmail\\crtf\\ubmail.jks");
        //System.setProperty("javax.net.ssl.trustStore", "C:\\Users\\DarkDhz\\IdeaProjects\\Ubmail\\crtf\\ubmail_trust.jts");
        //System.setProperty("javax.net.ssl.trustStorePassword", "ubmail");
        System.setProperty("javax.net.ssl.keyStorePassword", "ubmail");
        //System.setProperty("javax.net.debug", "all");


        try {
            ArrayList<YamlFile> configuration_files = new ArrayList<>();

            configuration_files.add(DomainYaml.getInstance());
            configuration_files.add(ServerYaml.getInstance());
            configuration_files.add(BlacklistYaml.getInstance());

            for (YamlFile file : configuration_files) {
                file.load();
            }


        } catch (FileNotFoundException ex) {
            System.out.println("Configuration files can't be loaded");
            System.exit(0);
        }

        try {
            ServerYaml server = ServerYaml.getInstance();

            ArrayList<Runnable> servers = new ArrayList<>();
            // SMTP Server
            servers.add(new Proxy(server.getIP(), server.getPort()));
            // SMTP SSL Server
            servers.add(new SSLProxy(server.getIP(), server.getSSlPort()));

            for (Runnable threadServer : servers) {
                Thread t = new Thread(threadServer);
                t.start();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("cannot open the server");
        }

    }
}
