package org.ulpgc.dacd.control;

public class Main {
    public static void main(String[] args) {
        EventReceiver receiver = new EventReceiver();
        EventsController hotelEventsController = new EventsController(receiver);
        hotelEventsController.runTask();
    }
}