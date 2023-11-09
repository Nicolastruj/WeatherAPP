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
    public Weather get(Location location) {
        String url = "http://api.openweathermap.org/data/2.5/forecast?lat=" + String.valueOf(location.getLat()) + "&lon=" + String.valueOf(location.getLon()) + "&units=metric" + "&appid=" + this.apiKey;
        try {
            Document document = Jsoup.connect(url).ignoreContentType(true).get();
            String json = document.text();
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
            JsonArray listArray = jsonObject.getAsJsonArray("list");
            JsonObject firstPrediction = listArray.get(0).getAsJsonObject();

            JsonObject mainData = firstPrediction.getAsJsonObject("main");
            Double temperature = mainData.get("temp").getAsDouble();
            Integer humidity = mainData.get("humidity").getAsInt();

            // Usar "pop" para la probabilidad de precipitación
            Double possibilityOfPrecipitation = firstPrediction.get("pop").getAsDouble();

            JsonObject cloudsData = firstPrediction.getAsJsonObject("clouds");
            Integer cloudiness = cloudsData.get("all").getAsInt();

            JsonObject windData = firstPrediction.getAsJsonObject("wind");
            Double windSpeed = windData.get("speed").getAsDouble();

            String predictionDateTime = firstPrediction.get("dt_txt").getAsString();
            predictionDateTime = predictionDateTime.replace(" ", "T") + "Z";
            Instant predictionTime = Instant.parse(predictionDateTime);

            // Crear un objeto Weather con los atributos como finales
            Weather weather = new Weather(
                    temperature,
                    possibilityOfPrecipitation,
                    humidity,
                    cloudiness,
                    windSpeed,
                    predictionTime,
                    location
            );

            return weather;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
