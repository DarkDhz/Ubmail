package es.arnaugris.external;

import java.io.IOException;

public interface  YamlFile {

    /**
     * Method to load data from file
     * @throws IOException file not found
     */
    void load() throws IOException;
}
