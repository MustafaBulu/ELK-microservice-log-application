package com.mustafabulu.elk.authservice.service;

import com.mustafabulu.elk.eventcontract.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisteredEventProducer {

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Value("${app.kafka.topics.user-registered}")
    private String userRegisteredTopic;

    public void publish(UserRegisteredEvent event) {
        try {
            kafkaTemplate.send(userRegisteredTopic, String.valueOf(event.getUserId()), event)
                    .get(5, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Publishing interrupted for user registration event", ex);
        } catch (ExecutionException | TimeoutException ex) {
            throw new IllegalStateException("Failed to publish user registration event", ex);
        }
        log.info("Published user registration event. userId={}, eventId={}", event.getUserId(), event.getEventId());
    }
}

