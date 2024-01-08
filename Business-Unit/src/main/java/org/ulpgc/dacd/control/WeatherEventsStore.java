package org.ulpgc.dacd.control;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WeatherEventsStore {

    private static final String JDBC_URL = "jdbc:sqlite:Business-Unit/src/main/resources/databases/WeatherEventsMart.db";

    public void saveWeatherEvent(String eventString) {
        JsonObject eventJson = JsonParser.parseString(eventString).getAsJsonObject();
        String tableName = generateTableName(eventJson);
        createTableIfNotExists(tableName);
        insertWeatherEvent(tableName, eventJson);
    }

    private String generateTableName(JsonObject eventJson) {
        String islandName = eventJson.getAsJsonObject("location").get("island").getAsString();
        return islandName;
    }

    private void createTableIfNotExists(String tableName) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + "predictionTime TEXT PRIMARY KEY,"
                + "temperature REAL,"
                + "precipitation REAL,"
                + "humidity INTEGER,"
                + "clouds INTEGER,"
                + "windSpeed REAL"
                + ");";

        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement stmt = conn.prepareStatement(createTableSQL)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertWeatherEvent(String tableName, JsonObject eventJson) {
        String insertSQL = "INSERT INTO " + tableName + " (predictionTime, temperature, precipitation, humidity, clouds, windSpeed) VALUES (?, ?, ?, ?, ?, ?);";

        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
            stmt.setString(1, eventJson.get("predictionTime").getAsString());
            stmt.setDouble(2, eventJson.get("temperature").getAsDouble());
            stmt.setDouble(3, eventJson.get("precipitation").getAsDouble());
            stmt.setInt(4, eventJson.get("humidity").getAsInt());
            stmt.setInt(5, eventJson.get("clouds").getAsInt());
            stmt.setDouble(6, eventJson.get("windSpeed").getAsDouble());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}