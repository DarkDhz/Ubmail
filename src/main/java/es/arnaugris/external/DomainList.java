package es.arnaugris.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import es.arnaugris.utils.BlackListUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

public class DomainList {

    // Singleton Instance
    private static volatile DomainList instance = null;

    private ArrayList<String> domains;
    private int sensitive = 0;


    private DomainList() {
        domains = new ArrayList<>();
    }

    /**
     * Method to get class instance
     * @return The BlackListUtils instance object
     */
    public static DomainList getInstance() {
        // To ensure only one instance is created
        if (instance == null) {
            synchronized (DomainList.class) {
                if (instance == null) {
                    instance = new DomainList();
                }
            }
        }
        return instance;
    }


    public void addDomain(String dom) {
        synchronized (DomainList.class) {
            if (!this.domains.contains(dom)) {
                this.domains.add(dom);
            }
        }

    }

    public ArrayList<String> getList() {
        return this.domains;
    }

    public void load() throws FileNotFoundException {

        InputStream inputStream = new FileInputStream("config/config.yml");

        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(inputStream);
        Map<String, Object> info = (Map<String, Object>) data.get("domain_check");

        this.sensitive = (int) info.get("sensitive");
        this.domains = (ArrayList<String>) info.get("domains");
    }

    public int getSensitive() {
        return this.sensitive;
    }
}
