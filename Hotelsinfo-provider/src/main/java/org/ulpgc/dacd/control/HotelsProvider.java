package org.ulpgc.dacd.control;

import com.google.gson.JsonObject;
import org.ulpgc.dacd.model.Hotel;

import java.util.List;

public interface HotelsProvider {
    public List<Hotel> getHotels() throws MyHotelException;
}
