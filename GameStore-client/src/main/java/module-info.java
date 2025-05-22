module com.example.gamestoreclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires com.google.gson;
    requires java.sql;

    opens com.example.gamestoreclient to javafx.fxml;
    exports com.example.gamestoreclient;

    opens com.example.gamestoreclient.controllers to javafx.fxml;
    exports com.example.gamestoreclient.controllers;

    opens com.example.gamestoreclient.models to javafx.fxml;
    exports com.example.gamestoreclient.models;

    opens com.example.gamestoreclient.services to javafx.fxml;
    exports com.example.gamestoreclient.services;

    opens com.example.gamestoreclient.utils to javafx.fxml;
    exports com.example.gamestoreclient.utils;


}