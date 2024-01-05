package org.ulpgc.dacd.control;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;

public class HotelEventsStore {

    private static final String JDBC_URL = "jdbc:sqlite:Business-Unit/src/main/resources/databases/HotelEventsMart.db";

    public void saveHotelEvent(String eventString) {
        JsonObject eventJson = JsonParser.parseString(eventString).getAsJsonObject();
        String tableName = generateTableName(eventJson);
        createTableIfNotExists(tableName);
        insertHotelEvent(tableName, eventJson);
    }

    private String generateTableName(JsonObject eventJson) {
        String islandName = eventJson.get("islandName").getAsString();
        Instant tomorrow = Instant.now().plusSeconds(24 * 60 * 60);
        String ts = eventJson.get("ts").getAsString().replace("T", " ").substring(0, 19);
        String timePart = ts.substring(11, 19);
        return islandName;
    }

    private void createTableIfNotExists(String tableName) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + "id VARCHAR(255) PRIMARY KEY,"
                + "name VARCHAR(255),"
                + "location VARCHAR(255),"
                + "latitude VARCHAR(50),"
                + "longitude VARCHAR(50),"
                + "islandName VARCHAR(100),"
                + "price DOUBLE,"
                + "pricePerNight DOUBLE,"
                + "discountPerNightForBookingOnline DOUBLE,"
                + "review VARCHAR(255),"
                + "reviewNumber VARCHAR(50),"
                + "distanceToCenter VARCHAR(50),"
                + "starsNumber VARCHAR(50),"
                + "freeCancelation BOOLEAN,"
                + "services VARCHAR(500),"
                + "checkIn VARCHAR(50),"
                + "checkOut VARCHAR(50),"
                + "ts VARCHAR(50),"
                + "ss VARCHAR(255)"
                + ");";

        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement stmt = conn.prepareStatement(createTableSQL)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertHotelEvent(String tableName, JsonObject eventJson) {
        String insertSQL = "INSERT INTO " + tableName + " (id, name, location, latitude, longitude, islandName, price, pricePerNight, " +
                "discountPerNightForBookingOnline, review, reviewNumber, distanceToCenter, starsNumber, freeCancelation, services, checkIn, checkOut, " +
                "ts, ss) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
            stmt.setString(1, eventJson.get("id").getAsString());
            stmt.setString(2, eventJson.get("name").getAsString());
            stmt.setString(3, eventJson.get("location").getAsString());
            stmt.setString(4, eventJson.get("latitude").getAsString());
            stmt.setString(5, eventJson.get("longitude").getAsString());
            stmt.setString(6, eventJson.get("islandName").getAsString());
            stmt.setDouble(7, eventJson.get("price").getAsDouble());
            stmt.setDouble(8, eventJson.get("pricePerNight").getAsDouble());
            stmt.setDouble(9, eventJson.get("discountPerNightForBookingOnline").getAsDouble());
            stmt.setString(10, eventJson.get("review").getAsString());
            stmt.setString(11, eventJson.get("reviewNumber").getAsString());
            stmt.setString(12, eventJson.get("distanceToCenter").getAsString());
            stmt.setString(13, eventJson.get("starsNumber").getAsString());
            stmt.setBoolean(14, eventJson.get("freeCancelation").getAsBoolean());
            stmt.setString(15, eventJson.get("services").toString());
            stmt.setString(16, eventJson.get("checkIn").getAsString());
            stmt.setString(17, eventJson.get("checkOut").getAsString());
            stmt.setString(18, eventJson.get("ts").getAsString());
            stmt.setString(19, eventJson.get("ss").getAsString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
