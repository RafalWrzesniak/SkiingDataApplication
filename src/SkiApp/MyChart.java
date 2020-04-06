package SkiApp;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import org.w3c.dom.ls.LSOutput;

import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.HashMap;
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

        xAxis.setAutoRanging(true);
        xAxis.setMinorTickVisible(false);

        yAxis.setAutoRanging(false);
        yAxis.setMinorTickVisible(false);
        yAxis.setUpperBound(100);
        yAxis.setTickUnit(10);

        chart = new AreaChart<>(xAxis, yAxis);
        chart.legendVisibleProperty().setValue(false);
        chart.createSymbolsProperty().setValue(false);
        chart.setCreateSymbols(false);


//        XYChart.Series<Number, Number> axisData = new XYChart.Series<>();
//        XYChart.Data dataPoint = new XYChart.Data<>(10, 20);
//        axisData.getData().add(dataPoint);
//        dataPoint = new XYChart.Data<>(5, 10);
//        axisData.getData().add(dataPoint);
//        chart.getData().add(axisData);
//        axisData.getNode().lookup(".chart-series-area-fill").setStyle("-fx-fill: #0000FF25;");
//        axisData.getNode().lookup(".chart-series-area-line").setStyle("-fx-stroke: #0000FF; -fx-stroke-width: 2");


    }

    void loadData(ObservableList<Double> xData, ObservableList<Double> yData) {

        // clear previous charts
        chart.getData().clear();

        // calc max value on Y
        double yMax = 0, yMin = 10000;
        for (Double yDatum : yData) {
            if (yDatum > yMax) yMax = yDatum;
            if (yDatum < yMin) yMin = yDatum;
        }
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
            XYChart.Data dataPoint = new XYChart.Data<>(xData.get(i), yData.get(i));
            axisData.getData().add(dataPoint);
        }
        chart.getData().add(axisData);

    }

    void changeColorsOfChart(Color color) {
        try {
            chart.getData().get(0).getNode().lookup(".chart-series-area-fill").setStyle("-fx-fill: #" + color.toString().substring(2, 8) + "35;");
            chart.getData().get(0).getNode().lookup(".chart-series-area-line").setStyle("-fx-stroke-width: 1.1; -fx-stroke: #" + color.toString().substring(2, 8) + "ff;");
        } catch (NullPointerException | IndexOutOfBoundsException ignored) {}
    }
    void setXaxisLabel(String name) {
        this.xAxis.setLabel(String.join(" ", name, name.equals("Distance") ? "[km]" : "[h]"));
    }

    void setYaxisLabel(String name) {
        this.yAxis.setLabel(String.join(" ", name, name.equals("Altitude") ? "[m]" : "[kh/h]"));
    }


}
