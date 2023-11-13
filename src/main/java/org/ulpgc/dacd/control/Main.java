package org.ulpgc.dacd.control;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Main {
    public static void main(String[] args) throws IOException, SQLException {
        WeatherProvider weatherProvider = new OpenWeatherMapProvider("src/main/resources/APIKey.txt");//TODO pasarlo arecibirlo en linea de comandos
        WeatherStore weatherStore = new SqliteWeatherStore("src/main/resources/WeatherDataBase.db");
        WeatherController openMapWeatherController = new WeatherController(weatherProvider, weatherStore);
        openMapWeatherController.runTask();
    }
}
//TODO implementar la llamada http con jsoup, pasarla a un jsonobject, mirar la documentacion de la pagina, mirar la clase instant, hacer las peticiones a la base de datos con jdbc y hacer las interfaces
//TODO Solucion de serializacion con gson instant
