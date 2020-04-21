package SkiApp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;


public class TrackPoint {

    private double lat;
    private double lon;
    private double alt;
    private LocalDate date;
    private LocalTime time;

    public TrackPoint(String lat, String lon, String alt, String dateTime) {
        setLat(lat);
        setLon(lon);
        setAlt(alt);
        setDate(trimInputZ(dateTime));
        setTime(trimInputZ(dateTime));
    }

    private String trimInputZ (String dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("DateTime argument cannot be null!");
        } else if (dateTime.equals("")) {
            throw new IllegalArgumentException("DateTime argument cannot be empty!");
        }
        if(dateTime.endsWith("Z")){
            return dateTime.substring(0, dateTime.length()-1);
        } else {
            return dateTime;
        }
    }

    private void setDate(String dateTime) {
        this.date = LocalDateTime.parse(dateTime).toLocalDate();
    }

    private void setTime(String dateTime) {
        this.time = LocalDateTime.parse(dateTime).toLocalTime();
    }

    private void setLat(String lat) {
        if (lat == null) {
            throw new IllegalArgumentException("Latitude argument cannot be null!");
        } else if (lat.equals("")) {
            throw new IllegalArgumentException("Latitude argument cannot be empty!");
        }
        this.lat = Double.parseDouble(lat);
    }

    private void setLon(String lon) {
        if (lon == null) {
            throw new IllegalArgumentException("Longitude argument cannot be null!");
        } else if (lon.equals("")) {
            throw new IllegalArgumentException("Longitude argument cannot be empty!");
        }
        this.lon = Double.parseDouble(lon);
    }

    private void setAlt(String alt) {
        if (alt == null) {
            throw new IllegalArgumentException("Altitude argument cannot be null!");
        } else if (alt.equals("")) {
            throw new IllegalArgumentException("Altitude argument cannot be empty!");
        }
        this.alt = Double.parseDouble(alt);
    }

    void printTrackPoint() {
        System.out.println(String.join(": ", "DateTime", String.join(" ", getDate().toString(), getTime().toString())));
        System.out.println(String.join(": ", "Position", String.join(", ", String.valueOf(getLat()), String.valueOf(getLon()))));
        System.out.println(String.join(": ", "Altitude", String.valueOf(getAlt())));
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double getAlt() {
        return alt;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }
}


