package org.ulpgc.dacd.control;

import java.time.Instant;
import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;

public interface WeatherProvider {
    public Weather get(Location location, Instant ts);
}
