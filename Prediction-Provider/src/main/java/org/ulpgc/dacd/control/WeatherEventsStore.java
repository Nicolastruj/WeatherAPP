package org.ulpgc.dacd.control;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.ulpgc.dacd.model.Weather;
import javax.jms.*;
import java.time.Instant;


public class WeatherEventsStore implements WeatherStore {

    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static String subject = "prediction.Weather";

    public WeatherEventsStore(){}
    public void save(Weather weatherPrediction) throws MyWeatherException {
        Connection connection = null;
        try {
            connection = createAndStartConnection();
            Session session = createSession(connection);
            Topic destination = createDestination(session);
            MessageProducer producer = createMessageProducer(session, destination);
            TextMessage message = createTextMessage(session, weatherPrediction);
            sendMessage(producer, message);
        } catch (JMSException e) {
            throw new MyWeatherException("Error in JMS processing", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    throw new MyWeatherException("Error in JMS connection", e);
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

    private Topic createDestination(Session session) throws JMSException {
        return session.createTopic(subject);
    }

    private MessageProducer createMessageProducer(Session session, Destination destination) throws JMSException {
        return session.createProducer(destination);
    }
    private TextMessage createTextMessage(Session session, Weather weatherPrediction) throws JMSException {
        try {
            String serializedData = serializeWeatherToJson(weatherPrediction);
            return session.createTextMessage(serializedData);
        } catch (Exception e) {
            throw new JMSException("Error creating the text message: " + e.getMessage());
        }
    }
    private String serializeWeatherToJson(Weather weather) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();
        return gson.toJson(weather);
    }
    private void sendMessage(MessageProducer producer, TextMessage message) throws JMSException {
        producer.send(message);
    }
}