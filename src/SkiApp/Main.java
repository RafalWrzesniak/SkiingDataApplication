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
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene(root, 1000, 850, Color.WHITE);
        scene.getStylesheets().add("");
        scene.getStylesheets().set(0, "/styles.css");
        Layout layout = new Layout(primaryStage);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/skiIcon.png") ));
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
    private HBox displayStatsPane = new HBox();
    private LeafletMapView mapView = new LeafletMapView();

    private ObservableList<OneDayDataWithFrame> oneDayDataList = FXCollections.observableArrayList();

    private Label chosenFileLabel = new Label();
    private Button chooseFileButton = new Button("Select file...");

    private MyChart chartAlt;
    private MyChart chartSpeed;
    private ComboBox<String> chartsType;
    private int currentFrameId;

    Layout(Stage primaryStage) {
        fileChooser(primaryStage);
        center();
        dateColumn();
        borderPane();
    }


    void setupAfterWindowShown(Stage primaryStage){
        scrollPane.setPrefWidth(217);
        mapViewPane.setMaxWidth(primaryStage.getScene().getWidth()-scrollPane.getPrefWidth());
        displayStatsPane.setMaxWidth(primaryStage.getScene().getWidth()-scrollPane.getPrefWidth());
        mapViewPane.setMaxHeight(primaryStage.getScene().getHeight()-displayStatsPane.getHeight()-fileChooserHBox.getHeight());

        primaryStage.fullScreenProperty().addListener(observable -> primaryStage.getWidth());

        primaryStage.getScene().heightProperty().addListener((obs, sceneHeightObs, newVal) -> {
            double sceneHeight = sceneHeightObs.doubleValue();
            System.out.println(sceneHeight);
            scrollPane.setMaxHeight(sceneHeight-fileChooserHBox.getHeight());
            scrollPane.setPrefHeight(sceneHeight-fileChooserHBox.getHeight());
            mapViewPane.setMaxHeight(sceneHeight-displayStatsPane.getHeight()-fileChooserHBox.getHeight());
            mapViewPane.setPrefHeight(sceneHeight-displayStatsPane.getHeight()-fileChooserHBox.getHeight());
            if(dateColumnVBox.getHeight() > sceneHeight-fileChooserHBox.getHeight()){
                scrollPane.setPrefWidth(dateColumnVBox.getWidth()+15);
            } else {
                scrollPane.setPrefWidth(dateColumnVBox.getWidth()+5);
            }
        });

        primaryStage.getScene().widthProperty().addListener((obs, oldVal, newVal) -> {
            mapViewPane.setMaxWidth(primaryStage.getScene().getWidth()-scrollPane.getWidth());
            displayStatsPane.setMaxWidth(primaryStage.getScene().getWidth()-scrollPane.getPrefWidth());
        });


        insertContentToScrollPane(oneDayDataList);
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
        fileChooserHBox.setStyle("-fx-background-color: dodgerblue;");
//        // label
//        Label chosenFileLabel = new Label();
        chosenFileLabel.setMinHeight(25);
        chosenFileLabel.setPrefWidth(300);
        chosenFileLabel.setAlignment(Pos.CENTER_LEFT);
        chosenFileLabel.setPadding(new Insets(0,5,0,5));
        chosenFileLabel.setStyle("-fx-border-color: crimson; -fx-background-color: white; -fx-border-width: 3");
//        //button
//        Button chooseFileButton = new Button("Select file...");
        chooseFileButton.setOnAction(e -> fileWasChosen(primaryStage));

        fileChooserHBox.getChildren().addAll(chooseFileButton, chosenFileLabel);
    }

    private void fileWasChosen(Stage primaryStage) {
        File chosenFile = chooseFileDialog(primaryStage);
        GPXparser gpXparser;
        try {
            gpXparser = new GPXparser(chosenFile);
        } catch (IllegalArgumentException | IOException | ParserConfigurationException | SAXException e) {
//            e.printStackTrace();
            return;
        }

        try {
            ObservableList<TrackPoint> allTrackedPoints = gpXparser.parseXMLtoTrackPointList();
            ObservableList<ObservableList<TrackPoint>> dayList = SingleDayStats.divideAllPointsToDays(allTrackedPoints);
            oneDayDataList.removeAll(oneDayDataList);
            for (int i = 0; i < dayList.size(); i++) {
                if(i%2 == 0) {
                    oneDayDataList.add(new OneDayDataWithFrame(dayList.get(i), true));
                } else {
                    oneDayDataList.add(new OneDayDataWithFrame(dayList.get(i), false));
                }
            }
            insertContentToScrollPane(oneDayDataList);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Errors in GPX file");
            return;
        }


        chosenFileLabel.setText(chosenFile.toString());
        chosenFileLabel.setStyle("-fx-border-color: limegreen; -fx-background-color: white; -fx-border-width: 3");

        mapView.addMarker(new LatLong(50.089306, 19.751844), "Zwierzątko", 1);
        mapView.addMarker(new LatLong(50.299849, 21.343366), "Moja ukochana", 1);
//            scrollPane.setContent(dateColumnVBox); // 83
    }

    private File chooseFileDialog(Stage primaryStage) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open your GPX file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("GPX Files", "*.gpx"));
        return fileChooser.showOpenDialog(primaryStage);
    }

    private void dateColumn() {
        dateColumnVBox.setStyle("-fx-background-color: dodgerblue;");
        dateColumnVBox.setSpacing(3);
        dateColumnVBox.setPadding(new Insets(3, 3,3,3));
        dateColumnVBox.setOnMouseClicked(mouseEvent -> colorFramesAndDisplayData(oneDayDataList));


        scrollPane.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setMaxHeight(Screen.getPrimary().getVisualBounds().getHeight());
        scrollPane.setStyle("-fx-background: dodgerblue;");
    }


    private void insertContentToScrollPane(ObservableList<OneDayDataWithFrame> oneDayDataList) {
        for (OneDayDataWithFrame frame : oneDayDataList) {
            dateColumnVBox.getChildren().add(frame.getFrameStats());
        }
        scrollPane.setContent(dateColumnVBox);
    }

    private void center() {
        displayStatsPane.setPrefHeight(400);

        chartAlt = new MyChart(new NumberAxis(), new NumberAxis());
        chartAlt.setYaxisLabel("Altitude");
        chartAlt.setXaxisLabel("Distance");
        chartSpeed = new MyChart(new NumberAxis(), new NumberAxis());
        chartSpeed.setYaxisLabel("Speed");
        chartSpeed.setXaxisLabel("Distance");
        chartSpeed.chart.setStyle("-fx-stroke: #e9967a;");
        VBox forCharts = new VBox(chartAlt.chart, chartSpeed.chart);
        GridPane preciseData = new GridPane();
        preciseData.setMinWidth(300);
        preciseData.setMinHeight(300);
        preciseData.setStyle("-fx-background-color: dodgerblue");

        GridPane chartsControl = new GridPane();
        chartsControl.setMinHeight(100);
        chartsControl.setPadding(new Insets(20, 30, 27 ,30));
        chartsControl.setHgap(30);
        chartsControl.setVgap(4);


        Label chartsColor = new Label("Charts color:");
        chartsColor.setPrefWidth(105);
        chartsColor.setAlignment(Pos.CENTER);
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setPrefWidth(105);
        colorPicker.setOnAction(actionEvent -> System.out.println(colorPicker.getValue()));


        Label chartsTypeLabel = new Label("Charts type:");
        chartsTypeLabel.setPrefWidth(105);
        chartsTypeLabel.setAlignment(Pos.CENTER);
        chartsType = new ComboBox<>(FXCollections.observableArrayList("Distance", "Time"));
        chartsType.setPrefWidth(105);
        chartsType.setValue("Distance");
        chartsType.setOnAction(actionEvent -> {
            chartSpeed.setXaxisLabel(chartsType.getValue());
            try {
                loadDataToCharts(oneDayDataList.get(currentFrameId));
            } catch (IndexOutOfBoundsException e) {
                e.getMessage();
            }
        });

        chartsControl.add(chartsColor,0, 0);
        chartsControl.add(chartsTypeLabel,1, 0);
        chartsControl.add(colorPicker, 0, 1);
        chartsControl.add(chartsType, 1, 1);


        VBox preciseAndControl = new VBox(preciseData, chartsControl);
        displayStatsPane.getChildren().addAll(preciseAndControl, forCharts);
        viewDataVBox.getChildren().addAll(displayStatsPane, mapViewPane);
        viewDataVBox.setPadding(new Insets(0,5,5,0));

        mapViewPane.getChildren().add(mapView);
        mapView.displayMap(new MapConfig());


    }

    private void colorFramesAndDisplayData(ObservableList<OneDayDataWithFrame> frameListObs) {
        for (OneDayDataWithFrame frame : frameListObs) {
            if(frame.isNormalColorStyle()){
                frame.getFrameStats().getStyleClass().set(0, "frameBlue");
            } else {
                frame.getFrameStats().getStyleClass().set(0, "frameYellow");
            }

            if(frame.isImClicked()) {
                frame.getFrameStats().getStyleClass().set(0, "frameClicked");
                frame.setImClicked(false);
                frame.printSingleDayStats();
                loadDataToCharts(frame);
                currentFrameId = frameListObs.indexOf(frame);
            }
        }
    }


    private void loadDataToCharts(OneDayDataWithFrame frame) {
            if (chartsType.getValue().equals("Distance")) {
                chartAlt.loadData(frame.getDistanceArray(), frame.getAltArray());
                chartSpeed.loadData(frame.getDistanceArray(), frame.getSpeedArray());
            } else if (chartsType.getValue().equals("Time")) {
                chartAlt.loadData(frame.getTimeArray(), frame.getAltArray());
                chartSpeed.loadData(frame.getTimeArray(), frame.getSpeedArray());
            }
    }




}
