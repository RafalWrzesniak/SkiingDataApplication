package SkiApp;

import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

class Controls {

    private Stage primaryStage;

    Controls(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    File chooseFileDialog() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("GPX Files", "*.gpx"));
//                new FileChooser.ExtensionFilter("All Files", "*.*"))
        return fileChooser.showOpenDialog(primaryStage);
    }


}
