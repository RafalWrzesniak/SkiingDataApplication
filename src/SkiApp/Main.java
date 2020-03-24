package SkiApp;

import de.saring.leafletmap.LatLong;
import de.saring.leafletmap.MapConfig;
import de.saring.leafletmap.Marker;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene(root, 1000, 700, Color.WHITE);
        scene.getStylesheets().add("/styles.css");
        Layout layout = new Layout(primaryStage);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Your ski application");
        root.getChildren().add(layout.mainBorderPane);
        primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWN, window -> {
            layout.setupAfterWindowShown(primaryStage);
        });
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
    private VBox viewDataVBox = new VBox();
    private ScrollPane scrollPane = new ScrollPane();
    private StackPane spMapView = new StackPane();
    de.saring.leafletmap.LeafletMapView mapView = new de.saring.leafletmap.LeafletMapView();


    Layout(Stage primaryStage) {
        fileChooser(primaryStage);
        center();
        dateColumn();
        borderPane();
    }

    void setupAfterWindowShown(Stage primaryStage){
        spMapView.setMaxWidth(primaryStage.getScene().getWidth()-224-5);
        spMapView.setMaxHeight(primaryStage.getScene().getHeight()-200);

        primaryStage.getScene().heightProperty().addListener((obs, oldVal, newVal) -> {
            scrollPane.setMaxHeight(primaryStage.getScene().getHeight()-fileChooserHBox.getHeight());
            spMapView.setMaxHeight(primaryStage.getScene().getHeight()-200);
        });
        primaryStage.getScene().widthProperty().addListener((obs, oldVal, newVal) -> spMapView.setMaxWidth(primaryStage.getScene().getWidth()-224-5));
    }


    private void borderPane(){
        mainBorderPane.setTop(fileChooserHBox);
        mainBorderPane.setLeft(scrollPane);
        mainBorderPane.setCenter(viewDataVBox);
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
//        chosenFileLabel.foc
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


            mapView.addMarker(new LatLong(50.089306, 19.751844), "Zwierzątko", 1);
            mapView.addMarker(new LatLong(50.299849, 21.343366), "Moja ukochana", 1);
        });
        fileChooserHBox.getChildren().addAll(chooseFileButton, chosenFileLabel);
    }

    private File chooseFileDialog(Stage primaryStage) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("GPX Files", "*.gpx"));
//                new FileChooser.ExtensionFilter("All Files", "*.*"))
        return fileChooser.showOpenDialog(primaryStage);
    }

    private void dateColumn() {
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

        singleDayFrame.getGPXdataFrame(), singleDayFrame2.getGPXdataFrame(), singleDayFrame3.getGPXdataFrame(), singleDayFrame4.getGPXdataFrame(), singleDayFrame5.getGPXdataFrame());
        ObservableList<VBox> frameList =  FXCollections.<VBox>observableArrayList(singleDayFrame.getGPXdataFrame(), singleDayFrame2.getGPXdataFrame(), singleDayFrame3.getGPXdataFrame(), singleDayFrame4.getGPXdataFrame(), singleDayFrame5.getGPXdataFrame()););

//        tbView.setItems(list);
        ListView frameListPane = new ListView(frameList);

        dateColumnVBox = new VBox(3, (Node) frameList);
//        dateColumnVBox.setMinWidth(100);

        dateColumnVBox.setStyle("-fx-background-color: dodgerblue; -fx-border-style: solid ;-fx-border-color: dodgerblue; -fx-border-width: 3;");


        scrollPane.setPrefSize(224, 300);
        scrollPane.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setMaxHeight(Screen.getPrimary().getVisualBounds().getHeight());
        scrollPane.setStyle("-fx-background: dodgerblue;");


    }


    private void center() {

        AnchorPane charts = new AnchorPane();
        charts.setPrefHeight(150);
        viewDataVBox.getChildren().addAll(charts, spMapView);

        spMapView.setStyle("-fx-background: white;");


        spMapView.getChildren().add(mapView);
        mapView.displayMap(new MapConfig());


    }



        }
