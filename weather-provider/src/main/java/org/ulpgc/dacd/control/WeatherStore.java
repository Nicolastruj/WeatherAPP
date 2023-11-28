package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Weather;
import java.util.List;

public interface WeatherStore {
    public void save(Weather weatherPrediction)throws MyWeatherException;
}
