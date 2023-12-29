package org.ulpgc.dacd.control;

public class Main {
    public static void main(String[] args) {
        EventsReceiver hotelEventsReceiver = new HotelEventsReceiver();
        EventsController hotelEventsController = new EventsController(hotelEventsReceiver);
        hotelEventsController.runTask();
    }
}