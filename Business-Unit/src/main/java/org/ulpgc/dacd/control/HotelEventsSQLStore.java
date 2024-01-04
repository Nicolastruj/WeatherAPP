package org.ulpgc.dacd.control;
import com.google.gson.JsonObject;

import java.sql.*;
import java.time.Instant;
import java.util.List;

public class HotelEventsSQLStore {

    private static final String JDBC_URL = "jdbc:sqlite:Business-Unit/src/main/resources/hotelEventsMart.db";  // Actualiza la ruta a tu base de datos
    private static final String TABLE_NAME = "hotels";

    public void storeHotelEvent(JsonObject hotelEvent) throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL);
             Statement statement = connection.createStatement()) {
            createTableIfNotExists(statement);
            String id = hotelEvent.get("id").getAsString();
            String name = hotelEvent.get("name").getAsString();
            String location = hotelEvent.get("location").getAsString();
            String latitude = hotelEvent.get("latitude").getAsString();
            String longitude = hotelEvent.get("longitude").getAsString();
            double price = hotelEvent.get("price").getAsDouble();
            double pricePerNight = hotelEvent.get("pricePerNight").getAsDouble();
            double discountPerNightForBookingOnline = hotelEvent.get("discountPerNightForBookingOnline").getAsDouble();
            String review = hotelEvent.get("review").getAsString();
            String reviewNumber = hotelEvent.get("reviewNumber").getAsString();
            String distanceToCenter = hotelEvent.get("distanceToCenter").getAsString();
            String starsNumber = hotelEvent.get("starsNumber").getAsString();
            boolean freeCancelation = hotelEvent.get("freeCancelation").getAsBoolean();
            String services = hotelEvent.get("services").getAsString();
            String checkIn = hotelEvent.get("checkIn").getAsString();
            String checkOut = hotelEvent.get("checkOut").getAsString();
            String ss = hotelEvent.get("ss").getAsString();
            Instant ts = Instant.now();

            // Crear el nombre de la tabla basado en la estructura que proporcionaste
            String tableName = TABLE_NAME + "_" + checkIn + "_" + checkOut + "_" + ss;

            // Insertar los datos en la tabla
            String sql = "INSERT INTO " + tableName + " (id, name, location, latitude, longitude, price, " +
                    "pricePerNight, discountPerNightForBookingOnline, review, reviewNumber, distanceToCenter, " +
                    "starsNumber, freeCancelation, services, checkIn, checkOut, ts, ss) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, id);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, location);
                preparedStatement.setString(4, latitude);
                preparedStatement.setString(5, longitude);
                preparedStatement.setDouble(6, price);
                preparedStatement.setDouble(7, pricePerNight);
                preparedStatement.setDouble(8, discountPerNightForBookingOnline);
                preparedStatement.setString(9, review);
                preparedStatement.setString(10, reviewNumber);
                preparedStatement.setString(11, distanceToCenter);
                preparedStatement.setString(12, starsNumber);
                preparedStatement.setBoolean(13, freeCancelation);
                preparedStatement.setString(14, services);
                preparedStatement.setString(15, checkIn);
                preparedStatement.setString(16, checkOut);
                preparedStatement.setTimestamp(17, Timestamp.from(ts));
                preparedStatement.setString(18, ss);

                preparedStatement.executeUpdate();
            }
        }
    }

    private void createTableIfNotExists(Statement statement) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id TEXT PRIMARY KEY, " +
                "name TEXT, " +
                "location TEXT, " +
                "latitude TEXT, " +
                "longitude TEXT, " +
                "price REAL, " +
                "pricePerNight REAL, " +
                "discountPerNightForBookingOnline REAL, " +
                "review TEXT, " +
                "reviewNumber TEXT, " +
                "distanceToCenter TEXT, " +
                "starsNumber TEXT, " +
                "freeCancelation BOOLEAN, " +
                "services TEXT, " +
                "checkIn TEXT, " +
                "checkOut TEXT, " +
                "ts TIMESTAMP, " +
                "ss TEXT)";
        statement.execute(sql);
    }
}