package org.ulpgc.dacd.control;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        HotelsProvider bookingProvider = new BookingHotelsProvider();
        bookingProvider.getHotels("Gran Canaria");
    }
}