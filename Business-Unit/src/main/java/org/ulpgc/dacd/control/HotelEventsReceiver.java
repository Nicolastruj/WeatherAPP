package org.ulpgc.dacd.control;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import javax.jms.Connection;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class HotelEventsReceiver implements EventsReceiver{
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static String subject = "prediction.Hotel";
    private static String baseDirectory = "Business-Unit/src/main/resources/eventstore/prediction.Hotel/";
    private static String clientID = "Business-Unit";
    private static final String JDBC_URL = "jdbc:sqlite:Business-Unit/src/main/resources/hotelEventsMart.db";  // Actualiza la ruta a tu base de datos
    private static final String TABLE_NAME = "hotels";
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
                } catch (JMSException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (SQLException e) {
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
        return session.createDurableSubscriber(destination,clientID + "-" + subject);
    }

    private void handleTextMessage(TextMessage textMessage) throws JMSException, IOException, SQLException {
        String eventData = textMessage.getText();
        JsonObject jsonObjectHotel = parseJsonHotel(eventData);
        String callInstantValue = getCallInstant(jsonObjectHotel);
        LocalDateTime localDateTime = parseToLocalDateTime(callInstantValue);
        String formattedDate = formatLocalDateTime(localDateTime);
        String ss = getSS(jsonObjectHotel);
        createEventStoreDirectory(ss);
        String fileName = createFileName(ss, formattedDate);
        writeToFile(eventData, fileName);
        storeHotelEvent(jsonObjectHotel);
    }

    private JsonObject parseJsonHotel(String eventData) {
        return JsonParser.parseString(eventData).getAsJsonObject();
    }

    private String getCallInstant(JsonObject jsonObjectWeather) {
        return jsonObjectWeather.get("ts").getAsString();
    }

    private LocalDateTime parseToLocalDateTime(String callInstantValue) {
        Instant instant = Instant.parse(callInstantValue);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    private String formatLocalDateTime(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return localDateTime.format(formatter);
    }//TODO recordar que en el momento del arranque la business unit no va a tener datos del broker porque los weather son cada 6 h por lo tanto en el momento del arranque hay que cogerlos del datalake y luego sigues con los del broker, es solo en el momento del arranque para tener datos

    private String getSS(JsonObject jsonObjectWeather) {
        return jsonObjectWeather.get("ss").getAsString();
    }
    private File createEventStoreDirectory(String ss) {
        File eventStoreDirectory = new File(baseDirectory + ss + "/");

        if (!eventStoreDirectory.exists()) {
            boolean directoriesCreated = eventStoreDirectory.mkdirs();
            if (directoriesCreated) {
                System.out.println("Directorio creado exitosamente en: " + eventStoreDirectory.getAbsolutePath());
            } else {
                System.out.println("No se pudo crear el directorio: " + eventStoreDirectory.getAbsolutePath());
            }
        } else {
            System.out.println("El directorio ya existe: " + eventStoreDirectory.getAbsolutePath());
        }

        return eventStoreDirectory;
    }

    private String createFileName(String ss, String formattedDate) {
        return baseDirectory + ss + "/" + formattedDate + ".events";
    }

    private void writeToFile(String eventData, String fileName) throws IOException {
        File eventFile = new File(fileName);
        try (FileWriter writer = new FileWriter(eventFile, true)) {
            System.out.println("Escribiendo en el archivo: " + fileName);
            writer.write(eventData + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());  // Agregar este registro
            throw e;
        }
    }
    public void storeHotelEvent(JsonObject hotelEvent) throws SQLException {
        try (java.sql.Connection connection = DriverManager.getConnection(JDBC_URL);
             Statement statement = connection.createStatement()) {
            createTableIfNotExists(statement);
            String id = hotelEvent.get("id").getAsString();
            String name = hotelEvent.get("name").getAsString();
            String location = hotelEvent.get("location").getAsString();
            String latitude = hotelEvent.get("latitude").getAsString();
            String longitude = hotelEvent.get("longitude").getAsString();
            double price = hotelEvent.get("price").getAsDouble();
            double pricePerNight = hotelEvent.get("pricePerNight").getAsDouble();
            double discountPerNightForBookingOnline = hotelEvent.get("discountPerNightForBookingOnline").getAsDouble();
            String review = hotelEvent.get("review").getAsString();
            String reviewNumber = hotelEvent.get("reviewNumber").getAsString();
            String distanceToCenter = hotelEvent.get("distanceToCenter").getAsString();
            String starsNumber = hotelEvent.get("starsNumber").getAsString();
            boolean freeCancelation = hotelEvent.get("freeCancelation").getAsBoolean();
            String services = hotelEvent.get("services").getAsString();
            String checkIn = hotelEvent.get("checkIn").getAsString();
            String checkOut = hotelEvent.get("checkOut").getAsString();
            String ss = hotelEvent.get("ss").getAsString();
            Instant ts = Instant.now();

            // Crear el nombre de la tabla basado en la estructura que proporcionaste
            String tableName = TABLE_NAME + "_" + checkIn + "_" + checkOut + "_" + ss;

            // Insertar los datos en la tabla
            String sql = "INSERT INTO " + tableName + " (id, name, location, latitude, longitude, price, " +
                    "pricePerNight, discountPerNightForBookingOnline, review, reviewNumber, distanceToCenter, " +
                    "starsNumber, freeCancelation, services, checkIn, checkOut, ts, ss) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, id);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, location);
                preparedStatement.setString(4, latitude);
                preparedStatement.setString(5, longitude);
                preparedStatement.setDouble(6, price);
                preparedStatement.setDouble(7, pricePerNight);
                preparedStatement.setDouble(8, discountPerNightForBookingOnline);
                preparedStatement.setString(9, review);
                preparedStatement.setString(10, reviewNumber);
                preparedStatement.setString(11, distanceToCenter);
                preparedStatement.setString(12, starsNumber);
                preparedStatement.setBoolean(13, freeCancelation);
                preparedStatement.setString(14, services);
                preparedStatement.setString(15, checkIn);
                preparedStatement.setString(16, checkOut);
                preparedStatement.setTimestamp(17, Timestamp.from(ts));
                preparedStatement.setString(18, ss);

                preparedStatement.executeUpdate();
            }
        }
    }

    private void createTableIfNotExists(Statement statement) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id TEXT PRIMARY KEY, " +
                "name TEXT, " +
                "location TEXT, " +
                "latitude TEXT, " +
                "longitude TEXT, " +
                "price REAL, " +
                "pricePerNight REAL, " +
                "discountPerNightForBookingOnline REAL, " +
                "review TEXT, " +
                "reviewNumber TEXT, " +
                "distanceToCenter TEXT, " +
                "starsNumber TEXT, " +
                "freeCancelation BOOLEAN, " +
                "services TEXT, " +
                "checkIn TEXT, " +
                "checkOut TEXT, " +
                "ts TIMESTAMP, " +
                "ss TEXT)";
        statement.execute(sql);
    }
}

