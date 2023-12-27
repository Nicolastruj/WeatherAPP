package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Hotel;
import org.ulpgc.dacd.model.Location;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HotelsController {
    private final HotelsProvider provider;
    private final HotelsStore storer;
    private final ScheduledExecutorService scheduler;
    public HotelsController(HotelsProvider provider, HotelsStore storer){
        this.provider = provider;
        this.storer = storer;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }
    public void runTask(){
        scheduler.scheduleAtFixedRate(this::task, 0, 6, TimeUnit.SECONDS);
    }
    public void task(){
        try {
            for (Hotel hotel : provider.getHotels()) {
                storer.save(hotel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
