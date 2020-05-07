package SkiApp;

import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class MapComponent extends StackPane {

    private static final Logger logger = LoggerFactory.getLogger(MapComponent.class.getName());

    private final WebView webView = new WebView();
    private final WebEngine webEngine = webView.getEngine();
    private int zoomLevel;

    MapComponent() {
        webEngine.load(getClass().getResource("/leafletmap/map.html").toExternalForm());
        webEngine.setJavaScriptEnabled(true);
        getChildren().add(webView);
        webEngine.getLoadWorker().stateProperty().addListener((observableValue, state, t1) -> {
            if(state == Worker.State.RUNNING) {
                webEngine.executeScript("var myMap = L.map('map').setView([50.07, 19.9], 10);");
                webEngine.executeScript("L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {attribution: 'SkiApp by Rafał Wrześniak',}).addTo(myMap)");
                logger.info("Map was loaded");
            }
        });
        webEngine.setOnAlert(stringWebEvent -> zoomLevel = Integer.parseInt(stringWebEvent.getData()));

    }

    void addMarker(TrackPoint trackPoint, String altitude, String markerName, String date) {
        webEngine.executeScript("var "+ markerName.replace(' ', '_') + " = L.marker([" +  trackPoint.getLat() + ", " +  trackPoint.getLon() + "]).addTo(myMap);");
        if(markerName.contains("Max altitude")) altitude = "" + altitude + " [m]";
        webEngine.executeScript(markerName.replace(' ', '_') + ".bindPopup('<b><center>" + altitude + "</center></b>" + date + "<br>" + markerName + "')");
        logger.debug("Marker with date of {} and altitude of {} added to the map", date, altitude);
    }

    void addCircle(TrackPoint trackPoint, String color, String name) {
        name = "circle_" + name;
        name = name.replace('-', '_');
        webEngine.executeScript("var " + name + " = L.circle([" + trackPoint.getLat() + ", " +  trackPoint.getLon()
                + "], {color: '" + color + "', fillColor: 'white', fillOpacity: 0.8, radius: 120}).addTo(myMap);");
        logger.debug("Circle {} with color of {} added to the map", name, color);
    }

    void moveCircle(TrackPoint trackPoint, String name) {
        name = "circle_" + name;
        name = name.replace('-', '_');
        webEngine.executeScript(name + ".setLatLng([" + trackPoint.getLat() + ", " + trackPoint.getLon() + "])");
    }


    void removeObject(String objectName) {
        webEngine.executeScript("myMap.removeLayer(" + objectName + ");");
        logger.debug("{} removed from the map", objectName);
    }

    void setView(double lat, double lon, int zoomLevel) throws IllegalArgumentException {
        if(zoomLevel < -1 || zoomLevel > 19) throw new IllegalArgumentException("Zoom level should be in range 0-19. Use -1 to use default");
        if(zoomLevel == -1) zoomLevel = 9;
        webEngine.executeScript("myMap.setView([" + lat + ", " + lon + "], " + zoomLevel + ");");
    }

    void createTrack(ObservableList<TrackPoint> trackPoints, String color) {
        String polylineList = "[";
        for(TrackPoint trackPoint : trackPoints) {
            polylineList = polylineList.concat("[" + trackPoint.getLat() + ", " + trackPoint.getLon() + "]");
            if(trackPoints.indexOf(trackPoint) != trackPoints.size()-1) {
                polylineList = polylineList.concat(", ");
            } else {
                polylineList = polylineList.concat("]");
            }
        }
        String name = "track_" + trackPoints.get(0).getDate().toString();
        name = name.replace('-', '_');

        webEngine.executeScript("var " + name + " = L.polyline([" + polylineList + "], " +
                "{color: '" + color + "', weight: 3}).addTo(myMap); ");
        webEngine.executeScript("myMap.fitBounds(" + name + ".getBounds());");
        logger.debug("Track {} added to the map", name);

    }

    void clearAll() {
        webEngine.executeScript(
                "for (i in myMap._layers) {" +
                   "   if (myMap._layers[i] instanceof L.Marker || myMap._layers[i] instanceof L.Path) {" +
                   "       myMap.removeLayer(myMap._layers[i]);" +
                   "   }" +
                   "}");
        logger.debug("Everything was removed from the map");
    }

    int getZoom() {
        webEngine.executeScript("alert(myMap.getZoom());");
        return zoomLevel;
    }

}
