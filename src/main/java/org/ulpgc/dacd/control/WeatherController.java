package org.ulpgc.dacd.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WeatherController {
    private final WeatherProvider provider;
    private final WeatherStore storer;
    private final List<Location> locations;

    public WeatherController(WeatherProvider weatherProvider, WeatherStore weatherStore, List<Location> locations) {
        this.provider = weatherProvider;
        this.storer = weatherStore;
        this.locations = locations;
    }

    public void runTask() throws IOException, SQLException {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    task();
                } catch (JsonProcessingException | SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        long intervaloHoras = 6;
        long intervaloMilisegundos = intervaloHoras * 60 * 60 * 1000;
        long duracionTotalDias = 5;
        long duracionTotalMilisegundos = duracionTotalDias * 24 * 60 * 60 * 1000;
        long tiempoDeInicio = System.currentTimeMillis();

        timer.scheduleAtFixedRate(task, new Date(tiempoDeInicio), intervaloMilisegundos);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                task.cancel();
                timer.cancel();
            }
        }, new Date(tiempoDeInicio + duracionTotalMilisegundos));
    }

    private void task() throws JsonProcessingException, SQLException {
        for (Location location : locations) {
            List<Weather> weatherList = provider.get(location);
            if (weatherList != null && !weatherList.isEmpty()) {
                for (Weather weather : weatherList) {
                    storer.save(weather);
                }
            }
        }
    }
}
