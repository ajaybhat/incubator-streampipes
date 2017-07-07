package de.fzi.cep.sepa.sdk.helpers;

import de.fzi.cep.sepa.model.impl.JmsTransportProtocol;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;

/**
 * Created by riemer on 28.01.2017.
 */
public class Protocols {

  public static KafkaTransportProtocol kafka(String kafkaHost, Integer kafkaPort, String topic) {
    return new KafkaTransportProtocol(kafkaHost, kafkaPort, topic, kafkaHost, kafkaPort);
  }

  public static JmsTransportProtocol jms(String jmsHost, Integer jmsPort, String topic) {
    return new JmsTransportProtocol(jmsHost, jmsPort, topic);
  }
}