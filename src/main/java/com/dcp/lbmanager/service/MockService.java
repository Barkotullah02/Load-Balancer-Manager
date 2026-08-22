package com.dcp.lbmanager.service;

import com.dcp.lbmanager.model.BackendTarget;
import com.dcp.lbmanager.model.ListenerRule;
import com.dcp.lbmanager.model.LoadBalancer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Generates realistic mock data for demonstration. No external APIs or persistence.
 */
public class MockService {

    public ObservableList<LoadBalancer> getLoadBalancers() {
        ObservableList<LoadBalancer> list = FXCollections.observableArrayList();
        list.add(new LoadBalancer("prod-web-alb",   "Application", "us-east-1",      "Active",       18452, 942.7));
        list.add(new LoadBalancer("prod-api-alb",   "Application", "us-east-1",      "Active",       26310, 1560.3));
        list.add(new LoadBalancer("prod-tcp-nlb",   "Network",     "us-west-2",      "Active",       4821,  3120.8));
        list.add(new LoadBalancer("stage-web-alb",  "Application", "eu-west-1",      "Provisioning", 0,     0.0));
        list.add(new LoadBalancer("internal-nlb",   "Network",     "us-east-2",      "Active",       9903,  740.2));
        list.add(new LoadBalancer("legacy-clb",     "Classic",     "ap-southeast-1", "Degraded",     1207,  88.5));
        list.add(new LoadBalancer("edge-cdn-alb",   "Application", "eu-central-1",   "Active",       33127, 2205.9));
        return list;
    }

    public ObservableList<BackendTarget> getBackendTargets() {
        ObservableList<BackendTarget> list = FXCollections.observableArrayList();
        list.add(new BackendTarget("prod-web-alb", "10.0.1.21",  443, "Healthy",   100));
        list.add(new BackendTarget("prod-web-alb", "10.0.1.22",  443, "Healthy",   100));
        list.add(new BackendTarget("prod-web-alb", "10.0.1.23",  443, "Unhealthy", 100));
        list.add(new BackendTarget("prod-api-alb", "10.0.2.31",  8080, "Healthy",  120));
        list.add(new BackendTarget("prod-api-alb", "10.0.2.32",  8080, "Healthy",  80));
        list.add(new BackendTarget("prod-tcp-nlb", "10.0.3.41",  5432, "Healthy",  100));
        list.add(new BackendTarget("prod-tcp-nlb", "10.0.3.42",  5432, "Draining", 50));
        list.add(new BackendTarget("internal-nlb", "10.0.4.51",  9000, "Healthy",  100));
        list.add(new BackendTarget("legacy-clb",   "10.0.5.61",  80,   "Unhealthy",100));
        list.add(new BackendTarget("edge-cdn-alb", "10.0.6.71",  443,  "Healthy",  100));
        list.add(new BackendTarget("edge-cdn-alb", "10.0.6.72",  443,  "Healthy",  100));
        return list;
    }

    public ObservableList<ListenerRule> getListenerRules() {
        ObservableList<ListenerRule> list = FXCollections.observableArrayList();
        list.add(new ListenerRule("prod-web-alb", 1,  "HTTPS", 443, "/",           "web-tg"));
        list.add(new ListenerRule("prod-web-alb", 10, "HTTPS", 443, "/static/*",   "static-tg"));
        list.add(new ListenerRule("prod-api-alb", 1,  "HTTPS", 443, "/v1/*",       "api-v1-tg"));
        list.add(new ListenerRule("prod-api-alb", 2,  "HTTPS", 443, "/v2/*",       "api-v2-tg"));
        list.add(new ListenerRule("prod-api-alb", 50, "HTTPS", 443, "/health",     "health-tg"));
        list.add(new ListenerRule("prod-tcp-nlb", 1,  "TCP",   5432,"*",           "db-tg"));
        list.add(new ListenerRule("edge-cdn-alb", 1,  "HTTPS", 443, "/assets/*",   "cdn-tg"));
        list.add(new ListenerRule("edge-cdn-alb", 20, "HTTPS", 443, "/api/*",      "cdn-api-tg"));
        return list;
    }
}
