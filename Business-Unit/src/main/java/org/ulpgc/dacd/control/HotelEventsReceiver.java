package org.ulpgc.dacd.control;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class HotelEventsReceiver implements EventsReceiver{
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static String subject = "prediction.Hotel";
    private static String baseDirectory = "eventstore/prediction.Hotel/";
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
                } catch (JMSException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
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

    private void handleTextMessage(TextMessage textMessage) throws JMSException, IOException {
        String eventData = textMessage.getText();
        JsonObject jsonObjectWeather = parseJsonWeather(eventData);
        String callInstantValue = getCallInstant(jsonObjectWeather);
        LocalDateTime localDateTime = parseToLocalDateTime(callInstantValue);
        String formattedDate = formatLocalDateTime(localDateTime);
        String ss = getSS(jsonObjectWeather);
        createEventStoreDirectory(ss);
        String fileName = createFileName(ss, formattedDate);
        writeToFile(eventData, fileName);
    }

    private JsonObject parseJsonWeather(String eventData) {
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
            System.out.println("Creating directory: " + eventStoreDirectory.getAbsolutePath());
            if (eventStoreDirectory.mkdirs()) {
                System.out.println("Directory created successfully!");
            } else {
                System.out.println("Failed to create directory.");
            }
        } else {
            System.out.println("Directory already exists: " + eventStoreDirectory.getAbsolutePath());
        }
        return eventStoreDirectory;
    }

    private String createFileName(String ss, String formattedDate) {
        return baseDirectory + ss + "/" + formattedDate + ".events";
    }

    private void writeToFile(String eventData, String fileName) throws IOException {
        File eventFile = new File(fileName);
        try (FileWriter writer = new FileWriter(eventFile, true)) {
            writer.write(eventData + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());  // Agregar este registro
            throw e;
        }
    }
}

