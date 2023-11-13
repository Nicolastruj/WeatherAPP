package org.ulpgc.dacd.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;

import java.io.IOException;
import java.sql.SQLException;
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
    public void runTask() throws IOException, SQLException {
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
    private void Task() throws JsonProcessingException, SQLException {
        /*ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());*/
        storer.Save(this.provider.get(new Location(28.498371,-13.900472, "Fuerteventura")));
        storer.Save(this.provider.get(new Location(28.116044,-15.429279, "GranCanaria")));
        storer.Save(this.provider.get(new Location(28.964191,-13.546709, "Lanzarote")));
        storer.Save(this.provider.get(new Location(29.233322,-13.500906, "LaGraciosa")));
        storer.Save(this.provider.get(new Location(28.466531,-16.251671, "Tenerife")));
        storer.Save(this.provider.get(new Location(28.682925,-17.765297, "LaPalma")));
        storer.Save(this.provider.get(new Location(28.098011,-17.107600, "LaGomera")));
        storer.Save(this.provider.get(new Location(28.098011,-17.107600, "ElHierro")));
        /*String jsonWeatherFuerteventura = objectMapper.writeValueAsString(weatherFuerteventura);
        System.out.println(jsonWeatherFuerteventura);*/
    }//TODO guardar en la base de datos aqui
    public WeatherStore getStorer(){
        return this.storer;
    }
}
