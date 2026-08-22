module com.dcp.lbmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires com.fasterxml.jackson.databind;
    requires org.controlsfx.controls;

    opens com.dcp.lbmanager to javafx.fxml;
    opens com.dcp.lbmanager.controller to javafx.fxml;
    opens com.dcp.lbmanager.model to javafx.base;

    exports com.dcp.lbmanager;
}
