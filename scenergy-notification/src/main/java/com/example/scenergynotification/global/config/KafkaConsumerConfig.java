package com.example.scenergynotification.global.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.example.scenergynotification.domain.notification.controller.request.OnFollowRequest;
import com.example.scenergynotification.domain.notification.controller.request.OnLikeRequest;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

	@Bean
	ConcurrentKafkaListenerContainerFactory<String, OnFollowRequest> followEventKafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, OnFollowRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(followEventConsumerFactory());
		return factory;
	}

	@Bean
	public ConsumerFactory<String, OnFollowRequest> followEventConsumerFactory() {
		return new DefaultKafkaConsumerFactory<>(consumerConfigurations(), new StringDeserializer(),
			new JsonDeserializer<>(OnFollowRequest.class, false));
	}

	@Bean
	public ConsumerFactory<String, OnLikeRequest> likeEventConsumerFactory() {
		return new DefaultKafkaConsumerFactory<>(consumerConfigurations(), new StringDeserializer(),
			new JsonDeserializer<>(OnLikeRequest.class, false));
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, OnLikeRequest> likeEventKafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, OnLikeRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(likeEventConsumerFactory());
		return factory;
	}

	@Bean
	public Map<String, Object> consumerConfigurations() {
		Map<String, Object> config = new HashMap<>();
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "http://localhost:10001");

		config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
		config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

		config.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
		config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

		config.put(ConsumerConfig.GROUP_ID_CONFIG, "follow-group");
		return config;
	}
}