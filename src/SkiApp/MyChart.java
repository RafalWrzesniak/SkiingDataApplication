package SkiApp;

import javafx.collections.ObservableList;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

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
        AreaChart.Series<Number, Number> axisData = new AreaChart.Series<>();
        for(int i = 0; i < xData.size(); i++) {
            axisData.getData().add(new XYChart.Data<>(xData.get(i), yData.get(i)));
        }

        chart.getData().add(axisData);
    }

    void setXaxisLabel(String name) {
        this.xAxis.setLabel(String.join(" ", name, name.equals("Distance") ? "[km]" : "[h]"));
    }

    void setYaxisLabel(String name) {
        this.yAxis.setLabel(String.join(" ", name, name.equals("Altitude") ? "[m]" : "[kh/h]"));
    }
}
