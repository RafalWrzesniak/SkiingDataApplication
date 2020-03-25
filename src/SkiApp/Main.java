package SkiApp;

import de.saring.leafletmap.LatLong;
import de.saring.leafletmap.LeafletMapView;
import de.saring.leafletmap.MapConfig;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;


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
        primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWN, window -> layout.setupAfterWindowShown(primaryStage));
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
    private StackPane mapViewPane = new StackPane();
    private LeafletMapView mapView = new LeafletMapView();
    private AnchorPane charts = new AnchorPane();
    private ObservableList<GPXdataFrame> frameListObs;

    Layout(Stage primaryStage) {
        fileChooser(primaryStage);
        center();
        dateColumn();
        borderPane();
    }


    void setupAfterWindowShown(Stage primaryStage){
        scrollPane.setPrefWidth(217);
        mapViewPane.setMaxWidth(primaryStage.getScene().getWidth()-scrollPane.getPrefWidth());
        mapViewPane.setMaxHeight(primaryStage.getScene().getHeight()-charts.getHeight()-fileChooserHBox.getHeight());

        primaryStage.getScene().heightProperty().addListener((obs, sceneHeightObs, newVal) -> {
            double sceneHeight = sceneHeightObs.doubleValue();
            scrollPane.setMaxHeight(sceneHeight-fileChooserHBox.getHeight());
            scrollPane.setPrefHeight(sceneHeight-fileChooserHBox.getHeight());
            mapViewPane.setMaxHeight(sceneHeight-charts.getHeight()-fileChooserHBox.getHeight());
            mapViewPane.setPrefHeight(sceneHeight-charts.getHeight()-fileChooserHBox.getHeight());
            if(dateColumnVBox.getHeight() > sceneHeight-fileChooserHBox.getHeight()){
                scrollPane.setPrefWidth(dateColumnVBox.getWidth()+15);
            } else {
                scrollPane.setPrefWidth(dateColumnVBox.getWidth()+5);
            }
        });
        primaryStage.getScene().widthProperty().addListener((obs, oldVal, newVal) -> mapViewPane.setMaxWidth(primaryStage.getScene().getWidth()-scrollPane.getWidth()));

        scrollPane.setContent(dateColumnVBox);
    }


    private void borderPane(){
        mainBorderPane.setTop(fileChooserHBox);
        mainBorderPane.setLeft(scrollPane);
        mainBorderPane.setCenter(viewDataVBox);
    }


    private void fileChooser(Stage primaryStage) {
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
                System.out.println("error while loading file");
            }
            mapView.addMarker(new LatLong(50.089306, 19.751844), "Zwierzątko", 1);
            mapView.addMarker(new LatLong(50.299849, 21.343366), "Moja ukochana", 1);
//            scrollPane.setContent(dateColumnVBox); // 83
        });
        fileChooserHBox.getChildren().addAll(chooseFileButton, chosenFileLabel);
    }

    private File chooseFileDialog(Stage primaryStage) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open your GPX file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("GPX Files", "*.gpx"));
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

        frameListObs = FXCollections.observableArrayList(singleDayFrame, singleDayFrame2,
                                                                    singleDayFrame3, singleDayFrame4, singleDayFrame5);
        for (GPXdataFrame frame : frameListObs) {
            dateColumnVBox.getChildren().add(frame.getFrameStats());

        }

        dateColumnVBox.setStyle("-fx-background-color: dodgerblue;");
        dateColumnVBox.setSpacing(3);
        dateColumnVBox.setPadding(new Insets(3, 3,3,3));
        dateColumnVBox.setOnMouseClicked(mouseEvent -> colorFrames(frameListObs));


        scrollPane.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setMaxHeight(Screen.getPrimary().getVisualBounds().getHeight());
        scrollPane.setStyle("-fx-background: dodgerblue;");
    }

    private void colorFrames(ObservableList<GPXdataFrame> frameListObs) {
        for (GPXdataFrame frame : frameListObs) {
            if(frame.getNormalColorStyle()){
                frame.getFrameStats().getStyleClass().set(0, "frameBlue");
            } else {
                frame.getFrameStats().getStyleClass().set(0, "frameYellow");
            }

            if(frame.getImClicked()) {
                frame.getFrameStats().getStyleClass().set(0, "frameClicked");
                frame.setImClicked(false);
            }
        }

    }

    private void center() {

        charts.setPrefHeight(150);
        viewDataVBox.getChildren().addAll(charts, mapViewPane);
        viewDataVBox.setPadding(new Insets(0,5,5,0));


        mapViewPane.getChildren().add(mapView);
        mapView.displayMap(new MapConfig());


    }



        }
