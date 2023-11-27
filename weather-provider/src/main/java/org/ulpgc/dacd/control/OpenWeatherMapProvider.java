package org.ulpgc.dacd.control;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
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
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class OpenWeatherMapProvider implements WeatherProvider {
    private String apiKey;
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static String subject = "WEATHER_QUEUE";
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

    public void send(List<Weather> weatherList) throws MyStoreBuilderException {
        Connection connection = null;
        try {
            connection = createAndStartConnection();
            Session session = createSession(connection);
            Destination destination = createDestination(session);
            MessageProducer producer = createMessageProducer(session, destination);
            TextMessage message = createTextMessage(session, weatherList);
            sendMessage(producer, message);
        } catch (JMSException e) {
            throw new MyStoreBuilderException("Error in JMS processing", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    throw new MyStoreBuilderException("Error in JMS connection", e);
                }
            }
        }
    }

    private Connection createAndStartConnection() throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        return connection;
    }

    private Session createSession(Connection connection) throws JMSException {
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    private Destination createDestination(Session session) throws JMSException {
        return session.createQueue(subject);
    }

    private MessageProducer createMessageProducer(Session session, Destination destination) throws JMSException {
        return session.createProducer(destination);
    }

    private TextMessage createTextMessage(Session session, List<Weather> weatherList) throws JMSException {
        // Aquí debes implementar la lógica para serializar el objeto WeatherList
        // y crear un TextMessage con el contenido serializado.
        // Por ejemplo:
        // String serializedData = serializeWeatherList(weatherList);
        // return session.createTextMessage(serializedData);
        return session.createTextMessage("Aqui va el objeto serializado");
    }

    private void sendMessage(MessageProducer producer, TextMessage message) throws JMSException {
        producer.send(message);
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

        return new Weather(temperature, possibilityOfPrecipitation, humidity, cloudiness, windSpeed, predictionTime, location, petitionInstant, dataFont);
    }
}//TODO cambiar la clase para que envie objetos serializados
