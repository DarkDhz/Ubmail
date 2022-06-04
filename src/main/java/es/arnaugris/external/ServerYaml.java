package es.arnaugris.external;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

public class ServerYaml implements YamlFile{

    private static volatile ServerYaml instance = null;

    private String ip;
    private int port;
    private int ssl_port;

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

    public void load() throws FileNotFoundException {

        InputStream inputStream = new FileInputStream("config/config.yml");

        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(inputStream);

        Map<String, Object> server = (Map<String, Object>) data.get("server");

        this.ip = (String) server.get("ip");
        this.port = (int) server.get("port");
        this.ssl_port = (int) server.get("ssl_port");
    }

    public int getPort() { return this.port; }

    public int getSSlPort() { return this.ssl_port; }

    public String getIP() { return this.ip; }

}
