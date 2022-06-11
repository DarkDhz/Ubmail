package es.arnaugris.factory;


import es.arnaugris.external.ServerYaml;
import es.arnaugris.proxy.Proxy;

import java.io.IOException;

public class ProxyFactory {

    // Singleton Instance
    private static volatile ProxyFactory instance = null;

    private ProxyFactory() {

    }

    public static ProxyFactory getInstance() {
        // To ensure only one instance is created
        if (instance == null) {
            synchronized (ProxyFactory.class) {
                if (instance == null) {
                    instance = new ProxyFactory();
                }
            }
        }
        return instance;
    }

    public Runnable createProxy(ProxyType type) throws IOException {
        ServerYaml server = ServerYaml.getInstance();
        switch (type)  {
            case NORMAL:
                return new Proxy(server.getIP(), server.getPort(), type);
            case SSL:
                return new Proxy(server.getIP(), server.getSSlPort(), type);
            case TLS:
                return new Proxy(server.getIP(), server.getTLSPort(), type);
            default:
                return null;
        }
    }
}
