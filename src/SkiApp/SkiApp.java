package SkiApp;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SkiApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene(root, 1270, 800, Color.WHITE);
        scene.getStylesheets().add("");
        scene.getStylesheets().set(0, "/styles.css");
        primaryStage.setMinWidth(570);
        primaryStage.setMinHeight(400);
        Layout layout = new Layout(primaryStage);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/skiIcon.png") ));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Your ski application");
        root.getChildren().add(layout.mainBorderPane);
        primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWN, window -> layout.setupAfterWindowShown());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}



class Layout {

    BorderPane mainBorderPane = new BorderPane();
    private final HBox fileChooserHBox = new HBox();
    private final VBox dateColumnVBox = new VBox();
    private final VBox viewDataVBox = new VBox();
    private final ScrollPane scrollPane = new ScrollPane();
    private final MapComponent mapComponent = new MapComponent();
    private final HBox dateBox = new HBox();
    private final GridPane preciseData = new GridPane();
    private HBox centerHbox = new HBox();
    private final VBox forCharts = new VBox();
    private final StackPane chartStackPane = new StackPane();

    private final ObservableList<OneDayDataWithFrame> oneDayDataList = FXCollections.observableArrayList();

    private MyChart chartAlt;
    private ComboBox<String> chartsType;
    private int currentFrameId = -1;
    private Boolean isFullScreen = false;
    private final ColorPicker colorPicker = new ColorPicker();
    private CheckBox keepChartsCheckBox;
    private Label detailedPoint;
    private final Stage primaryStage;

    Layout(Stage primaryStage) {
        this.primaryStage = primaryStage;
        fileChooser();
        center();
        dateColumn();
        borderPane();
    }


    void setupAfterWindowShown(){
        scrollPane.setMinWidth(217);
        mapComponent.setMaxWidth(primaryStage.getScene().getWidth()-scrollPane.getMinWidth()-500);
        mapComponent.setMinWidth(primaryStage.getScene().getWidth()-scrollPane.getMinWidth()-500);
        mapComponent.setPrefWidth(primaryStage.getScene().getWidth()-scrollPane.getMinWidth()-500);

        mapComponent.setMaxHeight(primaryStage.getScene().getHeight()-fileChooserHBox.getHeight());
        mapComponent.setMinHeight(primaryStage.getScene().getHeight()-fileChooserHBox.getHeight());
        mapComponent.setPrefHeight(primaryStage.getScene().getHeight()-fileChooserHBox.getHeight());

        centerHbox.setMaxHeight(primaryStage.getScene().getHeight()-fileChooserHBox.getHeight()+5);
        centerHbox.setMinHeight(primaryStage.getScene().getHeight()-fileChooserHBox.getHeight()+5);
        centerHbox.setPrefHeight(primaryStage.getScene().getHeight()-fileChooserHBox.getHeight()+5);

        forCharts.setPrefHeight(primaryStage.getScene().getHeight() - fileChooserHBox.getHeight());


        primaryStage.maximizedProperty().addListener((observableValue, aBoolean, isFullScreen) -> {
//            System.out.println("isFullScreen: " + isFullScreen);
           this.isFullScreen = isFullScreen;
        });


        primaryStage.heightProperty().addListener((obs, sceneHeightObs, newVal) -> {
            double sceneHeight;
            if(isFullScreen) {
                sceneHeight = 1020;
            } else {
                sceneHeight = sceneHeightObs.doubleValue()-37;
            }
            scrollPane.setMaxHeight(sceneHeight-fileChooserHBox.getHeight());
            scrollPane.setMinHeight(sceneHeight-fileChooserHBox.getHeight());
            scrollPane.setPrefHeight(sceneHeight-fileChooserHBox.getHeight());
            centerHbox.setMaxHeight(sceneHeight-fileChooserHBox.getHeight());
            centerHbox.setMinHeight(sceneHeight-fileChooserHBox.getHeight());
            centerHbox.setPrefHeight(sceneHeight-fileChooserHBox.getHeight());
            mapComponent.setMaxHeight(sceneHeight-fileChooserHBox.getHeight());
            mapComponent.setMinHeight(sceneHeight-fileChooserHBox.getHeight());
            mapComponent.setPrefHeight(sceneHeight-fileChooserHBox.getHeight());
            if(dateColumnVBox.getHeight() > sceneHeight-fileChooserHBox.getHeight()){
                scrollPane.setPrefWidth(dateColumnVBox.getWidth()+15);
            } else {
                scrollPane.setPrefWidth(dateColumnVBox.getWidth()+5);
            }
        });

        primaryStage.getScene().widthProperty().addListener((obs, oldVal, newVal) -> {
            mapComponent.setMaxWidth(primaryStage.getScene().getWidth()-scrollPane.getMinWidth()-500);
            mapComponent.setMinWidth(primaryStage.getScene().getWidth()-scrollPane.getMinWidth()-500);
            mapComponent.setPrefWidth(primaryStage.getScene().getWidth()-scrollPane.getMinWidth()-500);
        });

        resizeWindowToFixLayout();
    }


