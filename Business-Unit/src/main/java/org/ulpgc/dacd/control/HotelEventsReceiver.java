package org.ulpgc.dacd.control;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;

public class HotelEventsReceiver implements EventsReceiver {
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static String subject = "prediction.Hotel";
    private static String dbUrl = "jdbc:mysql://localhost:3306/tu_base_de_datos";
    private static String dbUser = "tu_usuario";
    private static String dbPassword = "tu_contraseña";

    private static String clientID = "Business-Unit";

    public void receive() throws MySoftwareException {
        try {
            Connection connection = createAndStartConnection();
            Session session = createSession(connection);
            Topic destination = createDestination(session);
            MessageConsumer consumer = createMessageConsumer(session, destination);
            setupMessageListener(consumer);
        } catch (JMSException e) {
            throw new MySoftwareException("Error in JMS processing", e);
        }
    }

    private void setupMessageListener(MessageConsumer consumer) throws JMSException {
        consumer.setMessageListener(message -> {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                try {
                    handleTextMessage(textMessage);
                } catch (JMSException | SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private Connection createAndStartConnection() throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.setClientID(clientID);
        connection.start();
        return connection;
    }

    private Session createSession(Connection connection) throws JMSException {
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    private Topic createDestination(Session session) throws JMSException {
        return session.createTopic(subject);
    }

    private MessageConsumer createMessageConsumer(Session session, Topic destination) throws JMSException {
        return session.createDurableSubscriber(destination, clientID + "-" + subject);
    }

    private void handleTextMessage(TextMessage textMessage) throws JMSException, SQLException {
        String eventData = textMessage.getText();
        JsonObject jsonObjectHotel = parseJsonHotel(eventData);

        String id = jsonObjectHotel.get("id").getAsString();
        String name = jsonObjectHotel.get("name").getAsString();
        String location = jsonObjectHotel.get("location").getAsString();
        double price = jsonObjectHotel.get("price").getAsDouble();
        double pricePerNight = jsonObjectHotel.get("pricePerNight").getAsDouble();
        double discountPerNightForBookingOnline = jsonObjectHotel.get("discountPerNightForBookingOnline").getAsDouble();
        String review = jsonObjectHotel.get("review").getAsString();
        String reviewNumber = jsonObjectHotel.get("reviewNumber").getAsString();
        String distanceToCenter = jsonObjectHotel.get("distanceToCenter").getAsString();
        String starsNumber = jsonObjectHotel.get("starsNumber").getAsString();
        boolean freeCancelation = jsonObjectHotel.get("freeCancelation").getAsBoolean();
        String checkIn = jsonObjectHotel.get("checkIn").getAsString();
        String checkOut = jsonObjectHotel.get("checkOut").getAsString();
        Instant ts = Instant.parse(jsonObjectHotel.get("ts").getAsString());
        String ss = jsonObjectHotel.get("ss").getAsString();

        insertEventToDatabase(id, name, location, price, pricePerNight, discountPerNightForBookingOnline, review, reviewNumber, distanceToCenter, starsNumber, freeCancelation, checkIn, checkOut, ts, ss);
    }

    private void insertEventToDatabase(String id, String name, String location, double price, double pricePerNight, double discountPerNightForBookingOnline, String review, String reviewNumber, String distanceToCenter, String starsNumber, boolean freeCancelation, String checkIn, String checkOut, Instant ts, String ss) throws SQLException {
        String tableName = generateTableName(checkIn, checkOut, ss);
        String query = "INSERT INTO " + tableName + " (id, name, location, price, pricePerNight, discountPerNightForBookingOnline, review, reviewNumber, distanceToCenter, starsNumber, freeCancelation, checkIn, checkOut, ts, ss) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (
                java.sql.Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, location);
            preparedStatement.setDouble(4, price);
            preparedStatement.setDouble(5, pricePerNight);
            preparedStatement.setDouble(6, discountPerNightForBookingOnline);
            preparedStatement.setString(7, review);
            preparedStatement.setString(8, reviewNumber);
            preparedStatement.setString(9, distanceToCenter);
            preparedStatement.setString(10, starsNumber);
            preparedStatement.setBoolean(11, freeCancelation);
            preparedStatement.setString(12, checkIn);
            preparedStatement.setString(13, checkOut);
            preparedStatement.setObject(14, ts);
            preparedStatement.setString(15, ss);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Evento insertado exitosamente en la base de datos.");
            } else {
                System.out.println("No se pudo insertar el evento en la base de datos.");
            }
        }
    }

    private JsonObject parseJsonHotel(String eventData) {
        return JsonParser.parseString(eventData).getAsJsonObject();
    }
    private String generateTableName(String checkIn, String checkOut, String ss) {
        return checkIn + "_" + checkOut + "_" + ss;
    }
}
