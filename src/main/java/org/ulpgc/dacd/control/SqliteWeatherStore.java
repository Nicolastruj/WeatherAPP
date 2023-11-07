package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Weather;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
    public void Save(Weather weather) {

    }

    @Override
    public void close() throws Exception {

    }
}//Autoclosable
