package com.dcp.lbmanager.controller;

import com.dcp.lbmanager.model.BackendTarget;
import com.dcp.lbmanager.model.ListenerRule;
import com.dcp.lbmanager.model.LoadBalancer;
import com.dcp.lbmanager.service.MockService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Binds table columns to model properties and computes summary statistics
 * from the mock service on initialization.
 */
public class DashboardController {

    // --- Load balancer table ---
    @FXML private TableView<LoadBalancer> lbTable;
    @FXML private TableColumn<LoadBalancer, String> lbNameCol;
    @FXML private TableColumn<LoadBalancer, String> lbTypeCol;
    @FXML private TableColumn<LoadBalancer, String> lbRegionCol;
    @FXML private TableColumn<LoadBalancer, String> lbStatusCol;
    @FXML private TableColumn<LoadBalancer, Number> lbConnCol;
    @FXML private TableColumn<LoadBalancer, Number> lbThroughputCol;

    // --- Backend target table ---
    @FXML private TableView<BackendTarget> targetTable;
    @FXML private TableColumn<BackendTarget, String> tgtLbCol;
    @FXML private TableColumn<BackendTarget, String> tgtIpCol;
    @FXML private TableColumn<BackendTarget, Number> tgtPortCol;
    @FXML private TableColumn<BackendTarget, String> tgtHealthCol;
    @FXML private TableColumn<BackendTarget, Number> tgtWeightCol;

    // --- Listener rule table ---
    @FXML private TableView<ListenerRule> ruleTable;
    @FXML private TableColumn<ListenerRule, String> ruleLbCol;
    @FXML private TableColumn<ListenerRule, Number> rulePriorityCol;
    @FXML private TableColumn<ListenerRule, String> ruleProtocolCol;
    @FXML private TableColumn<ListenerRule, Number> rulePortCol;
    @FXML private TableColumn<ListenerRule, String> rulePathCol;
    @FXML private TableColumn<ListenerRule, String> ruleTargetCol;

    // --- Summary cards ---
    @FXML private Label lbCountLabel;
    @FXML private Label activeLbLabel;
    @FXML private Label targetCountLabel;
    @FXML private Label healthyTargetLabel;
    @FXML private Label totalConnLabel;
    @FXML private Label totalThroughputLabel;

    private final MockService service = new MockService();

    @FXML
    public void initialize() {
        ObservableList<LoadBalancer> loadBalancers = service.getLoadBalancers();
        ObservableList<BackendTarget> targets = service.getBackendTargets();
        ObservableList<ListenerRule> rules = service.getListenerRules();

        wireLoadBalancerTable(loadBalancers);
        wireTargetTable(targets);
        wireRuleTable(rules);
        computeSummary(loadBalancers, targets);
    }

    private void wireLoadBalancerTable(ObservableList<LoadBalancer> data) {
        lbNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        lbTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        lbRegionCol.setCellValueFactory(new PropertyValueFactory<>("region"));
        lbStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        lbConnCol.setCellValueFactory(c -> c.getValue().activeConnectionsProperty());
        lbThroughputCol.setCellValueFactory(c -> c.getValue().throughputMbpsProperty());

        applyStatusColoring(lbStatusCol);
        lbTable.setItems(data);
    }

    private void wireTargetTable(ObservableList<BackendTarget> data) {
        tgtLbCol.setCellValueFactory(new PropertyValueFactory<>("loadBalancer"));
        tgtIpCol.setCellValueFactory(new PropertyValueFactory<>("ipAddress"));
        tgtPortCol.setCellValueFactory(c -> c.getValue().portProperty());
        tgtHealthCol.setCellValueFactory(new PropertyValueFactory<>("health"));
        tgtWeightCol.setCellValueFactory(c -> c.getValue().weightProperty());

        applyStatusColoring(tgtHealthCol);
        targetTable.setItems(data);
    }

    private void wireRuleTable(ObservableList<ListenerRule> data) {
        ruleLbCol.setCellValueFactory(new PropertyValueFactory<>("loadBalancer"));
        rulePriorityCol.setCellValueFactory(c -> c.getValue().priorityProperty());
        ruleProtocolCol.setCellValueFactory(new PropertyValueFactory<>("protocol"));
        rulePortCol.setCellValueFactory(c -> c.getValue().listenerPortProperty());
        rulePathCol.setCellValueFactory(new PropertyValueFactory<>("pathPattern"));
        ruleTargetCol.setCellValueFactory(new PropertyValueFactory<>("targetGroup"));

        ruleTable.setItems(data);
    }

    /** Colors status/health text cells green/yellow/red by value. */
    private <T> void applyStatusColoring(TableColumn<T, String> col) {
        col.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String value, boolean empty) {
                super.updateItem(value, empty);
                getStyleClass().removeAll("status-good", "status-warn", "status-bad");
                if (empty || value == null) {
                    setText(null);
                    return;
                }
                setText(value);
                switch (value) {
                    case "Active", "Healthy" -> getStyleClass().add("status-good");
                    case "Provisioning", "Draining" -> getStyleClass().add("status-warn");
                    case "Degraded", "Unhealthy" -> getStyleClass().add("status-bad");
                    default -> { }
                }
            }
        });
    }

    private void computeSummary(ObservableList<LoadBalancer> lbs, ObservableList<BackendTarget> targets) {
        long activeLbs = lbs.stream().filter(l -> "Active".equals(l.getStatus())).count();
        long healthyTargets = targets.stream().filter(t -> "Healthy".equals(t.getHealth())).count();
        long totalConn = lbs.stream().mapToInt(LoadBalancer::getActiveConnections).sum();
        double totalThroughput = lbs.stream().mapToDouble(LoadBalancer::getThroughputMbps).sum();

        lbCountLabel.setText(String.valueOf(lbs.size()));
        activeLbLabel.setText(activeLbs + " active");
        targetCountLabel.setText(String.valueOf(targets.size()));
        healthyTargetLabel.setText(healthyTargets + " healthy");
        totalConnLabel.setText(String.format("%,d", totalConn));
        totalThroughputLabel.setText(String.format("%.1f Mbps", totalThroughput));
    }
}
