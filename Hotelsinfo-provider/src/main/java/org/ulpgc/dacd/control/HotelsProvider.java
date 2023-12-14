package org.ulpgc.dacd.control;

import com.google.gson.JsonObject;

import java.util.List;

public interface HotelsProvider {
    public List<JsonObject> getHotels();
}
