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
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
    private GridPane preciseData = new GridPane();

    private ObservableList<OneDayDataWithFrame> oneDayDataList = FXCollections.observableArrayList();

    private MyChart chartAlt;
    private MyChart chartSpeed;
    private ComboBox<String> chartsType;
    private int currentFrameId = -1;

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
        fileChooserHBox.getStyleClass().add("borderLine");
        fileChooserHBox.setStyle("-fx-background-color: royalblue; -fx-border-style: hidden hidden solid hidden;");

//        // label
        Label chosenFileLabel = new Label();
        chosenFileLabel.setMinHeight(25);
        chosenFileLabel.setMinWidth(300);
        chosenFileLabel.setAlignment(Pos.CENTER_LEFT);
        chosenFileLabel.setPadding(new Insets(0,5,0,5));
        chosenFileLabel.setStyle("-fx-border-color: crimson; -fx-background-color: white; -fx-border-width: 3");
//        //button
        Button chooseFileButton = new Button("Select file...");
        chooseFileButton.setOnAction(e -> fileWasChosen(primaryStage, chosenFileLabel));

        fileChooserHBox.getChildren().addAll(chooseFileButton, chosenFileLabel);
    }

    private void fileWasChosen(Stage primaryStage, Label chosenFileLabel) {
        ObservableList<File> chosenFiles = chooseFileDialog(primaryStage);
        if(chosenFiles.size() == 0) return;

        for(File xmlFIle: chosenFiles) {
            GPXparser gpXparser;
            try {
                gpXparser = new GPXparser(xmlFIle);
            } catch (IllegalArgumentException | IOException | ParserConfigurationException | SAXException e) {
//            e.printStackTrace();
                return;
            }

            try {
                ObservableList<TrackPoint> allTrackedPoints = gpXparser.parseXMLtoTrackPointList();
                ObservableList<ObservableList<TrackPoint>> dayList = SingleDayStats.divideAllPointsToDays(allTrackedPoints);
                if(chosenFiles.indexOf(xmlFIle) == 0) {
                    dateColumnVBox.getChildren().clear();
                    oneDayDataList.clear();
                    currentFrameId = -1;
                }
                for (int i = 0; i < dayList.size(); i++) {
                    if (i % 2 == 0) {
                        oneDayDataList.add(new OneDayDataWithFrame(dayList.get(i), true));
                    } else {
                        oneDayDataList.add(new OneDayDataWithFrame(dayList.get(i), false));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Errors in GPX file");
                return;
            }

            chosenFileLabel.setText(chosenFiles.toString());
//            chosenFileLabel.setText(chosenFileLabel.getText() + ", " + xmlFIle.toString());
        }

        insertContentToScrollPane(oneDayDataList);
        chosenFileLabel.setStyle("-fx-border-color: limegreen; -fx-background-color: white; -fx-border-width: 3");


        mapView.addMarker(new LatLong(50.089306, 19.751844), "Zwierzątko", 1);
        mapView.addMarker(new LatLong(50.299849, 21.343366), "Moja ukochana", 1);
    }

    private ObservableList<File> chooseFileDialog(Stage primaryStage) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open your GPX file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("GPX Files", "*.gpx"));
        List<File> fileList;
        fileList =  fileChooser.showOpenMultipleDialog(primaryStage);
        ObservableList<File> listToReturn = FXCollections.observableArrayList();
        try {
            listToReturn.addAll(fileList);
        } catch (NullPointerException ignored) {}
        return listToReturn;
    }

    private void dateColumn() {
        dateColumnVBox.getStyleClass().add("mainLeftBackground");
        dateColumnVBox.setSpacing(3);
        dateColumnVBox.setPadding(new Insets(3, 3,3,3));
        dateColumnVBox.setOnMouseClicked(mouseEvent -> colorFramesAndDisplayData(oneDayDataList));


        scrollPane.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setMaxHeight(Screen.getPrimary().getVisualBounds().getHeight());
        scrollPane.getStyleClass().add("mainLeftBackground");
    }


    private void insertContentToScrollPane(ObservableList<OneDayDataWithFrame> oneDayDataList) {
        for (OneDayDataWithFrame frame : oneDayDataList) {
            dateColumnVBox.getChildren().add(frame.getFrameStats());
        }
        scrollPane.setContent(dateColumnVBox);
    }

    private void center() {
        displayStatsPane.setPrefHeight(400);


        // display data
        preciseData.setMinWidth(300);
        preciseData.setMinHeight(300);
        preciseData.getStyleClass().add("mainLeftBackground");
        preciseData.getStyleClass().add("borderLine");
        preciseData.setStyle("-fx-border-style: hidden solid solid hidden;");

        final Label date = new Label("Date:");
        final Label time = new Label("Total time:");
        final Label distance = new Label("Total distance:");
        final Label distanceDown = new Label("Distance down:");
        final Label distanceUp = new Label("Distance up:");
        final Label maxSpeed = new Label("Max speed:");
        final Label avgSpeed = new Label("Average speed:");
        final Label maxAltitude = new Label("Max altitude:");
        final Label minAltitude = new Label("Min altitude:");
        Label dateValue = new Label(), timeValue = new Label(), distanceValue = new Label(),
                distDownValue = new Label(), distUpValue = new Label(), maxSpdValue = new Label(),
                avgSpdValue = new Label(), maxAltValue = new Label(), minAltValue = new Label();

        List<Label> unitLabels = new ArrayList<>(List.of(new Label(), new Label(), new Label("[km]"), new Label("[km]"),
                new Label("[km]"), new Label("[km/h]"), new Label("[km/h]"), new Label("[m]"), new Label("[m]")));

        List<Label> finalLabels = new ArrayList<>(List.of(date, time, distance, distanceDown, distanceUp,
                maxSpeed, avgSpeed, maxAltitude, minAltitude));

        List<Label> valueLabels = new ArrayList<>(List.of(dateValue, timeValue, distanceValue, distDownValue,
                                                        distUpValue, maxSpdValue, avgSpdValue, maxAltValue, minAltValue));

        List<Label> allLabels = new ArrayList<>(finalLabels);
        allLabels.addAll(valueLabels);
        allLabels.addAll(unitLabels);

        for (Label label : allLabels) {
            if (allLabels.indexOf(label) < allLabels.size()/3) {
                label.setPrefWidth(130);
            } else if(allLabels.indexOf(label) < allLabels.size()*2/3){
                label.setPrefWidth(100);
            } else {
                label.setPrefWidth(70);
            }
            label.setAlignment(Pos.CENTER);
            label.setPadding(new Insets(4, 5, 4, 5));
            label.setStyle("-fx-background-color: aliceblue ; -fx-font-size: 15; -fx-text-fill: black;" +
                           " -fx-font-weight: bold; -fx-border-width: 0.2; -fx-border-color: black");
        }
        preciseData.setGridLinesVisible(false);
        preciseData.setPadding(new Insets(8, 20, 8, 20));
        for (int i = 0; i < finalLabels.size(); i++) {
            preciseData.add(finalLabels.get(i), 0, i);
            preciseData.add(valueLabels.get(i), 1, i);
            preciseData.add(unitLabels.get(i), 2, i);
        }

        // charts controls
        GridPane chartsControl = new GridPane();
        chartsControl.setMinHeight(100);
        chartsControl.setPadding(new Insets(20, 30, 27 ,30));
        chartsControl.setHgap(30);
        chartsControl.setVgap(4);
        chartsControl.getStyleClass().add("borderLine");
        chartsControl.setStyle("-fx-border-style: hidden hidden hidden solid;");

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
            chartAlt.setXaxisLabel(chartsType.getValue());
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

        // charts
        chartAlt = new MyChart(new NumberAxis(), new NumberAxis());
        chartAlt.setYaxisLabel("Altitude");
        chartAlt.setXaxisLabel("Distance");
        chartSpeed = new MyChart(new NumberAxis(), new NumberAxis());
        chartSpeed.setYaxisLabel("Speed");
        chartSpeed.setXaxisLabel("Distance");
        VBox forCharts = new VBox(chartAlt.chart, chartSpeed.chart);

        displayStatsPane.getChildren().addAll(preciseAndControl, forCharts);
        viewDataVBox.getChildren().addAll(displayStatsPane, mapViewPane);
        viewDataVBox.setPadding(new Insets(0,5,5,0));

        //map
        mapViewPane.getChildren().add(mapView);
        mapViewPane.getStyleClass().add("borderLine");
        mapViewPane.setStyle("-fx-border-style: hidden hidden hidden solid;");
        mapView.displayMap(new MapConfig());


    }

    private void insertDataForDisplay(OneDayDataWithFrame frame) {
//        List<String> values = new ArrayList<>(List.of(frame.getDate().toString(), frame.getTotalTime().getHour() + "h " + frame.getTotalTime().getMinute() + "m",
//                round(frame.getTotalDistance())+" [km]", round(frame.getDistDown())+" [km]", round(frame.getDistUp())+" [km]", Math.round(frame.getMaxSpeed())+" [km/h]",
//                Math.round(frame.getAvgSpeed())+" [km/h]", Math.round(frame.getMaxAlt())+" [m]", Math.round(frame.getMinAlt())+" [m]"));
        List<String> values = new ArrayList<>(List.of(frame.getDate().toString(), frame.getTotalTime().getHour() + "h " + frame.getTotalTime().getMinute() + "m",
                round(frame.getTotalDistance())+"", round(frame.getDistDown())+"", round(frame.getDistUp())+"", Math.round(frame.getMaxSpeed())+"",
                Math.round(frame.getAvgSpeed())+"", Math.round(frame.getMaxAlt())+"", Math.round(frame.getMinAlt())+""));
        System.out.println(values.size());
        for (int i = 1, j = 0; i < (values.size()*3); i+=3, j++) {
            Label labelToChange = (Label) (preciseData.getChildren().get(i));
            labelToChange.setText(values.get(j));
        }

    }

    private double round(double value) {
        value = value * 100;
        value = Math.round(value);
        return value/100;
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
//                frame.printSingleDayStats();
                loadDataToCharts(frame);
                currentFrameId = frameListObs.indexOf(frame);
                insertDataForDisplay(frame);
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
