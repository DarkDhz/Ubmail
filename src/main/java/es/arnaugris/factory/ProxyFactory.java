package es.arnaugris.factory;


import es.arnaugris.external.ServerYaml;
import es.arnaugris.proxy.Proxy;
import es.arnaugris.sslproxy.SSLProxy;

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
                return new Proxy(server.getIP(), server.getPort());
            case SSL:
                return new SSLProxy(server.getIP(), server.getSSlPort());
            case TLS:
                return new SSLProxy(server.getIP(), server.getTLSPort());
            default:
                return null;
        }
    }
}
