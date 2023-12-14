package org.ulpgc.dacd.control;

public class Main {
    public static void main(String[] args) {
        EventsReceiver weatherEventsReceiver = new WeatherEventsReceiver();
        EventsController weatherEventsController = new EventsController(weatherEventsReceiver);
        weatherEventsController.runTask();
    }
}