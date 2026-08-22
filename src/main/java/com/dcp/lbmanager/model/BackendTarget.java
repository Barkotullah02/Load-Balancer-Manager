package com.dcp.lbmanager.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Reactive data model representing a backend target registered behind a load balancer.
 */
public class BackendTarget {

    private final StringProperty loadBalancer = new SimpleStringProperty();
    private final StringProperty ipAddress = new SimpleStringProperty();
    private final IntegerProperty port = new SimpleIntegerProperty();
    private final StringProperty health = new SimpleStringProperty();
    private final IntegerProperty weight = new SimpleIntegerProperty();

    public BackendTarget(String loadBalancer, String ipAddress, int port, String health, int weight) {
        this.loadBalancer.set(loadBalancer);
        this.ipAddress.set(ipAddress);
        this.port.set(port);
        this.health.set(health);
        this.weight.set(weight);
    }

    public String getLoadBalancer() { return loadBalancer.get(); }
    public void setLoadBalancer(String v) { loadBalancer.set(v); }
    public StringProperty loadBalancerProperty() { return loadBalancer; }

    public String getIpAddress() { return ipAddress.get(); }
    public void setIpAddress(String v) { ipAddress.set(v); }
    public StringProperty ipAddressProperty() { return ipAddress; }

    public int getPort() { return port.get(); }
    public void setPort(int v) { port.set(v); }
    public IntegerProperty portProperty() { return port; }

    public String getHealth() { return health.get(); }
    public void setHealth(String v) { health.set(v); }
    public StringProperty healthProperty() { return health; }

    public int getWeight() { return weight.get(); }
    public void setWeight(int v) { weight.set(v); }
    public IntegerProperty weightProperty() { return weight; }
}
