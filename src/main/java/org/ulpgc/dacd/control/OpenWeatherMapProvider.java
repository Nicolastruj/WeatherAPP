package org.ulpgc.dacd.control;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;

public class OpenWeatherMapProvider implements WeatherProvider {
    private String apiKey;
    public OpenWeatherMapProvider(String fileName) throws IOException {
        this.apiKey = ReadFile(fileName);
    }
    public String ReadFile(String fileName) throws IOException {
        File file = new File(fileName);
        FileReader reader = new FileReader(file);
        StringBuilder apiKeyBuilder = new StringBuilder();
        int character;

        while ((character = reader.read()) != -1) {
            apiKeyBuilder.append((char) character);
        }

        return apiKeyBuilder.toString().trim();
    }

    @Override
    public Weather get(Location location, Instant ts) {
        String url = "http://api.openweathermap.org/data/2.5/forecast?lat="+String.valueOf(location.getLat())+"&lon="+String.valueOf(location.getLon())+"&appid="+this.apiKey;
        try {
            Document document = Jsoup.connect(url).ignoreContentType(true).get();
            String json = document.text();
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
            JsonArray listArray = jsonObject.getAsJsonArray("list");
            JsonObject mainData = listArray.get(0).getAsJsonObject().getAsJsonObject("main");
            JsonObject rainData = listArray.get(0).getAsJsonObject().getAsJsonObject("rain");
            JsonObject cloudsData = listArray.get(0).getAsJsonObject().getAsJsonObject("clouds");
            JsonObject windData = listArray.get(0).getAsJsonObject().getAsJsonObject("wind");


            // Crea un objeto Weather y asigna los valores correspondientes
            Weather weather = new Weather();
            weather.setTemperature(mainData.get("temp").getAsDouble());
            weather.setPrecipitation(rainData.get("3h").getAsDouble());
            weather.setHumidity(mainData.get("humidity").getAsInt());
            weather.setClouds(cloudsData.get("all").getAsInt());
            weather.setWindSpeed(windData.get("speed").getAsDouble());
            weather.setLocaiton(location);
            weather.setTimeStand(ts);
            return weather;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
