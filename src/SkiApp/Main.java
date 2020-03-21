package SkiApp;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene(root, 500, 400, Color.WHITE);
        scene.getStylesheets().add("/styles.css");
        Layout layout = new Layout(primaryStage);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Your ski application");
        root.getChildren().add(layout.mainBorderPane);
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
    private ScrollPane scrollPane = new ScrollPane();

    Layout(Stage primaryStage) {
        fileChooser(primaryStage);
        center();
        dateColumn(primaryStage);
        borderPane();
    }

    private void borderPane(){
        mainBorderPane.setTop(fileChooserHBox);
        mainBorderPane.setLeft(scrollPane);
        mainBorderPane.setCenter(anchorPane);
    }


    private void fileChooser(Stage primaryStage) {
//        Controls controls = new Controls(primaryStage);
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
                chosenFileLabel.setText(chooseFileDialog(primaryStage).toString());
                chosenFileLabel.setStyle("-fx-border-color: limegreen; -fx-background-color: white; -fx-border-width: 3");
            } catch (NullPointerException npe) {
                System.out.println(chosenFileLabel.getText());
            }

            scrollPane.setContent(dateColumnVBox);
            scrollPane.setMaxHeight(primaryStage.getScene().getHeight()-fileChooserHBox.getHeight());
            primaryStage.getScene().heightProperty().addListener((obs, oldVal, newVal) -> {
                scrollPane.setMaxHeight(primaryStage.getScene().getHeight()-fileChooserHBox.getHeight());
            });
        });
        fileChooserHBox.getChildren().addAll(chooseFileButton, chosenFileLabel);
    }

    File chooseFileDialog(Stage primaryStage) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("GPX Files", "*.gpx"));
//                new FileChooser.ExtensionFilter("All Files", "*.*"))
        return fileChooser.showOpenDialog(primaryStage);
    }

    private void dateColumn(Stage primaryStage) {
        GPXdataFrame singleDayFrame = new GPXdataFrame("10.03.2020", "46", "5h 12m",
                "57", "2596", true);
        GPXdataFrame singleDayFrame2 = new GPXdataFrame("11.03.2020", "62", "6h 32m",
                "63", "3126", false);
        GPXdataFrame singleDayFrame3 = new GPXdataFrame("12.03.2020", "46", "5h 12m",
                "57", "2596", true);
        GPXdataFrame singleDayFrame4 = new GPXdataFrame("13.03.2020", "62", "6h 32m",
                "63", "3126", false);
        GPXdataFrame singleDayFrame5 = new GPXdataFrame("14.03.2020", "46", "5h 12m",
                "57", "2596", true);
        dateColumnVBox = new VBox(3, singleDayFrame.getGPXdataFrame(), singleDayFrame2.getGPXdataFrame(), singleDayFrame3.getGPXdataFrame(), singleDayFrame4.getGPXdataFrame(), singleDayFrame5.getGPXdataFrame());
        dateColumnVBox.setMinWidth(100);
        dateColumnVBox.setStyle("-fx-background-color: dodgerblue; -fx-border-style: solid ;-fx-border-color: dodgerblue; -fx-border-width: 3;");


        scrollPane.setPrefSize(224, 300);
        scrollPane.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setMaxHeight(Screen.getPrimary().getVisualBounds().getHeight());
        scrollPane.setStyle("-fx-background: dodgerblue;");


    }


    private void center() {
        anchorPane.setPrefWidth(Screen.getPrimary().getVisualBounds().getWidth());
        anchorPane.setPrefHeight(Screen.getPrimary().getVisualBounds().getHeight());
        anchorPane.setStyle("-fx-background-color: white;");


    }



        }
