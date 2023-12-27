package org.ulpgc.dacd.control;

public class Main {
    public static void main(String[] args) {
        HotelsProvider bookingHotelsProvider = new BookingHotelsProvider();
        HotelsStore bookingHotelsStorer = new BookingEventsStore();
        HotelsController controller = new HotelsController(bookingHotelsProvider, bookingHotelsStorer);
    }
}