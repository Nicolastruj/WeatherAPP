package org.ulpgc.dacd.control;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.ulpgc.dacd.model.Location;
import org.ulpgc.dacd.model.Weather;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OpenWeatherMapProvider implements WeatherProvider {
    private String apiKey;

    public OpenWeatherMapProvider(String apiKey) throws IOException {
        this.apiKey = apiKey;
    }

    @Override
    public List<Weather> get(Location location) {
        String url = "http://api.openweathermap.org/data/2.5/forecast?lat=" + location.getLat() +
                "&lon=" + location.getLon() + "&units=metric" + "&appid=" + this.apiKey;

        try {
            Document document = Jsoup.connect(url).ignoreContentType(true).get();
            String json = document.text();
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
            JsonArray listArray = jsonObject.getAsJsonArray("list");

            List<Weather> fiveDayForecast = new ArrayList<>();

            for (int i = 0; i < listArray.size(); i++) {
                JsonObject prediction = listArray.get(i).getAsJsonObject();

                if (isNoonPrediction(prediction)) {
                    Weather weather = extractWeatherData(prediction, location);
                    fiveDayForecast.add(weather);

                    if (fiveDayForecast.size() == 5) {
                        break;
                    }
                }
            }

            return fiveDayForecast;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isNoonPrediction(JsonObject prediction) {
        String predictionDateTime = prediction.get("dt_txt").getAsString();
        predictionDateTime = predictionDateTime.replace("Z", "");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(predictionDateTime, formatter);

        return localDateTime.getHour() == 12 && localDateTime.getMinute() == 0;
    }

    private Weather extractWeatherData(JsonObject prediction, Location location) {
        JsonObject mainData = prediction.getAsJsonObject("main");
        double temperature = mainData.get("temp").getAsDouble();
        int humidity = mainData.get("humidity").getAsInt();
        double possibilityOfPrecipitation = prediction.get("pop").getAsDouble();
        JsonObject cloudsData = prediction.getAsJsonObject("clouds");
        int cloudiness = cloudsData.get("all").getAsInt();
        JsonObject windData = prediction.getAsJsonObject("wind");
        double windSpeed = windData.get("speed").getAsDouble();
        String predictionDateTime = prediction.get("dt_txt").getAsString();
        predictionDateTime = predictionDateTime.replace(" ", "T") + "Z";
        Instant predictionTime = Instant.parse(predictionDateTime);

        return new Weather(temperature, possibilityOfPrecipitation, humidity, cloudiness, windSpeed, predictionTime, location);
    }
}
