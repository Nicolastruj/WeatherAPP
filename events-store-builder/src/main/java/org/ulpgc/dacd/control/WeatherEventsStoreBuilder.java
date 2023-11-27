package org.ulpgc.dacd.control;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class WeatherEventsStoreBuilder implements EventsStoreBuilder {
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static String subject = "JCG_QUEUE";
    public void receiver() throws MyStoreBuilderException {
        try {
            Connection connection = createAndStartConnection();
            Session session = createSession(connection);
            Destination destination = createDestination(session);
            MessageConsumer consumer = createMessageConsumer(session, destination);
            receiveAndProcessMessage(consumer);
        } catch (JMSException e) {
            throw new MyStoreBuilderException("Error in JMS processing", e);
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

    private void receiveAndProcessMessage(MessageConsumer consumer) throws JMSException {
        Message message = consumer.receive();
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            System.out.println("Received message '" + textMessage.getText() + "'");
        }
    }
}
//TODO puede haber posibilidad de dividir esto en metodos