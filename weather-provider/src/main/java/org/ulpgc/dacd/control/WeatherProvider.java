package org.ulpgc.dacd.control;

import java.util.List;
import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;

import javax.jms.JMSException;

public interface WeatherProvider {
    public List<Weather> get(Location location);
}
