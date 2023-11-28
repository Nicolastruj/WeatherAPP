package org.ulpgc.dacd.control;

public interface EventsReceiver {
    public void receive() throws MyWeatherException;
}
