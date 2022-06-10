package es.arnaugris.external;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class BlacklistYaml implements YamlFile {

    // Singleton Instance
    private static volatile BlacklistYaml instance = null;

    private String api_key;
    private String shorten_key;

    private BlacklistYaml() {
    }

    public static BlacklistYaml getInstance() {
        // To ensure only one instance is created
        if (instance == null) {
            synchronized (BlacklistYaml.class) {
                if (instance == null) {
                    instance = new BlacklistYaml();
                }
            }
        }
        return instance;
    }

    public String getKey() {
        return this.api_key;
    }

    public String getShortenKey() {
        return this.shorten_key;
    }

    public void load() throws IOException {

        InputStream inputStream = Files.newInputStream(Paths.get("config/config.yml"));

        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(inputStream);
        Map<String, Object> info = (Map<String, Object>) data.get("blacklist");

        this.api_key = (String) info.get("api_key");

        info = (Map<String, Object>) data.get("shorten");

        this.shorten_key = (String) info.get("api_key");
    }

}
