package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Hotel;

public interface HotelsStore {
    public void save(Hotel hotelPrediction) throws MyHotelException;
}
