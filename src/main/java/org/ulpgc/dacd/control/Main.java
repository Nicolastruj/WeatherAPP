package org.ulpgc.dacd.control;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Main {
    public static void main(String[] args) throws IOException {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    runTask();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        // Calcular el intervalo en milisegundos para ejecutar cada 3 horas
        long intervaloHoras = 3;
        long intervaloMilisegundos = intervaloHoras * 60 * 60 * 1000;

        // Programar la tarea para ejecutarse cada 3 horas durante 5 días
        Date horaDeInicio = new Date(); // Hora actual
        long duracionEnMilisegundos = 5 * 24 * 60 * 60 * 1000; // 5 días

        timer.scheduleAtFixedRate(task, horaDeInicio, intervaloMilisegundos);

        // Cancelar la tarea después de 5 días
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                task.cancel();
                timer.cancel();
            }
        }, duracionEnMilisegundos);
    }
    public static void runTask()throws IOException{
        WeatherProvider weatherProvider = new OpenWeatherMapProvider("src/main/resources/APIKey.txt");
        Instant now = Instant.now();
        Weather weather = weatherProvider.get(new Location(28.498371,-13.900472, "Fuerteventura"), now);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Habilitar el soporte de tipos de fecha y hora de Java 8
        String json = objectMapper.writeValueAsString(weather);
        System.out.println(json);
    }
}
//TODO implementar la llamada http con jsoup, pasarla a un jsonobject, mirar la documentacion de la pagina, mirar la clase instant, hacer las peticiones a la base de datos con jdbc y hacer las interfaces