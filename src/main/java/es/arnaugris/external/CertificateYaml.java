package es.arnaugris.external;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

public class CertificateYaml implements YamlFile{

    // Singleton Instance
    private static volatile CertificateYaml instance = null;

    String path;
    String password;


    private CertificateYaml() {
    }

    public static CertificateYaml getInstance() {
        // To ensure only one instance is created
        if (instance == null) {
            synchronized (CertificateYaml.class) {
                if (instance == null) {
                    instance = new CertificateYaml();
                }
            }
        }
        return instance;
    }

    public void load() throws FileNotFoundException {

        InputStream inputStream = new FileInputStream("config/config.yml");

        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(inputStream);
        Map<String, Object> info = (Map<String, Object>) data.get("ssl");

        this.path = (String) info.get("certificate_path");
        this.password = (String) info.get("pass");

    }

    public String getPath() {
        return this.path;
    }

    public String getPassword() {
        return this.password;
    }

}