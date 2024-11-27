module com.bistable_spinulator.bistable_spinulator {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.desktop;
    requires java.sql;

    opens com.bistable_spinulator.bistable_spinulator to javafx.fxml;
    exports com.bistable_spinulator.bistable_spinulator;
}