    private void borderPane(){
        mainBorderPane.setTop(fileChooserHBox);
        mainBorderPane.setLeft(scrollPane);
        mainBorderPane.setCenter(centerHbox);
    }


    private void fileChooser() {
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
        scrollPane.setContent(dateColumnVBox);

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
//                e.printStackTrace();
                System.out.println("Mismatch in .input file - different amount of gps coordinates, altitudes and time");
                chosenFileLabel.setText("Incorrect input file!");
                return;
            }

            chosenFileLabel.setText(chosenFiles.toString());
        }

        insertContentToScrollPane(oneDayDataList);
        chosenFileLabel.setStyle("-fx-border-color: limegreen; -fx-background-color: white; -fx-border-width: 3");
        if(keepChartsCheckBox.isSelected()) {
            currentFrameId = 0;
            keepChartsCheckBox.setSelected(false);
        } else {
        oneDayDataList.get(0).setImClicked(true);
        colorFramesAndDisplayData(oneDayDataList);
        }
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

        // display data
        final Label date = new Label("Date:");
        final Label totalTime = new Label("Total time:");
        final Label times = new Label("Time down/up/rest:");
        final Label distance = new Label("Total distance:");
        final Label distanceDownUp = new Label("Distance down/up:");
        final Label maxSpeed = new Label("Max speed:");
        final Label avgSpeed = new Label("Average speed:");
        final Label maxAltitude = new Label("Max altitude:");
        final Label minAltitude = new Label("Min altitude:");
        final Label hills = new Label("Downhill/uphill:");
        final Label lvlDiff = new Label("Level diff down/up:");
        Label dateValue = new Label(), timeValue = new Label(), timesValue = new Label(), distanceValue = new Label(),
                distDownUpValue = new Label(), maxSpdValue = new Label(), avgSpdValue = new Label(),
                maxAltValue = new Label(), minAltValue = new Label(), hillsValue = new Label(), lvlDiffValue = new Label();

        List<Label> unitLabels = new ArrayList<>(List.of(new Label("[h]"), new Label("[h]"), new Label("[km]"),
                new Label("[km]"), new Label("[km/h]"), new Label("[km/h]"), new Label("[m]"),
                new Label("[m]"), new Label(), new Label("[km]")));

        List<Label> finalLabels = new ArrayList<>(List.of(totalTime, times, distance, distanceDownUp,
                maxSpeed, avgSpeed, maxAltitude, minAltitude, hills, lvlDiff));

        List<Label> valueLabels = new ArrayList<>(List.of(timeValue, timesValue, distanceValue, distDownUpValue,
                maxSpdValue, avgSpdValue, maxAltValue, minAltValue, hillsValue, lvlDiffValue));

        List<Label> allLabels = new ArrayList<>(finalLabels);
        allLabels.addAll(valueLabels);
        allLabels.addAll(unitLabels);

        for (Label label : allLabels) {
            if (allLabels.indexOf(label) < allLabels.size()/3) {
                label.setPrefWidth(140);
                label.setMinWidth(140);
            } else if(allLabels.indexOf(label) < allLabels.size()*2/3){
                label.setMinWidth(100);
                label.setPrefWidth(100);
            } else {
                label.setMinWidth(55);
                label.setPrefWidth(55);
            }
            label.setAlignment(Pos.CENTER);
            label.setPadding(new Insets(2, 2, 2, 2));
            label.getStyleClass().add("viewPreciseData");
        }

        // data
        for (int i = 0; i < finalLabels.size(); i++) {
            preciseData.add(finalLabels.get(i), 0, i);
            preciseData.add(valueLabels.get(i), 1, i);
            preciseData.add(unitLabels.get(i), 2, i);
        }

        // date
        date.setMinWidth(140);
        dateValue.setMinWidth(155);
        dateValue.setPrefWidth(155);
        dateBox.getChildren().addAll(date, dateValue);
        for (int i = 0; i < dateBox.getChildren().size(); i++) {
            Label label = (Label) dateBox.getChildren().get(i);
            label.setAlignment(Pos.CENTER);
            label.setPadding(new Insets(0, 5, 0, 5));
            label.getStyleClass().add("viewPreciseData");
            label.setStyle("-fx-font-size: 20");
        }

        VBox preciseDataAndDate = new VBox(dateBox, preciseData);
        preciseDataAndDate.setMaxHeight(320);
        preciseDataAndDate.setMinHeight(320);
        preciseDataAndDate.setPrefHeight(320);
        preciseDataAndDate.getStyleClass().add("mainLeftBackground");
        preciseDataAndDate.getStyleClass().add("borderLine");
        preciseDataAndDate.setStyle("-fx-border-style: hidden solid solid hidden;");
        preciseDataAndDate.setPadding(new Insets(20, 20, 20, 20));

        // color picker
        Label chartsColor = new Label("Charts color:");
        chartsColor.setMinWidth(105);
        chartsColor.setAlignment(Pos.CENTER);
        colorPicker.setPrefWidth(105);
        colorPicker.setMinHeight(Region.USE_PREF_SIZE);
        colorPicker.setValue(Color.ROYALBLUE);
        colorPicker.setOnAction(actionEvent -> {
            Circle tempCircle = (Circle) chartStackPane.getChildren().get(1);
            tempCircle.setFill(colorPicker.getValue());
            chartAlt.changeColorsOfChart(colorPicker.getValue());
            mapManagement(oneDayDataList.get(currentFrameId));
            });

        // charts type
        Label chartsTypeLabel = new Label("Charts type:");
        chartsTypeLabel.setPrefWidth(105);
        chartsTypeLabel.setAlignment(Pos.CENTER);
        chartsType = new ComboBox<>(FXCollections.observableArrayList(MyChart.DISTANCE, MyChart.TIME));
        chartsType.setPrefWidth(105);
        chartsType.setValue(MyChart.DISTANCE);
        chartsType.setOnAction(actionEvent -> {
            chartAlt.setXaxisLabel(chartsType.getValue());
            chartAlt.chart.setTitle("Chart of altitude versus " + chartsType.getValue().toLowerCase());
            detailedPoint.setText(String.format(chartsType.getValue() +  ": %.1f, Altitude: %.0f, (Lat, Lon): (%.2f, %.2f)", 0.0, 0.0, 0.0, 0.0));
            try {
                loadDataToCharts(oneDayDataList.get(currentFrameId));
            } catch (IndexOutOfBoundsException e) {
                e.getMessage();
            }
        });
        // keep charts checkbox
        keepChartsCheckBox = new CheckBox("Keep charts");
        keepChartsCheckBox.setDisable(true);
        keepChartsCheckBox.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            if(!keepChartsCheckBox.isSelected()) {
                chartAlt.chart.getData().clear();
                oneDayDataList.get(currentFrameId).setImClicked(true);
                colorFramesAndDisplayData(oneDayDataList);
                chartsType.setDisable(false);
                colorPicker.setDisable(false);
            } else {
                chartsType.setDisable(true);
                colorPicker.setDisable(true);
            }
        });

        // charts controls
        GridPane chartsControl = new GridPane();
        chartsControl.setPadding(new Insets(0, 0, 0, 30));

        Button fejkbutton = new Button();
        fejkbutton.setVisible(false);
        Button fejkbutton2 = new Button();
        fejkbutton2.setVisible(false);

        chartsControl.add(chartsColor,0, 0);
        chartsControl.add(colorPicker, 0, 1);
        chartsControl.add(fejkbutton, 0, 2);
        chartsControl.add(chartsTypeLabel,0, 3);
        chartsControl.add(chartsType, 0, 4);
        chartsControl.add(fejkbutton2, 0, 5);
        chartsControl.add(keepChartsCheckBox, 0, 6);
        chartsControl.setVgap(10);
        chartsControl.setAlignment(Pos.CENTER);

        HBox preciseAndControl = new HBox(preciseDataAndDate, chartsControl);



        // chart
        chartAlt = new MyChart(new NumberAxis(), new NumberAxis());
        chartAlt.chart.setOnMouseMoved(mouseEvent -> chartInteractiveManagement(mouseEvent.getX()));
        chartAlt.chart.setOnMouseEntered(mouseEvent -> {
                if(chartAlt.chart.getData().get(0).getData().size() > 10) {
                    chartInteractiveManagement(73);
                    chartStackPane.getChildren().get(1).setVisible(true);
        }});
        Circle circle = new Circle(0, 0, 4, colorPicker.getValue());
        circle.setVisible(false);
        chartStackPane.getChildren().addAll(chartAlt.chart, circle);

        // detailed point label
        detailedPoint = new Label(String.format(chartsType.getValue() +  ": %.1f, Altitude: %.0f, (Lat, Lon): (%.2f, %.2f)", 0.0, 0.0, 0.0, 0.0));
        detailedPoint.setPadding(new Insets(2,0,0,0));


        forCharts.getChildren().addAll(chartStackPane, detailedPoint);
        forCharts.setAlignment(Pos.TOP_CENTER);
        forCharts.setSpacing(10);
        forCharts.setPadding(new Insets(10, 0, 0, 0));
        forCharts.getStyleClass().add("borderLine");
        forCharts.setStyle("-fx-border-style: hidden hidden hidden solid;");

        forCharts.setMaxWidth(500);
        viewDataVBox.setMaxWidth(500);
        viewDataVBox.getChildren().addAll(preciseAndControl, forCharts);

        centerHbox = new HBox(viewDataVBox, mapComponent);

    }

    private void chartInteractiveManagement(double mouseXPos) {
        int currentDataSeries = 0;
        for (int i = 0; i < chartAlt.chart.getData().size(); i++) {
            if(currentFrameId != -1 && chartAlt.chart.getData().get(i).getName().equals(oneDayDataList.get(currentFrameId).getDate().toString())) {
                currentDataSeries = i;
            }
        }
        double hoveredX = chartAlt.getChartXFromMousePos(mouseXPos, currentDataSeries);
        int altFromGivenChartX, hoveredPointIndex;
        int yOffset = chartAlt.chart.getData().size() > 5 ? 88+24 : 88;
        try {
            ObservableList<XYChart.Data<Number, Number>> chartData = chartAlt.chart.getData().get(currentDataSeries).getData();
            if(chartsType.getValue().equals(MyChart.DISTANCE) && hoveredX <= chartData.get(chartData.size()-2).getXValue().doubleValue()) {
                hoveredPointIndex = oneDayDataList.get(currentFrameId).getDistanceArray().indexOf(hoveredX);
            } else if(chartsType.getValue().equals(MyChart.TIME) && hoveredX <= chartData.get(chartData.size()-2).getXValue().doubleValue()) {
                hoveredPointIndex = oneDayDataList.get(currentFrameId).getTimeArray().indexOf(hoveredX);
            } else return;
            altFromGivenChartX = (int) oneDayDataList.get(currentFrameId).getAllUsedPoints().get(hoveredPointIndex).getAlt();
            if(mouseXPos > 72 && mouseXPos < 72 + chartAlt.chart.getXAxis().getWidth()) {
                chartStackPane.getChildren().get(1).setTranslateX((mouseXPos - chartStackPane.getWidth() / 2));
                chartStackPane.getChildren().get(1).setTranslateY((chartStackPane.getHeight() / 2)-yOffset - chartAlt.convertAltToPx(altFromGivenChartX));
            }
            //change circle color
            Circle tempCircle = (Circle) chartStackPane.getChildren().get(1);
            String currentColor = chartAlt.chart.getData().get(currentDataSeries).getNode().lookup(".chart-series-area-line").getStyle();
            currentColor = currentColor.substring(currentColor.indexOf('#'), currentColor.indexOf("ff;"));
            tempCircle.setFill(Paint.valueOf(currentColor));
            // set label
            detailedPoint.setText(String.format(chartsType.getValue() +  ": %.1f, Altitude: %.0f, (Lat, Lon): (%.2f, %.2f)",
                    chartData.get(hoveredPointIndex).getXValue().doubleValue(),
                    oneDayDataList.get(currentFrameId).getAllUsedPoints().get(hoveredPointIndex).getAlt(),
                    oneDayDataList.get(currentFrameId).getAllUsedPoints().get(hoveredPointIndex).getLat(),
                    oneDayDataList.get(currentFrameId).getAllUsedPoints().get(hoveredPointIndex).getLon()));

            mapComponent.moveCircle(oneDayDataList.get(currentFrameId).getAllUsedPoints().get(hoveredPointIndex), oneDayDataList.get(currentFrameId).getDate().toString());
        } catch(IndexOutOfBoundsException ignored) {}
    }

    private void insertDataForDisplay(OneDayDataWithFrame frame) {
        List<String> values = new ArrayList<>(List.of(frame.getTotalTime().getHour() + ":" + frame.getTotalTime().getMinute(),
                frame.getTimeDown() + ", " + frame.getTimeUp() + ", " + frame.getTimeRest(), round(frame.getTotalDistance())+"",
                round(frame.getDistDown()) + ", " + round(frame.getDistUp()), Math.round(frame.getMaxSpeed())+"",
                Math.round(frame.getAvgSpeed())+"", Math.round(frame.getMaxAlt())+"", Math.round(frame.getMinAlt())+"",
                frame.getDownhill() + ", " + frame.getUphill(), round(frame.getAltDown()) + ", " + round(frame.getAltUp())));

        for (int i = 1, j = 0; i < values.size()*3; i+=3, j++) {
            Label labelToChange = (Label) (preciseData.getChildren().get(i));
            labelToChange.setText(values.get(j));
        }

        Label labelToChange = (Label) dateBox.getChildren().get(1);
        labelToChange.setText(frame.getDate().toString());

    }

    private void colorFramesAndDisplayData(ObservableList<OneDayDataWithFrame> frameListObs) {
        for (OneDayDataWithFrame frame : frameListObs) {
            if(frame.isNormalColorStyle()){
                frame.getFrameStats().getStyleClass().set(0, "frameBlue");
            } else {
                frame.getFrameStats().getStyleClass().set(0, "frameYellow");
            }

            if(wasClickedPreviously(frame) && keepChartsCheckBox.isSelected()) {
                frame.getFrameStats().getStyleClass().set(0, "frameWasClicked");
            }

            if(frame.isImClicked()) {
                frame.getFrameStats().getStyleClass().set(0, "frameClicked");
                frame.setImClicked(false);
                currentFrameId = frameListObs.indexOf(frame);
                insertDataForDisplay(frame);
                if(chartAlt.chart.getData().size() <= 7 && !wasClickedPreviously(frame)) {
                    loadDataToCharts(frame);
                    mapManagement(frame);
                }
            }
        }
    }

    private void mapManagement(OneDayDataWithFrame frame) {
        if(!keepChartsCheckBox.isSelected()) {
            mapComponent.clearAll();
        }
        String currentColor = chartAlt.chart.getData().get(chartAlt.chart.getData().size()-1).getNode().lookup(".chart-series-area-line").getStyle();
        currentColor = currentColor.substring(currentColor.indexOf('#'), currentColor.indexOf("ff;"));
        mapComponent.createTrack(frame.getAllUsedPoints(), currentColor);
        mapComponent.addMarker(frame.getAllUsedPoints().get(frame.getAltArray().indexOf(frame.getMaxAlt())), String.valueOf((int) frame.getMaxAlt()), "Max altitude", frame.getDate().toString());
        mapComponent.addCircle(frame.getAllUsedPoints().get(0), currentColor, frame.getDate().toString());
    }



    private void loadDataToCharts(OneDayDataWithFrame frame) {
        keepChartsCheckBox.setDisable(false);
        chartStackPane.getChildren().get(1).setVisible(false);
        double lastxDist = frame.getDistanceArray().get(frame.getDistanceArray().size()-1);
        double lastxTime = frame.getTimeArray().get(frame.getTimeArray().size()-1);

        if (chartsType.getValue().equals(MyChart.DISTANCE)) {
            for(int i = 0; i < chartAlt.chart.getData().size(); i++) {
                double lastx = chartAlt.chart.getData().get(i).getData().get(chartAlt.chart.getData().get(i).getData().size()-1).getXValue().doubleValue();
                if(lastx == lastxDist) return;
            }
            chartAlt.loadData(frame.getDistanceArray(), frame.getAltArray(), colorPicker.getValue(), keepChartsCheckBox.isSelected());

        } else if (chartsType.getValue().equals(MyChart.TIME)) {
            for(int i = 0; i < chartAlt.chart.getData().size(); i++) {
                double lastx = chartAlt.chart.getData().get(i).getData().get(chartAlt.chart.getData().get(i).getData().size()-1).getXValue().doubleValue();
                if(lastx == lastxTime) return;
            }
            chartAlt.loadData(frame.getTimeArray(), frame.getShortAltByTimeArray(), colorPicker.getValue(), keepChartsCheckBox.isSelected());
        }
        // set name of last added chart
        chartAlt.chart.getData().get(chartAlt.chart.getData().size()-1).setName(frame.getDate().toString());

        if(chartAlt.chart.getData().size() > 5) {
            chartAlt.chart.setMinHeight(385);
        } else {
            chartAlt.chart.setMinHeight(361);
        }
    }


    private boolean wasClickedPreviously(OneDayDataWithFrame frame) {
        for(int i = 0; i < chartAlt.chart.getData().size(); i++) {
            if(chartAlt.chart.getData().get(i).getName().equals(frame.getDate().toString())) return true;
        }
        return false;
    }


    static double round(double value) {
        value = value * 100;
        value = Math.round(value);
        return value/100;
    }


    private void resizeWindowToFixLayout() {
        PauseTransition fixWindow = new PauseTransition(Duration.millis(250));
        fixWindow.setOnFinished((ActionEvent e) -> {
            primaryStage.setWidth(primaryStage.getWidth()+1);
            primaryStage.setHeight(primaryStage.getHeight()+1);
            primaryStage.setWidth(primaryStage.getWidth()-1);
            primaryStage.setHeight(primaryStage.getHeight()-1);
            fixWindow.playFromStart();
        });
        fixWindow.play();
    }


}

