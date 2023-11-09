package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;

import java.io.File;
import java.sql.*;
import java.time.Instant;

public class SqliteWeatherStore implements WeatherStore{
    private String file;
    public SqliteWeatherStore(String file){
        this.file = file;
    }
    public Connection open(){
        String url = "jdbc:sqlite"+this.file;
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return connection;
    }
    @Override
    public void Save(Weather weather) throws SQLException {
        Connection connection = this.open();
        String insert = "INSERT INTO "+ weather.getLocaiton().getIsland()
                + " (temp, precipitationPossibility, humidity, cloudisness, windSpeed, ts)"
                + "VALUES "
                + "('" + weather.getTemperature() + ", " + weather.getPossibilityOfPrecipitation() + ", "
                + weather.getHumidity() + ", " + weather.getCloudisness() + ", " + weather.getWindSpeed() + ", "
                + weather.getTimeStand() + ")";
        Statement statement = connection.createStatement();
        statement.execute(insert);
    }

    @Override
    public Weather get(Location location, Instant ts) throws SQLException {
        Connection connection = this.open();
        String select = "SELECT * " + "FROM " + location.getIsland() + " WHERE ts LIKE " + "'" + "FROM"
                + ts.toString() + "'";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(select);
        Weather weather = null;
        while (resultSet.next()){
            double temp = resultSet.getDouble("temp");
            double precipitationPossibility = resultSet.getDouble("precipitationPossibility");
            int humidity = resultSet.getInt("humidity");
            int cloudisness = resultSet.getInt("cloudisness");
            double windSpeed = resultSet.getDouble("windSpeed");
            weather = new Weather(temp, precipitationPossibility, humidity, cloudisness, windSpeed, ts, location);
        }
        return weather;
    }

    @Override
    public void close() throws Exception {
        String url = "jdbc:sqlite;ç:src/main/resources/WeatherDataBase.db";
        Connection connection = DriverManager.getConnection(url);
        connection.close();
    }
}//Autoclosable
