package es.arnaugris.external;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

public class BlacklistYaml implements YamlFile {

    // Singleton Instance
    private static volatile BlacklistYaml instance = null;

    private String api_key;

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

    public void load() throws FileNotFoundException {

        InputStream inputStream = new FileInputStream("config/config.yml");

        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(inputStream);
        Map<String, Object> info = (Map<String, Object>) data.get("blacklist");

        this.api_key = (String) info.get("api_key");
    }

}
