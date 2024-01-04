package org.ulpgc.dacd.control;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EventsController {
    private final EventsReceiver weatherReceiver;
    private final EventsReceiver hotelReceiver;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2); // 2 hilos

    public EventsController(EventsReceiver weatherEventsReceiver, EventsReceiver hotelReceiver) {
        this.weatherReceiver = weatherEventsReceiver;
        this.hotelReceiver = hotelReceiver;
    }

    public void runTask() {
        scheduler.scheduleAtFixedRate(() -> {
            executeTask();
        }, 0, 15, TimeUnit.MINUTES);
    }

    private void executeTask() {
        // Crea un nuevo ExecutorService para las tareas actuales
        var executorService = Executors.newFixedThreadPool(2);

        executorService.submit(() -> {
            try {
                weatherReceiver.receive();
            } catch (MySoftwareException e) {
                e.printStackTrace(); // O manejar el error según tu necesidad
            }
        });

        executorService.submit(() -> {
            try {
                hotelReceiver.receive();
            } catch (MySoftwareException e) {
                e.printStackTrace(); // O manejar el error según tu necesidad
            }
        });

        // Cierra el ExecutorService después de completar las tareas
        executorService.shutdown();
    }

    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException ex) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
