package SkiApp;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Set;

class MyChart extends AreaChart{

    private NumberAxis xAxis;
    private NumberAxis yAxis;
    AreaChart<Number, Number> chart;

    MyChart(NumberAxis xAxis, NumberAxis yAxis) {
        super(xAxis, yAxis);
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        commonSetups();
    }

    private void commonSetups() {

        xAxis.setAutoRanging(false);
        xAxis.setMinorTickVisible(false);
        xAxis.setUpperBound(100);
        xAxis.setTickUnit(10);

        yAxis.setAutoRanging(false);
        yAxis.setMinorTickVisible(false);
        yAxis.setUpperBound(3000);
        yAxis.setTickUnit(500);

        chart = new AreaChart<>(xAxis, yAxis);
        chart.legendVisibleProperty().setValue(true);
        chart.createSymbolsProperty().setValue(false);
        chart.setCreateSymbols(false);

    }

    void loadData(ObservableList<Double> xData, ObservableList<Double> yData, Color color) {

        // clear previous charts
        chart.getData().clear();

        // calc max and min value on Y
        double yMax = calcMaxY(yData);
        double yMin = calcMinY(yData);

        int scale = 500;
        if(yMax<200) {
            scale = 10;
            yAxis.setTickUnit(10);
        } else {
            yAxis.setTickUnit(500);
        }
        //set bounds depending on max Y value
        for (int i = 0, j = scale*20; i < scale*20 && j > 0; i+=scale, j-= scale) {
            if(yMin > i) yAxis.setLowerBound(i);
            if(yMax < j) yAxis.setUpperBound(j);
        }

        // add to chart
        XYChart.Series<Number, Number> axisData = new XYChart.Series<>();
        for(int i = 0; i < xData.size(); i++) {
//        for(int i = 0; i < 500; i++) {
            axisData.getData().add(new XYChart.Data<>(xData.get(i), yData.get(i)));
        }

        // set x range
        xAxis.setAutoRanging(false);
        int lastX = axisData.getData().get(axisData.getData().size()-1).getXValue().intValue();
        if(lastX > 20) {
            xAxis.setTickUnit(5);
            lastX += 5;
            for (int i = 0; i < 5; i++) {
                if (lastX % 5 == 0) {
                    xAxis.setUpperBound(lastX);
                    break;
                } else {
                    lastX -= 1;
                }
            }
        } else {
            xAxis.setTickUnit(1);
            xAxis.setUpperBound(lastX+1);
        }

        // animate data
        animateChart(axisData, yMin, color);

    }

    double calcMaxY(ObservableList<Double> yData) {
        double yMax = 0;
        for (Double yDatum : yData) {
            if (yDatum > yMax) yMax = yDatum;
        }
        return yMax;
    }

    double calcMinY(ObservableList<Double> yData) {
        double yMin = 10000;
        for (Double yDatum : yData) {
            if (yDatum < yMin) yMin = yDatum;
        }
        return yMin;
    }


    void changeColorsOfChart(Color color) {
        try {
            chart.getData().get(0).getNode().lookup(".chart-series-area-fill").setStyle("-fx-fill: #" + color.toString().substring(2, 8) + "35;");
            chart.getData().get(0).getNode().lookup(".chart-series-area-line").setStyle("-fx-stroke-width: 1.1; -fx-stroke: #" + color.toString().substring(2, 8) + "ff;");
            Label tempLabel = (Label) chart.lookup("Label.chart-legend-item");
            tempLabel.getGraphic().setStyle("-fx-background-color: #" + color.toString().substring(2, 8) + "ff;");
        } catch (NullPointerException | IndexOutOfBoundsException ignored) {}
    }


    void setXaxisLabel(String name) {
        this.xAxis.setLabel(String.join(" ", name, name.equals("Distance") ? "[km]" : "[h]"));
    }

    void setYaxisLabel(String name) {
        this.yAxis.setLabel(String.join(" ", name, name.equals("Altitude") ? "[m]" : "[kh/h]"));
    }

    void animateChart(XYChart.Series<Number, Number> axisData, double yMin, Color color) {

        // prepare start data
        double size = 15;
        XYChart.Series<Number, Number> newAxisData = new XYChart.Series<>();
        for(int i = 0; i < axisData.getData().size(); i++) {
            newAxisData.getData().add(new XYChart.Data<>(axisData.getData().get(i).getXValue().doubleValue() / size, yMin + Math.random() * (yMin + 20) / 5));
        }
        chart.getData().add(newAxisData);
        changeColorsOfChart(color);
        XYChart.Series<Number, Number> newAxisDataCopy = new XYChart.Series<>();
        newAxisDataCopy.getData().addAll(newAxisData.getData());

        // move charts right
        Timeline moveXdataIntoChart = new Timeline();
        moveXdataIntoChart.getKeyFrames().add(new KeyFrame(Duration.millis(20), (ActionEvent actionEvent) -> {
            for(int i = 0; i < axisData.getData().size(); i++) {
                Number curx = newAxisData.getData().get(i).getXValue();
                if(curx.doubleValue() < axisData.getData().get(i).getXValue().doubleValue()) {
                    newAxisData.getData().get(i).setXValue(curx.doubleValue() + (axisData.getData().get(i).getXValue().doubleValue() / size));
                }
            }
            chart.getData().clear();
            chart.getData().add(newAxisData);
            changeColorsOfChart(color);
        }));

        // move charts up
        Timeline moveYdataIntoChart = new Timeline();
        moveYdataIntoChart.getKeyFrames().add(new KeyFrame(Duration.millis(30), (ActionEvent actionEvent) -> {
            for(int i = 0; i < axisData.getData().size(); i++) {
                Number cury = newAxisData.getData().get(i).getYValue();
                Number tary = axisData.getData().get(i).getYValue();
                if(cury.doubleValue() < tary.doubleValue()) {
                    newAxisData.getData().get(i).setYValue(cury.doubleValue() + (axisData.getData().get(i).getYValue().doubleValue() / (size)));
                } else if(cury.doubleValue() != axisData.getData().get(i).getYValue().doubleValue()){
                    newAxisData.getData().get(i).setYValue(axisData.getData().get(i).getYValue().doubleValue());

                }
            }
            chart.getData().clear();
            chart.getData().add(newAxisData);
            changeColorsOfChart(color);
        }));

        moveYdataIntoChart.setCycleCount((int) size);
        moveYdataIntoChart.setOnFinished(actionEvent -> {
            for(int i = 0; i < axisData.getData().size(); i++) {
                newAxisData.getData().get(i).setXValue(axisData.getData().get(i).getXValue());
                newAxisData.getData().get(i).setYValue(axisData.getData().get(i).getYValue());
            }
            chart.getData().clear();
            chart.getData().add(newAxisData);
            changeColorsOfChart(color);
        });

        moveXdataIntoChart.setCycleCount((int) size);
        moveXdataIntoChart.setOnFinished((actionEvent -> moveYdataIntoChart.play()));
        moveXdataIntoChart.play();
    }



}
