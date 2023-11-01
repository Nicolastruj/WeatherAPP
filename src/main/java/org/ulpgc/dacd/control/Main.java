package org.ulpgc.dacd.control;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) throws IOException {
        //File file = new File("LocationFile.txt");
        WeatherProvider weatherProvider = new OpenWeatherMapProvider("src/main/resources/APIKey.txt");
        Instant now = Instant.now();
        Weather weather = weatherProvider.get(new Location(28.498371,-13.900472, "Fuerteventura"), now);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(weather);
        System.out.println(json);
    }
    /*public void runTask(){

    }*/
}
//TODO implementar la llamada http con jsoup, pasarla a un jsonobject, mirar la documentacion de la pagina, mirar la clase instant, hacer las peticiones a la base de datos con jdbc y hacer las interfaces