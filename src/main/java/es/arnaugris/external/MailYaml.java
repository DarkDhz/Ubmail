package es.arnaugris.external;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class MailYaml implements YamlFile{

    private static volatile MailYaml instance = null;

    private String username;
    private String password;

    private MailYaml() {
    }

    public static MailYaml getInstance() {
        // To ensure only one instance is created
        if (instance == null) {
            synchronized (MailYaml.class) {
                if (instance == null) {
                    instance = new MailYaml();
                }
            }
        }
        return instance;
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
        Map<String, Object> info = (Map<String, Object>) data.get("mail");

        this.username = (String) info.get("username");
        this.password = (String) info.get("password");

    }
}
