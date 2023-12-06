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
        String url = buildApiUrl(location);

        try {
            Document document = getWeatherDocument(url);
            String json = document.text();
            JsonObject jsonObject = parseJson(json);
            JsonArray listArray = jsonObject.getAsJsonArray("list");

            return extractFiveDayForecast(listArray, location);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String buildApiUrl(Location location) {
        return "http://api.openweathermap.org/data/2.5/forecast?lat=" + location.getLat() +
                "&lon=" + location.getLon() + "&units=metric" + "&appid=" + this.apiKey;
    }

    private Document getWeatherDocument(String url) throws IOException {
        return Jsoup.connect(url).ignoreContentType(true).get();
    }

    private JsonObject parseJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, JsonObject.class);
    }

    private List<Weather> extractFiveDayForecast(JsonArray listArray, Location location) {
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
    }

    private boolean isNoonPrediction(JsonObject prediction) {
        LocalDateTime localDateTime = extractLocalDateTime(prediction);
        return localDateTime.getHour() == 12 && localDateTime.getMinute() == 0;
    }

    private LocalDateTime extractLocalDateTime(JsonObject prediction) {
        String predictionDateTime = prediction.get("dt_txt").getAsString();
        predictionDateTime = predictionDateTime.replace("Z", "");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(predictionDateTime, formatter);
    }

    private Weather extractWeatherData(JsonObject prediction, Location location) {
        double temperature = extractTemperature(prediction);
        int humidity = extractHumidity(prediction);
        double possibilityOfPrecipitation = extractPossibilityOfPrecipitation(prediction);
        int cloudiness = extractCloudiness(prediction);
        double windSpeed = extractWindSpeed(prediction);
        Instant predictionTime = extractPredictionTime(prediction);
        Instant ts = Instant.now();

        return new Weather(temperature, possibilityOfPrecipitation, humidity, cloudiness, windSpeed, predictionTime, location, ts, "weather-provider");
    }

    private double extractTemperature(JsonObject prediction) {
        JsonObject mainData = prediction.getAsJsonObject("main");
        return mainData.get("temp").getAsDouble();
    }

    private int extractHumidity(JsonObject prediction) {
        JsonObject mainData = prediction.getAsJsonObject("main");
        return mainData.get("humidity").getAsInt();
    }

    private double extractPossibilityOfPrecipitation(JsonObject prediction) {
        return prediction.get("pop").getAsDouble();
    }

    private int extractCloudiness(JsonObject prediction) {
        JsonObject cloudsData = prediction.getAsJsonObject("clouds");
        return cloudsData.get("all").getAsInt();
    }

    private double extractWindSpeed(JsonObject prediction) {
        JsonObject windData = prediction.getAsJsonObject("wind");
        return windData.get("speed").getAsDouble();
    }

    private Instant extractPredictionTime(JsonObject prediction) {
        String predictionDateTime = prediction.get("dt_txt").getAsString();
        predictionDateTime = predictionDateTime.replace(" ", "T") + "Z";
        return Instant.parse(predictionDateTime);
    }
}
