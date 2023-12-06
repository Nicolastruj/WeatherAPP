package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Location;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException{
        if (args.length < 1) {
            System.out.println("Por favor, proporciona la API key como argumento.");
            return;
        }
        String apiKey = args[0];
        WeatherProvider weatherProvider = new OpenWeatherMapProvider(apiKey);
        WeatherStore weatherStore = new WeatherEventsStore();
        List<Location> locations = getLocations();
        WeatherController openMapWeatherController = new WeatherController(weatherProvider, weatherStore, locations);
        openMapWeatherController.runTask();
    }
    public static List<Location> getLocations(){
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(28.498371, -13.900472, "Fuerteventura"));
        locations.add(new Location(28.116044, -15.429279, "GranCanaria"));
        locations.add(new Location(28.964191, -13.546709, "Lanzarote"));
        locations.add(new Location(29.233322, -13.500906, "LaGraciosa"));
        locations.add(new Location(28.466531, -16.251671, "Tenerife"));
        locations.add(new Location(28.682925, -17.765297, "LaPalma"));
        locations.add(new Location(28.098011, -17.107600, "LaGomera"));
        locations.add(new Location(28.098011, -17.107600, "ElHierro"));
        return locations;
    }
}
