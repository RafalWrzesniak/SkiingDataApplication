package SkiApp;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;

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

        System.out.println(randomColorRgbFormat());
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
            AreaChart.Data dataPoint = new AreaChart.Data<>(xData.get(i), yData.get(i));
//            Node lineSymbol = dataPoint.getNode().lookup(".default-color0").lookup(".chart-series-area-line");
//            lineSymbol.setStyle("-fx-stroke: white;");
            axisData.getData().add(dataPoint);
        }

        chart.getData().add(axisData);
    }

    void setXaxisLabel(String name) {
        this.xAxis.setLabel(String.join(" ", name, name.equals("Distance") ? "[km]" : "[h]"));
    }

    void setYaxisLabel(String name) {
        this.yAxis.setLabel(String.join(" ", name, name.equals("Altitude") ? "[m]" : "[kh/h]"));
    }

    public void changeColorsOfChart(AreaChart<Number, Number> chart)
    {
//        chart.getData().get(0).getNode().setStyle("-fx-border-style: solid; -fx-stroke: #0000FF; -fx-background-color: #0000FF;");
//        AreaChart.Series<Number, Number> series = chart.getData().get(0);
//        String rgb = randomColorRgbFormat();
//        series.getData().get(0).getNode().setStyle("-fx-stroke: rgb(" + rgb + ");");

//        XYChart.Series<Number,Number> value  //is our serie value.
        AreaChart.Series<Number, Number> value = chart.getData().get(0);
        System.out.println(chart.getData().get(0).getData().size());

        for(int i = 0; i<value.getData().size(); i++){
            // we're looping for each data point, changing the color of line symbol
            AreaChart.Data dataPoint = value.getData().get(i);
            Node lineSymbol = dataPoint.getNode().lookup(".default-color0.chart-series-area-fill");
            lineSymbol.setStyle("-fx-stroke: blue;");
        }
// and this is for the color of the line
        value.getNode().setStyle("-fx-border-style: solid; -fx-stroke: #0000FF; -fx-background-color: #0000FF;");


//        for (AreaChart.Data<Number, Number> data : series.getData())
//        {
//            data.getNode().setStyle("-fx-stroke: rgb(" + rgb + ");");
//        }
//        colors.put(series.getName(), rgb);

//        for (Node n : chart.getChildrenUnmodifiable())
//        {
//            if (n instanceof Legend)
//            {
//                for (LegendItem items : ((Legend)n).getItems())
//                {
//                    String rgb = colors.get(items.getText());
//                    items.getSymbol().setStyle("-fx-bar-fill: rgb(" + rgb + ");");
//                }
//            }
//        }
    }


    public String randomColorRgbFormat()
    {
        Color color = new Color(Math.random(), Math.random(), Math.random(), 0);
        return String.format("%d, %d, %d, 1.0", (int) (color.getRed() * 255), (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}
