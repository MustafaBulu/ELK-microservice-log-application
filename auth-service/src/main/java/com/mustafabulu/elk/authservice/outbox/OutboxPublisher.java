package com.mustafabulu.elk.authservice.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mustafabulu.elk.authservice.service.UserRegisteredEventProducer;
import com.mustafabulu.elk.eventcontract.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private static final String USER_REGISTERED_EVENT_TYPE = "USER_REGISTERED";

    private final OutboxEventRepository outboxEventRepository;
    private final UserRegisteredEventProducer eventProducer;
    private final ObjectMapper objectMapper;

    @Value("${app.outbox.publisher.max-retries:10}")
    private int maxRetries;

    @Transactional
    @Scheduled(fixedDelayString = "${app.outbox.publisher.fixed-delay-ms:2000}")
    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.NEW);
        for (OutboxEvent outboxEvent : pendingEvents) {
            publishOne(outboxEvent);
        }
    }

    private void publishOne(OutboxEvent outboxEvent) {
        try {
            if (!USER_REGISTERED_EVENT_TYPE.equals(outboxEvent.getEventType())) {
                log.warn("Unsupported outbox event type. id={}, eventType={}", outboxEvent.getId(), outboxEvent.getEventType());
                markFailed(outboxEvent);
                return;
            }

            UserRegisteredEvent event = objectMapper.readValue(outboxEvent.getPayload(), UserRegisteredEvent.class);
            eventProducer.publish(event);
            outboxEvent.setStatus(OutboxStatus.PUBLISHED);
            outboxEvent.setPublishedAt(Instant.now());
            outboxEventRepository.save(outboxEvent);
        } catch (Exception ex) {
            log.error("Failed to publish outbox event. id={}, retryCount={}",
                    outboxEvent.getId(), outboxEvent.getRetryCount(), ex);
            markFailed(outboxEvent);
        }
    }

    private void markFailed(OutboxEvent outboxEvent) {
        int nextRetryCount = outboxEvent.getRetryCount() + 1;
        outboxEvent.setRetryCount(nextRetryCount);
        if (nextRetryCount >= maxRetries) {
            outboxEvent.setStatus(OutboxStatus.FAILED);
        }
        outboxEventRepository.save(outboxEvent);
    }
}

