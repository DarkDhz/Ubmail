package es.arnaugris.external;

import es.arnaugris.utils.BlackListUtils;

import java.util.ArrayList;

public class DomainList {

    // Singleton Instance
    private static volatile DomainList instance = null;

    private ArrayList<String> domains;


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

    public void loadDomain() {
        this.domains = new ArrayList<>();
        domains.add("www.ub.edu");
        domains.add("ub.edu");
        domains.add("campusvirtual.ub.edu");
    }
}
