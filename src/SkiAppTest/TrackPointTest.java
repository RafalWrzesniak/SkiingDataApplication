package SkiAppTest;

import SkiApp.TrackPoint;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class TrackPointLatLonAltTest {

    static Collection<Object[]> latLonAltParams() {
        return Arrays.asList(new Object[][]{
                {"51.4423", 51.4423},
                {"13.3455", 13.3455},
                {"00.2344", 0.2344},
                {"1897.373046875", 1897.373046875},
                {"2678.4365625", 2678.4365625}
        });
    }

    // get lat
    @ParameterizedTest(name = "#{index} - Test with Argument = {arguments}")
    @MethodSource("latLonAltParams")
    void getLatParametrized(String input, double output) {
        TrackPoint trackPoint = new TrackPoint(input, "22.3534", "1897.373046875", "2019-03-10T09:30:44Z");
        assertEquals(output, trackPoint.getLat());
    }

    @org.junit.jupiter.api.Test
    void getLatNullInput() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new TrackPoint(null, "22.3534", "1897.373046875", "2019-03-10T09:30:44Z"));
        assertEquals("Latitude argument cannot be null!", exception.getMessage());
    }

    @org.junit.jupiter.api.Test
    void getLatEmptyInput() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new TrackPoint("", "22.3534", "1897.373046875", "2019-03-10T09:30:44Z"));
        assertEquals("Latitude argument cannot be empty!", exception.getMessage());
    }

    @org.junit.jupiter.api.Test
    void getLatNotDoubleInput() {
        String wrongString = "20someString";
        IllegalArgumentException exception = assertThrows(NumberFormatException.class,
                () -> new TrackPoint(wrongString, "22.3534", "1897.373046875", "2019-03-10T09:30:44Z"));
        assertEquals("For input string: \"" + wrongString + "\"", exception.getMessage());
    }

    // get lon
    @ParameterizedTest(name = "#{index} - Test with Argument = {arguments}")
    @MethodSource("latLonAltParams")
    void getLonParametrized(String input, double output) {
        TrackPoint trackPoint = new TrackPoint("51.6654", input, "1897.373046875", "2019-03-10T09:30:44Z");
        assertEquals(output, trackPoint.getLon());
    }

    @org.junit.jupiter.api.Test
    void getLonNullInput() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new TrackPoint("51.6654", null, "1897.373046875", "2019-03-10T09:30:44Z"));
        assertEquals("Longitude argument cannot be null!", exception.getMessage());
    }

    @org.junit.jupiter.api.Test
    void getLonEmptyInput() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new TrackPoint("51.6654", "", "1897.373046875", "2019-03-10T09:30:44Z"));
        assertEquals("Longitude argument cannot be empty!", exception.getMessage());
    }

    @org.junit.jupiter.api.Test
    void getLonNotDoubleInput() {
        String wrongString = "20someString";
        IllegalArgumentException exception = assertThrows(NumberFormatException.class,
                () -> new TrackPoint("51.6654", wrongString, "1897.373046875", "2019-03-10T09:30:44Z"));
        assertEquals("For input string: \"" + wrongString + "\"", exception.getMessage());
    }


    // get alt
    @ParameterizedTest(name = "#{index} - Test with Argument = {arguments}")
    @MethodSource("latLonAltParams")
    void getAltParametrized(String input, double output) {
        TrackPoint trackPoint = new TrackPoint("51.6654", "22.5543", input, "2019-03-10T09:30:44Z");
        assertEquals(output, trackPoint.getAlt());
    }

    @org.junit.jupiter.api.Test
    void getAltNullInput() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new TrackPoint("51.6654", "22.5543", null, "2019-03-10T09:30:44Z"));
        assertEquals("Altitude argument cannot be null!", exception.getMessage());
    }

    @org.junit.jupiter.api.Test
    void getAltEmptyInput() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new TrackPoint("51.6654", "22.5543", "", "2019-03-10T09:30:44Z"));
        assertEquals("Altitude argument cannot be empty!", exception.getMessage());
    }

    @org.junit.jupiter.api.Test
    void getAltnNotDoubleInput() {
        String wrongString = "20someString";
        IllegalArgumentException exception = assertThrows(NumberFormatException.class,
                () -> new TrackPoint("51.6654", "22.5543", wrongString, "2019-03-10T09:30:44Z"));
        assertEquals("For input string: \"" + wrongString + "\"", exception.getMessage());
    }

}


class TrackPointDateTimeTest {

    static Collection<Object[]> dateTimeParams() {
        return Arrays.asList(new Object[][]{
                {"2019-03-10T09:31:20Z", LocalDate.of(2019, 3, 10), LocalTime.of(9, 31, 20)},
                {"2019-12-12T19:21:20", LocalDate.of(2019, 12, 12), LocalTime.of(19, 21, 20)},
                {"2020-01-11T04:01:02Z", LocalDate.of(2020, 1, 11), LocalTime.of(4, 1, 2)},
                {"2021-04-16T09:04:06", LocalDate.of(2021, 4, 16), LocalTime.of(9, 4, 6)},
                {"2016-02-12T09:31:45Z", LocalDate.of(2016, 2, 12), LocalTime.of(9, 31, 45)},
        });
    }

    // get date
    @ParameterizedTest(name = "#{index} - Test with Argument = {arguments}")
    @MethodSource("dateTimeParams")
    void getDate(String input, LocalDate localDate) {
        TrackPoint trackPoint = new TrackPoint("51.6654", "22.5543", "1764.657535", input);
        assertEquals(localDate, trackPoint.getDate());
    }
    // get time
    @ParameterizedTest(name = "#{index} - Test with Argument = {arguments}")
    @MethodSource("dateTimeParams")
    void getTime(String input, LocalDate localDate, LocalTime localTime) {
        TrackPoint trackPoint = new TrackPoint("51.6654", "22.5543", "1764.657535", input);
        assertEquals(localTime, trackPoint.getTime());
    }

    // date time
    @org.junit.jupiter.api.Test
    void getDateTimeNullInput() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new TrackPoint("51.6654", "22.5543", "1764.657535", null));
        assertEquals("DateTime argument cannot be null!", exception.getMessage());
    }

    @org.junit.jupiter.api.Test
    void getDateTimeEmptyInput() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new TrackPoint("51.6654", "22.5543", "1764.657535", ""));
        assertEquals("DateTime argument cannot be empty!", exception.getMessage());
    }


    @ParameterizedTest(name = "#{index} - Test with Argument = {arguments}")
    @ValueSource(strings = { "2019-03-10T9:31:20Z", "2019-3-10T09:31:20Z", "2019-03-3T09:31:20Z", "2019-3-10t09:31:20Z",
            "2019-3-10T9:31:20Z", "2019-3-10T09:31:206Z", "someString" })
    void getDateTimeWrongInput(String input) {
        assertThrows(DateTimeParseException.class,
                () -> new TrackPoint("51.6654", "22.5543", "1764.657535", input));
    }

}