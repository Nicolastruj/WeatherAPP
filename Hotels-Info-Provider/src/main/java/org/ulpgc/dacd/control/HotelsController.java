package org.ulpgc.dacd.control;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.ulpgc.dacd.model.Hotel;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HotelsController {
    private final HotelsProvider provider;
    private final HotelsStore storer;
    private final ScheduledExecutorService scheduler;
    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
            .create();
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
                String hotelJson = gson.toJson(hotel);
                System.out.println(hotelJson);
                storer.save(hotel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
//TODO quitar el gson