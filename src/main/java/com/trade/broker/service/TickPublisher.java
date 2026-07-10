package com.trade.broker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TickPublisher {

	private static final Logger log = LoggerFactory.getLogger(TickPublisher.class);

	@Autowired(required = false)
	private KafkaTemplate<String, String> kafkaTemplate;

	public void publish(String topic, String message) {
		if (kafkaTemplate == null) {
			log.warn("Kafka producer is not configured; skipping tick publication to topic {}", topic);
			return;
		}

		try {
			kafkaTemplate.send(topic, message);
		} catch (Exception ex) {
			log.warn("Unable to publish tick event to Kafka topic {}", topic, ex);
		}
	}
}
