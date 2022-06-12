package es.arnaugris.external;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    /**
     * Method to load data from file
     * @throws IOException file not found
     */
    public void load() throws IOException {

        InputStream inputStream = Files.newInputStream(Paths.get("config/config.yml"));

        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(inputStream);
        Map<String, Object> info = (Map<String, Object>) data.get("ssl");

        this.path = (String) info.get("certificate_path");
        this.password = (String) info.get("pass");

    }

    /**
     * Method to get the certificate path
     * @return The path
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Method to get the password of the cert vault
     * @return The password
     */
    public String getPassword() {
        return this.password;
    }

}
