package es.arnaugris.external;

import org.yaml.snakeyaml.Yaml;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

public class DomainYaml implements YamlFile{

    // Singleton Instance
    private static volatile DomainYaml instance = null;

    private int sensitive = 0;

    private DomainYaml() {}

    public static DomainYaml getInstance() {
        // To ensure only one instance is created
        if (instance == null) {
            synchronized (DomainYaml.class) {
                if (instance == null) {
                    instance = new DomainYaml();
                }
            }
        }
        return instance;
    }

    public void load() throws FileNotFoundException {

        InputStream inputStream = new FileInputStream("config/config.yml");

        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(inputStream);
        Map<String, Object> info = (Map<String, Object>) data.get("domain_check");

        this.sensitive = (int) info.get("sensitive");

    }

    public int getSensitive() {
        return this.sensitive;
    }


}
