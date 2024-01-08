package org.ulpgc.dacd.control;


public class EventsController {
    private final EventReceiver receiver;
    public EventsController(EventReceiver receiver) {
        this.receiver = receiver;
    }

    public void runTask() {
        executeTask();
    }

    private void executeTask() {
        try {
            receiver.receive();
        } catch (MySoftwareException e) {
            throw new RuntimeException(e);
        }
    }
}
