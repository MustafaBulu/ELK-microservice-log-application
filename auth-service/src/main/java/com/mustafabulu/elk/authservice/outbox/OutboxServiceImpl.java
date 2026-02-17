package com.mustafabulu.elk.authservice.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mustafabulu.elk.eventcontract.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxServiceImpl implements OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void enqueueUserRegisteredEvent(UserRegisteredEvent event) {
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setAggregateType("USER");
        outboxEvent.setAggregateId(String.valueOf(event.getUserId()));
        outboxEvent.setEventType(event.getEventType());
        outboxEvent.setStatus(OutboxStatus.NEW);
        outboxEvent.setRetryCount(0);
        outboxEvent.setPayload(toJson(event));
        outboxEventRepository.save(outboxEvent);
    }

    private String toJson(UserRegisteredEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize user registered event", e);
        }
    }
}

