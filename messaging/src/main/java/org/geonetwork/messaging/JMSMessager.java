package org.geonetwork.messaging;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.ApplicationEvent;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

/**
 * Created by francois on 05/11/15.
 */
public class JMSMessager {
    private String jmsUrl;

    public String getJmsUrl() {
        return jmsUrl;
    }

    public void setJmsUrl(String jmsUrl) {
        this.jmsUrl = jmsUrl;
    }

    public void sendMessage(String queue, ApplicationEvent event) {
        try {
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(this.jmsUrl);

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue(queue);

            // Create a MessageProducer from the Session to the Topic or Queue
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // Create a messages
            ObjectMessage message = session.createObjectMessage(event);

            // Tell the producer to send the message
            producer.send(message);

            // Clean up
            session.close();
            connection.close();
        } catch (Exception e) {
            // TODO : dedicated logger needed
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }
}
