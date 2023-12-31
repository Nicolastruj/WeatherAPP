package org.ulpgc.dacd.control;

import com.google.gson.JsonObject;
import org.ulpgc.dacd.model.Hotel;

import java.util.List;

public interface HotelsProvider {
    List<Hotel> getHotels(String apiKey, String apiHost, String checkinDate, String checkoutDate,
                          String adultsNumber, String childrensNumber, String childrensAge, String roomNumber,
                          String islandName) throws MyHotelException;
}
