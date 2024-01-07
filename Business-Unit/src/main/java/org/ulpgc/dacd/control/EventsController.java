package org.ulpgc.dacd.control;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EventsController {
    private final EventReceiver receiver;
    public EventsController(EventReceiver receiver) {
        this.receiver = receiver;
    }

    public void runTask() {
        executeTask();
    }

    private void executeTask() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        try {
            receiver.receive();
        } catch (MySoftwareException e) {
            throw new RuntimeException(e);
        }
    }
}
