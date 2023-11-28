package org.ulpgc.dacd.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WeatherController {
    private final WeatherProvider provider;
    private final WeatherStore storer;
    private final List<Location> locations;

    public WeatherController(WeatherProvider weatherProvider, WeatherStore wetherStorer, List<Location> locations) {
        this.provider = weatherProvider;
        this.storer = wetherStorer;
        this.locations = locations;
    }

    public void runTask(){
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    task();
                } catch (JsonProcessingException | MyWeatherException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Calendar startTime = Calendar.getInstance();
        startTime.set(2023, Calendar.NOVEMBER, 19, 0, 0, 0);
        long tiempoDeInicio = startTime.getTimeInMillis();

        long intervaloHoras = 6;
        long intervaloMilisegundos = intervaloHoras * 60 * 60 * 1000;

        timer.scheduleAtFixedRate(task, new Date(tiempoDeInicio), intervaloMilisegundos);
    }//TODO esto esta mal porque no tenia que parar a los cinco dias sino dejarlo ejecutando y se para cuando el programador decidad

    private void task() throws JsonProcessingException, MyWeatherException {
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
