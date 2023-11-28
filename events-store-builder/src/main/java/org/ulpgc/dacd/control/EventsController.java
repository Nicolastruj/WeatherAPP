package org.ulpgc.dacd.control;

import org.apache.activemq.thread.Task;

public class EventsController {
    private final EventsReceiver receiver;

    public EventsController(EventsReceiver weatherEventsReceiver){
        this.receiver = weatherEventsReceiver;
    }
    public void runTask(){
        try {
            Task();
        } catch (MyWeatherException e) {
            throw new RuntimeException(e);
        }
    }
    public void Task() throws MyWeatherException {
        this.receiver.receive();
    }

    public EventsReceiver getReceiver() {
        return receiver;
    }
}
