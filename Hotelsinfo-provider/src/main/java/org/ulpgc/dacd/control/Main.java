package org.ulpgc.dacd.control;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        HotelsProvider provider = new TipsterHotelsProvider();

        try {
            List<JsonObject> hotels = provider.getHotels();

            if (hotels != null && !hotels.isEmpty()) {
                for (JsonObject hotel : hotels) {
                    System.out.println(new Gson().toJson(hotel));
                }
            } else {
                System.out.println("No se encontraron hoteles.");
            }
        } catch (Exception e) {
            System.err.println("Error al obtener hoteles: " + e.getMessage());
            e.printStackTrace();
        }
    }
}