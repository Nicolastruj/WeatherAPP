package org.ulpgc.dacd.control;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        HotelsProvider bookingHotelsProvider = new BookingHotelsProvider();
        HotelsStore bookingHotelsStorer = new BookingEventsStore();
        HotelsController controller = new HotelsController(bookingHotelsProvider, bookingHotelsStorer, getIslandNames());
        String apiKey = "597710c62dmsh05f6cf3b401fb3ep174551jsnd58ccf2826ae";
        String apiHost = "booking-com.p.rapidapi.com";
        String adultsNumber = "1";
        String childrensNumber = "1";
        String childrensAge = "3";
        String roomNumber = "1";
        controller.runTask(apiKey, apiHost, adultsNumber, childrensNumber, childrensAge, roomNumber);
    }
    public static List<String> getIslandNames() {
        List<String> islands = new ArrayList<>();
        islands.add("Tenerife");
        islands.add("GranCanaria");
        islands.add("Lanzarote");
        islands.add("Fuerteventura");
        islands.add("LaPalma");
        islands.add("LaGomera");
        islands.add("ElHierro");
        islands.add("LaGraciosa");
        return islands;
    }
}