package org.ulpgc.dacd.control;

import java.time.Instant;
import java.util.List;

import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;

public interface WeatherProvider {
    public List<Weather> get(Location location);
}
