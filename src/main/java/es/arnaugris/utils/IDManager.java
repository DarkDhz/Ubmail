package es.arnaugris.utils;

public class IDManager {

    // Singleton Instance
    private static volatile IDManager instance = null;
    private int id = 0;
    private IDManager() {

    }

    public static IDManager getInstance() {
        // To ensure only one instance is created
        if (instance == null) {
            synchronized (IDManager.class) {
                if (instance == null) {
                    instance = new IDManager();
                }
            }
        }
        return instance;
    }

    public int getNextID() {
        synchronized (IDManager.class) {
            return id++;
        }
    }
}
