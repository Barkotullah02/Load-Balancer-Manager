package com.dcp.lbmanager.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Reactive data model representing a listener routing rule (path-based routing).
 */
public class ListenerRule {

    private final StringProperty loadBalancer = new SimpleStringProperty();
    private final IntegerProperty priority = new SimpleIntegerProperty();
    private final StringProperty protocol = new SimpleStringProperty();
    private final IntegerProperty listenerPort = new SimpleIntegerProperty();
    private final StringProperty pathPattern = new SimpleStringProperty();
    private final StringProperty targetGroup = new SimpleStringProperty();

    public ListenerRule(String loadBalancer, int priority, String protocol,
                        int listenerPort, String pathPattern, String targetGroup) {
        this.loadBalancer.set(loadBalancer);
        this.priority.set(priority);
        this.protocol.set(protocol);
        this.listenerPort.set(listenerPort);
        this.pathPattern.set(pathPattern);
        this.targetGroup.set(targetGroup);
    }

    public String getLoadBalancer() { return loadBalancer.get(); }
    public void setLoadBalancer(String v) { loadBalancer.set(v); }
    public StringProperty loadBalancerProperty() { return loadBalancer; }

    public int getPriority() { return priority.get(); }
    public void setPriority(int v) { priority.set(v); }
    public IntegerProperty priorityProperty() { return priority; }

    public String getProtocol() { return protocol.get(); }
    public void setProtocol(String v) { protocol.set(v); }
    public StringProperty protocolProperty() { return protocol; }

    public int getListenerPort() { return listenerPort.get(); }
    public void setListenerPort(int v) { listenerPort.set(v); }
    public IntegerProperty listenerPortProperty() { return listenerPort; }

    public String getPathPattern() { return pathPattern.get(); }
    public void setPathPattern(String v) { pathPattern.set(v); }
    public StringProperty pathPatternProperty() { return pathPattern; }

    public String getTargetGroup() { return targetGroup.get(); }
    public void setTargetGroup(String v) { targetGroup.set(v); }
    public StringProperty targetGroupProperty() { return targetGroup; }
}
