package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Hotel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HotelsController {
    private final HotelsProvider provider;
    private final HotelsStore storer;
    private final List<String> islandList;
    private final ScheduledExecutorService scheduler;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-DD");
    public HotelsController(HotelsProvider provider, HotelsStore storer, List<String> islandList){
        this.provider = provider;
        this.storer = storer;
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.islandList = islandList;
    }
    public void runTask(String apiKey, String apiHost, String adultsNumber,
                        String childrensNumber, String childrensAge, String roomNumber) {
        scheduler.scheduleAtFixedRate(() -> task(apiKey, apiHost, formatter.format(LocalDate.now().plusDays(1)),
                formatter.format(LocalDate.now().plusDays(5)),
                adultsNumber, childrensNumber, childrensAge, roomNumber), 0, 6, TimeUnit.SECONDS);
    }
    public void task(String apiKey, String apiHost, String checkinDate, String checkoutDate,
                     String adultsNumber, String childrensNumber, String childrensAge, String roomNumber) {
        try {
            for (String islandName : islandList) {
                List<Hotel> hotels = provider.getHotels(apiKey, apiHost, checkinDate, checkoutDate,
                        adultsNumber, childrensNumber, childrensAge, roomNumber,
                        islandName);
                if (hotels != null && !hotels.isEmpty()) {
                    for (Hotel hotel : hotels) {
                        storer.save(hotel);
                    }
                }else{
                    System.out.println("Hotels in "+ islandName + " on " + checkinDate + " not found.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
