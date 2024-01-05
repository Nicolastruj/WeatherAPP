package org.ulpgc.dacd.control;

public class EventsController {
    private final EventsReceiver weatherReceiver;
    private final EventsReceiver hotelReceiver;

    public EventsController(EventsReceiver weatherEventsReceiver, EventsReceiver hotelEventsReceiver) {
        this.weatherReceiver = weatherEventsReceiver;
        this.hotelReceiver = hotelEventsReceiver;
    }

    public void runTask() {
        try {
            task();
        } catch (MySoftwareException e) {
            throw new RuntimeException(e);
        }
    }

    public void task() throws MySoftwareException {
        this.weatherReceiver.receive();
        this.hotelReceiver.receive();
    }
}
