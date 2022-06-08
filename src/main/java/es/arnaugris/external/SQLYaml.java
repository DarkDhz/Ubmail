package es.arnaugris.external;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class SQLYaml implements YamlFile{

    private static volatile SQLYaml instance = null;

    private String host;
    private int port;
    private String username;
    private String password;

    private String db;
    private SQLYaml() {
    }

    public static SQLYaml getInstance() {
        // To ensure only one instance is created
        if (instance == null) {
            synchronized (SQLYaml.class) {
                if (instance == null) {
                    instance = new SQLYaml();
                }
            }
        }
        return instance;
    }

    public String getDb() {
        return db;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return password;
    }


    public void load() throws IOException {

        InputStream inputStream = Files.newInputStream(Paths.get("config/config.yml"));

        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(inputStream);
        Map<String, Object> info = (Map<String, Object>) data.get("mysql");

        this.host = (String) info.get("host");
        this.port = (int) info.get("port");
        this.username = (String) info.get("username");
        this.password = (String) info.get("password");
        this.db = (String) info.get("database");

    }
}
