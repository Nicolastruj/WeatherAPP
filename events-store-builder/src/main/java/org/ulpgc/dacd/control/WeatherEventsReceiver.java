package org.ulpgc.dacd.control;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherEventsReceiver implements EventsReceiver{
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static String subject = "topic:prediction.Weather";
    private static String baseDirectory = "eventstore/prediction.Weather/";
    public void receive() throws MyWeatherException {
        try {
            Connection connection = createAndStartConnection();
            Session session = createSession(connection);
            Destination destination = createDestination(session);
            MessageConsumer consumer = createMessageConsumer(session, destination);

            while (true) {
                Message message = consumer.receive();
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    handleTextMessage(textMessage);
                } else {
                    // Manejar otros tipos de mensajes según sea necesario
                }
            }

        } catch (JMSException | IOException e) {
            throw new MyWeatherException("Error in JMS processing", e);
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

    private MessageConsumer createMessageConsumer(Session session, Destination destination) throws JMSException {
        return session.createConsumer(destination);
    }

    private void processMessage(MessageConsumer consumer) throws JMSException, IOException {
        Message message = consumer.receive();
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            handleTextMessage(textMessage);
        }
    }

    private void handleTextMessage(TextMessage textMessage) throws JMSException, IOException {
        String eventData = textMessage.getText();
        long timestamp = System.currentTimeMillis();

        String ss = "prediction-provider";  // Reemplaza con la lógica para obtener el ss
        String formattedDate = formatDate(timestamp);

        File eventStoreDirectory = createEventStoreDirectory(ss, formattedDate);
        String fileName = createFileName(ss, formattedDate);

        writeToFile(eventData, fileName);
    }

    private String formatDate(long timestamp) {
        return new SimpleDateFormat("yyyyMMdd").format(new Date(timestamp));
    }

    private File createEventStoreDirectory(String ss, String formattedDate) {
        File eventStoreDirectory = new File(baseDirectory + ss + "/" + formattedDate);
        eventStoreDirectory.mkdirs();
        return eventStoreDirectory;
    }

    private String createFileName(String ss, String formattedDate) {
        return baseDirectory + ss + "/" + formattedDate + ".events";
    }

    private void writeToFile(String eventData, String fileName) throws IOException {
        File eventFile = new File(fileName);
        try (FileWriter writer = new FileWriter(eventFile, true)) {
            writer.write(eventData + System.lineSeparator());
        }
    }
}
//TODO cambia la fecha del event por la fecha de la prediccion