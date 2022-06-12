package es.arnaugris.external;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class ServerYaml implements YamlFile{

    private static volatile ServerYaml instance = null;

    private String ip;
    private int port;
    private int ssl_port;

    private int tls_port;

    private ServerYaml() { }

    public static ServerYaml getInstance() {
        // To ensure only one instance is created
        if (instance == null) {
            synchronized (ServerYaml.class) {
                if (instance == null) {
                    instance = new ServerYaml();
                }
            }
        }
        return instance;
    }

    /**
     * Method to load data from file
     * @throws IOException file not found
     */
    public void load() throws IOException {

        InputStream inputStream = Files.newInputStream(Paths.get("config/config.yml"));

        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(inputStream);

        Map<String, Object> server = (Map<String, Object>) data.get("server");

        this.ip = (String) server.get("ip");
        this.port = (int) server.get("port");
        this.ssl_port = (int) server.get("ssl_port");
        this.tls_port = (int) server.get("tls_port");
    }

    /**
     * Method to get no encrypted port
     * @return The port
     */
    public int getPort() { return this.port; }

    /**
     * Method to get SSL port
     * @return The port
     */
    public int getSSlPort() { return this.ssl_port; }

    /**
     * Method to get TLS port
     * @return The port
     */
    public int getTLSPort() { return this.tls_port; }

    /**
     * Method to get server IP
     * @return The IPv4
     */
    public String getIP() { return this.ip; }

}
