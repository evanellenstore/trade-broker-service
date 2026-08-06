package com.trade.broker.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TickPublisher {

	@Value("${market.kafka.topic}")
    private String marketTickTopic;

	private static final Logger log = LoggerFactory.getLogger(TickPublisher.class);

	@Autowired(required = false)
	private KafkaTemplate<String, String> kafkaTemplate;

	public void publish(String message) {
		if (kafkaTemplate == null) {
			log.warn("Kafka producer is not configured; skipping tick publication to topic {}", marketTickTopic);
			return;
		}

		try {
			kafkaTemplate.send(marketTickTopic, message);
		} catch (Exception ex) {
			log.warn("Unable to publish tick event to Kafka topic {}", marketTickTopic, ex);
		}
	}
}
