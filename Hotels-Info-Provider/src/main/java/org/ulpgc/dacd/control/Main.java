package org.ulpgc.dacd.control;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        HotelsProvider bookingHotelsProvider = new BookingHotelsProvider();
        HotelsStore bookingHotelsStorer = new BookingEventsStore();
        HotelsController controller = new HotelsController(bookingHotelsProvider, bookingHotelsStorer, getIslandNames());
        String apiKey = args[0];
        String apiHost = args[1];
        String adultsNumber = args[2];
        String childrensNumber = args[3];
        String childrensAge = args[4];
        String roomNumber = args[5];
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