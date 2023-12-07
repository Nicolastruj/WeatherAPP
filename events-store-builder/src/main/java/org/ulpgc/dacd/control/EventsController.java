package org.ulpgc.dacd.control;

public class EventsController {
    private final EventsReceiver receiver;

    public EventsController(EventsReceiver weatherEventsReceiver){
        this.receiver = weatherEventsReceiver;
    }
    public void runTask(){
        try {
            task();
        } catch (MyWeatherException e) {
            throw new RuntimeException(e);
        }
    }
    public void task() throws MyWeatherException {
        this.receiver.receive();
    }

    public EventsReceiver getReceiver() {
        return receiver;
    }
}
