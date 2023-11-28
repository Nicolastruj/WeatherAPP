package org.ulpgc.dacd.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WeatherController {
    private final WeatherProvider provider;
    private final WeatherStore storer;
    private final List<Location> locations;
    private final ScheduledExecutorService scheduler;

    public WeatherController(WeatherProvider weatherProvider, WeatherStore weatherStorer, List<Location> locations) {
        this.provider = weatherProvider;
        this.storer = weatherStorer;
        this.locations = locations;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void runTask(){
        // Programar tarea para ejecutarse cada 6 horas
        scheduler.scheduleAtFixedRate(this::task, 0, 6, TimeUnit.HOURS);
    }
    private void task() {
        try {
            for (Location location : locations) {
                List<Weather> weatherList = provider.get(location);
                if (weatherList != null && !weatherList.isEmpty()) {
                    for (Weather weather : weatherList) {
                        storer.save(weather);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();  // Puedes cambiar esto según tu manejo de excepciones
            // También puedes registrar los detalles en un archivo de registro
        }
    }
}
