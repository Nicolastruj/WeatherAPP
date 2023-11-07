package org.ulpgc.dacd.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class WeatherController {
    private final WeatherProvider provider;
    private final WeatherStore storer;

    public WeatherController(WeatherProvider weatherProvider, WeatherStore weatherStore){
        this.provider = weatherProvider;
        this.storer = weatherStore;
    }
    public void runTask() throws IOException{
        Task();
        /*Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    Task();
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        // Calcular el intervalo en milisegundos para ejecutar cada 6 horas
        long intervaloHoras = 6;
        long intervaloMilisegundos = intervaloHoras * 60 * 60 * 1000;

        // Programar la tarea para ejecutarse cada 6 horas durante 5 días
        Date horaDeInicio = new Date(2023, 11, 7, 12, 0, 0); // poner la hora
        long duracionEnMilisegundos = 5 * 24 * 60 * 60 * 1000; // 5 días

        timer.scheduleAtFixedRate(task, horaDeInicio, intervaloMilisegundos);

        // Cancelar la tarea después de 5 días
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                task.cancel();
                timer.cancel();
            }
        }, duracionEnMilisegundos);*/
    }
    private void Task() throws JsonProcessingException {
        /*ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());*/
        
        this.provider.get(new Location(28.498371,-13.900472, "Fuerteventura"));
        this.provider.get(new Location(28.116044,-15.429279, "Gran Canaria"));
        this.provider.get(new Location(28.964191,-13.546709, "Lanzarote"));
        this.provider.get(new Location(29.233322,-13.500906, "La Graciosa"));
        this.provider.get(new Location(28.466531,-16.251671, "Tenerife"));
        this.provider.get(new Location(28.682925,-17.765297, "La Palma"));
        this.provider.get(new Location(28.098011,-17.107600, "La Gomera"));
        this.provider.get(new Location(28.098011,-17.107600, "El Hierro"));
        /*String jsonWeatherFuerteventura = objectMapper.writeValueAsString(weatherFuerteventura);
        System.out.println(jsonWeatherFuerteventura);*/
    }
}
