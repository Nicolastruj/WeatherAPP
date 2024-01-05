package org.ulpgc.dacd.control;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EventsController {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ExecutorService executorService;
    private final EventReceiver receiver;
    public EventsController(EventReceiver receiver) {
        this.executorService = Executors.newFixedThreadPool(2);
        this.receiver = receiver;
    }

    public void runTask() {
        scheduler.scheduleAtFixedRate(this::executeTask, 0, 15, TimeUnit.MINUTES);
    }

    private void executeTask() {
        /*executorService.submit(() -> {
            try {
                weatherReceiver.receive();
            } catch (MySoftwareException e) {
                e.printStackTrace();
            }
        });

        executorService.submit(() -> {
            try {
                hotelReceiver.receive();
            } catch (MySoftwareException e) {
                e.printStackTrace();
            }
        });*/
        try {
            receiver.receive();
        } catch (MySoftwareException e) {
            throw new RuntimeException(e);
        }
    }

    /*public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException ex) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }*/
}
