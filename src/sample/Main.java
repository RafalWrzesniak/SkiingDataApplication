package sample;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene(root, 500, 300, Color.WHITE);
        scene.getStylesheets().add("/styles.css");
        Layout layout = new Layout(primaryStage);
        root.getChildren().add(layout.mainBorderPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Your ski application");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

class Layout {

    BorderPane mainBorderPane = new BorderPane();
    private HBox fileChooserHBox = new HBox();
    private VBox dateColumnVBox = new VBox();
    private AnchorPane anchorPane = new AnchorPane();

    Layout(Stage primaryStage) {
        fileChooser(primaryStage);
        dateColumn();
        center();
        borderPane();
    }

    private void borderPane(){
//        mainBorderPane.setStyle("-fx-border-color: black");
        mainBorderPane.setTop(fileChooserHBox);
        mainBorderPane.setLeft(dateColumnVBox);
        mainBorderPane.setCenter(anchorPane);
    }


    private void fileChooser(Stage primaryStage) {
        Controls controls = new Controls(primaryStage);
        fileChooserHBox.setPrefHeight(20);
        fileChooserHBox.setPrefWidth(Screen.getPrimary().getVisualBounds().getWidth());
        fileChooserHBox.setSpacing(20);
        fileChooserHBox.setPadding(new Insets(10, 0, 10, 10));
        fileChooserHBox.setStyle("-fx-background-color: dodgerblue ");
        // label
        Label chosenFileLabel = new Label();
        chosenFileLabel.setMinHeight(25);
        chosenFileLabel.setMinWidth(300);
        chosenFileLabel.setAlignment(Pos.CENTER_LEFT);
        chosenFileLabel.setPadding(new Insets(0,5,0,5));
        chosenFileLabel.setStyle("-fx-border-color: crimson; -fx-background-color: white; -fx-border-width: 3");
        //button
        Button chooseFileButton = new Button("Select file...");
        chooseFileButton.setOnAction(e -> {
            try {
                chosenFileLabel.setText(controls.chooseFileDialog().toString());
                chosenFileLabel.setStyle("-fx-border-color: limegreen; -fx-background-color: white; -fx-border-width: 3");
            } catch (NullPointerException npe) {
                System.out.println(chosenFileLabel.getText());
            }
        });
        fileChooserHBox.getChildren().addAll(chooseFileButton, chosenFileLabel);
    }

    private void dateColumn() {
        Button label1 = new Button("Button 1");
        Button button2 = new Button("Button 2");

        GridPane singleDayStats = new GridPane();

        Label date = new Label("Date:");
        Label dateValue = new Label("10.03"); // to delete

        Label distance = new Label("Route:");
        Label distanceValue = new Label("52 km"); // to delete
        Label time = new Label("Time:");
        Label timeValue = new Label("6h 45m"); // to delete
        Label speed = new Label("Speed:");
        Label speedValue = new Label("61 km/h"); // to delete
        Label altitude = new Label("Height:");
        Label altitudeValue = new Label("2546 m"); // to delete

        Label[] labelki = {date, dateValue, distance, distanceValue, time, timeValue, speed, speedValue, altitude, altitudeValue};
        for (Label labelka: labelki) {
            labelka.setPrefWidth(50);
            labelka.setAlignment(Pos.CENTER);
            labelka.setPadding(new Insets(3,3,3,3));
        }

        singleDayStats.gridLinesVisibleProperty().setValue(true);
//        singleDayStats.add(date, 1, 0);
//        singleDayStats.add(dateValue, 2, 0);
        singleDayStats.addRow(1, distance, distanceValue, altitude, altitudeValue);
        singleDayStats.addRow(2, time, timeValue, speed, speedValue);
        for (Node child: singleDayStats.getChildren()) {
            child.getStyleClass().add("label1");
        }

        HBox dateOnly = new HBox(date, dateValue);
        dateOnly.setPadding(new Insets(0,0,0,50));

        VBox wholeFrameStats = new VBox(dateOnly, singleDayStats);
        wholeFrameStats.setStyle("-fx-border-color: blue; -fx-border-width: 2");



        dateColumnVBox = new VBox(20, label1, button2, wholeFrameStats);
        dateColumnVBox.setMinWidth(100);
        dateColumnVBox.setStyle("-fx-border-color: pink; -fx-border-width: 3");
    }


    private void center() {
        anchorPane.setPrefHeight(200);
        anchorPane.setPrefWidth(200);
//        anchorPane.setMaxWidth(300);
        anchorPane.setStyle("-fx-border-color: blueviolet  ; -fx-border-width: 3");

    }



        }
