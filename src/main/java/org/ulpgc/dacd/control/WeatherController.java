package org.ulpgc.dacd.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class WeatherController {
    private final WeatherProvider provider;
    private final WeatherStore storer;
    private final List<Location> locations;

    public WeatherController(WeatherProvider weatherProvider, WeatherStore weatherStore, List<Location> locations){
        this.provider = weatherProvider;
        this.storer = weatherStore;
        this.locations = locations;
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
        for (Location location : locations) {
            List<Weather> weatherList = provider.get(location);
            if (weatherList != null && !weatherList.isEmpty()) {
                for (Weather weather : weatherList) {
                    storer.Save(weather);
                }
            }
        }
    }

}
