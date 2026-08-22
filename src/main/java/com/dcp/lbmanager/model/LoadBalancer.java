package com.dcp.lbmanager.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Reactive data model representing a single load balancer instance.
 * Uses JavaFX properties so the UI updates automatically on change.
 */
public class LoadBalancer {

    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty type = new SimpleStringProperty();
    private final StringProperty region = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final IntegerProperty activeConnections = new SimpleIntegerProperty();
    private final DoubleProperty throughputMbps = new SimpleDoubleProperty();

    public LoadBalancer(String name, String type, String region, String status,
                        int activeConnections, double throughputMbps) {
        this.name.set(name);
        this.type.set(type);
        this.region.set(region);
        this.status.set(status);
        this.activeConnections.set(activeConnections);
        this.throughputMbps.set(throughputMbps);
    }

    public String getName() { return name.get(); }
    public void setName(String v) { name.set(v); }
    public StringProperty nameProperty() { return name; }

    public String getType() { return type.get(); }
    public void setType(String v) { type.set(v); }
    public StringProperty typeProperty() { return type; }

    public String getRegion() { return region.get(); }
    public void setRegion(String v) { region.set(v); }
    public StringProperty regionProperty() { return region; }

    public String getStatus() { return status.get(); }
    public void setStatus(String v) { status.set(v); }
    public StringProperty statusProperty() { return status; }

    public int getActiveConnections() { return activeConnections.get(); }
    public void setActiveConnections(int v) { activeConnections.set(v); }
    public IntegerProperty activeConnectionsProperty() { return activeConnections; }

    public double getThroughputMbps() { return throughputMbps.get(); }
    public void setThroughputMbps(double v) { throughputMbps.set(v); }
    public DoubleProperty throughputMbpsProperty() { return throughputMbps; }
}
