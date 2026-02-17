package com.mustafabulu.elk.userservice.event;

import com.mustafabulu.elk.eventcontract.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisteredEventConsumer {

    private final ProcessedEventStore processedEventStore;

    @KafkaListener(topics = "${app.kafka.topics.user-registered}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(UserRegisteredEvent event) {
        Objects.requireNonNull(event, "event must not be null");

        if (event.getEventId() == null || event.getEventId().isBlank()) {
            throw new IllegalArgumentException("eventId is required");
        }

        if (!processedEventStore.markAsProcessedIfNew(event.getEventId())) {
            log.info("Duplicate event ignored. eventId={}", event.getEventId());
            return;
        }

        log.info("Consumed user registered event. userId={}, username={}, eventId={}",
                event.getUserId(), event.getUsername(), event.getEventId());
    }

    @KafkaListener(topics = "${app.kafka.topics.user-registered-dlt}", groupId = "${spring.kafka.consumer.group-id}-dlt")
    public void consumeDeadLetter(UserRegisteredEvent event) {
        log.error("Event moved to DLT. eventId={}, username={}, email={}",
                event.getEventId(), event.getUsername(), event.getEmail());
    }
}

