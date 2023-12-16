package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Hotel;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        HotelsProvider bookingProvider = new BookingHotelsProvider();
        try {
            List<Hotel> hotelList = bookingProvider.getHotels();
        } catch (MyHotelException e) {
            throw new RuntimeException(e);
        }
    }
}