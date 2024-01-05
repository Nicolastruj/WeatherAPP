package org.ulpgc.dacd.control;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.ulpgc.dacd.model.Hotel;

import javax.jms.*;
import java.time.Instant;

public class BookingEventsStore implements HotelsStore{
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static String subject = "prediction.Hotel";
    @Override
    public void save(Hotel hotelPrediction) throws MyHotelException {
        Connection connection = null;
        try {
            connection = createAndStartConnection();
            Session session = createSession(connection);
            Destination destination = createDestination(session);
            MessageProducer producer = createMessageProducer(session, destination);
            TextMessage message = createTextMessage(session, hotelPrediction);
            sendMessage(producer, message);
        } catch (JMSException e) {
            throw new MyHotelException("Error in JMS processing", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    throw new MyHotelException("Error in JMS connection", e);
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
        return session.createTopic(subject);
    }

    private MessageProducer createMessageProducer(Session session, Destination destination) throws JMSException {
        return session.createProducer(destination);
    }
    private TextMessage createTextMessage(Session session, Hotel hotelPrediction) throws JMSException {
        try {
            String serializedData = serializeHotelToJson(hotelPrediction);
            return session.createTextMessage(serializedData);
        } catch (Exception e) {
            throw new JMSException("Error al crear el mensaje de texto: " + e.getMessage());
        }
    }
    private String serializeHotelToJson(Hotel hotel) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();
        return gson.toJson(hotel);
    }
    private void sendMessage(MessageProducer producer, TextMessage message) throws JMSException {
        producer.send(message);
    }
}