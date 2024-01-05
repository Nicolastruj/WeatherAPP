package org.ulpgc.dacd.control;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventReceiver implements EventsReceiver {
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static String[] subjects = {"prediction.Weather", "prediction.Hotel"};
    private static String[] baseDirectories = {
            "Business-Unit/src/main/resources/eventstore/prediction.Weather/",
            "Business-Unit/src/main/resources/eventstore/prediction.Hotel/"
    };
    private static String clientID = "Business-Unit";

    private final HotelEventsStore hotelEventsStore = new HotelEventsStore();
    private final WeatherEventsStore weatherEventsStore = new WeatherEventsStore();
    @Override
    public void receive() throws MySoftwareException {
        try {
            Connection connection = createAndStartConnection();
            Session session = createSession(connection);
            for (String subject : subjects) {
                Topic destination = createDestination(session, subject);
                MessageConsumer consumer = createMessageConsumer(session, destination);
                setupMessageListener(consumer);
            }
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
                } catch (JMSException | IOException e) {
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

    private Topic createDestination(Session session, String subject) throws JMSException {
        return session.createTopic(subject);
    }

    private MessageConsumer createMessageConsumer(Session session, Topic destination) throws JMSException {
        return session.createDurableSubscriber(destination, clientID + "-" + destination.getTopicName());
    }

    private void handleTextMessage(TextMessage textMessage) throws JMSException, IOException {
        String eventData = textMessage.getText();
        JsonObject jsonObject = JsonParser.parseString(eventData).getAsJsonObject();
        String ss = getSS(jsonObject);

        // Evaluación del valor de 'ss' para determinar en qué tópico y base de datos guardar el evento
        if (ss.startsWith("H")) {
            // Si el valor de 'ss' comienza con 'H', se trata de eventos de hoteles
            String fileName = createFileName(baseDirectories[1], ss); // Utiliza el segundo directorio
            writeToFile(eventData, fileName);
            hotelEventsStore.saveHotelEvent(eventData); // Guarda en la base de datos de hoteles
        } else if (ss.startsWith("P")) {
            // Si el valor de 'ss' comienza con 'W', se trata de eventos climáticos
            String fileName = createFileName(baseDirectories[0], ss); // Utiliza el primer directorio
            writeToFile(eventData, fileName);
            weatherEventsStore.saveWeatherEvent(eventData); // Guarda en la base de datos de clima
        }
    }

    private String getSS(JsonObject jsonObject) {
        return jsonObject.get("ss").getAsString();
    }

    private String createFileName(String baseDirectory, String ss) {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = localDateTime.format(formatter);
        return baseDirectory + ss + "/" + formattedDate + ".events";
    }

    private void writeToFile(String eventData, String fileName) throws IOException {
        File eventFile = new File(fileName);
        if (!eventFile.getParentFile().exists()) {
            eventFile.getParentFile().mkdirs();
        }
        try (FileWriter writer = new FileWriter(eventFile, true)) {
            writer.write(eventData + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
            throw e;
        }
    }
}
