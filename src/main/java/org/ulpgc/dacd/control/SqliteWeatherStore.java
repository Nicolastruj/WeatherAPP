package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;
import java.sql.*;
import java.time.Instant;

public class SqliteWeatherStore implements WeatherStore{
    private String file;
    public SqliteWeatherStore(String file){
        this.file = file;
        createTables();
    }

    private void createTables() {
        String[] islasCanarias = {"Tenerife", "GranCanaria", "Lanzarote", "Fuerteventura", "LaPalma", "LaGomera", "ElHierro", "LaGraciosa"};

        try (Connection connection = open();
             Statement statement = connection.createStatement()) {

            for (String isla : islasCanarias) {
                String createTableQuery = "CREATE TABLE IF NOT EXISTS " + isla + " ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "temp DOUBLE NOT NULL,"
                        + "possibilityOfPrecipitation DOUBLE NOT NULL,"
                        + "humidity INT NOT NULL,"
                        + "cloudisness INT NOT NULL,"
                        + "windSpeed DOUBLE NOT NULL,"
                        + "ts TIMESTAMP NOT NULL"
                        + ")";
                statement.executeUpdate(createTableQuery);
            }

        } catch (SQLException e) {
            System.err.println("Error al crear las tablas: " + e.getMessage());
        }
    }

    public Connection open(){
        String url = "jdbc:sqlite:" + this.file;
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }
    @Override
    public void save(Weather weather) throws SQLException {
        Connection connection = this.open();
        String insert = "INSERT INTO " + weather.getLocation().getIsland()
                + " (temp, possibilityOfPrecipitation, humidity, cloudisness, windSpeed, ts)"
                + " VALUES "
                + "(" + weather.getTemperature() + ", " + weather.getPossibilityOfPrecipitation() + ", "
                + weather.getHumidity() + ", " + weather.getCloudisness() + ", " + weather.getWindSpeed() + ", '"
                + weather.getTimeStand().toString() + "')";
        Statement statement = connection.createStatement();
        statement.execute(insert);
    }

    @Override
    public Weather get(Location location, Instant ts) throws SQLException {
        try (Connection connection = this.open();
             Statement statement = connection.createStatement()) {
            String select = "SELECT * FROM " + location.getIsland() +
                    " WHERE ts LIKE '%" + ts.toString() + "%'";
            ResultSet resultSet = statement.executeQuery(select);
            Weather weather = null;
            while (resultSet.next()) {
                double temp = resultSet.getDouble("temp");
                double precipitationPossibility = resultSet.getDouble("precipitationPossibility");
                int humidity = resultSet.getInt("humidity");
                int cloudisness = resultSet.getInt("cloudisness");
                double windSpeed = resultSet.getDouble("windSpeed");
                weather = new Weather(temp, precipitationPossibility, humidity, cloudisness, windSpeed, ts, location);
            }
            return weather;
        }
    }


    @Override
    public void close() throws Exception {
    }
}
