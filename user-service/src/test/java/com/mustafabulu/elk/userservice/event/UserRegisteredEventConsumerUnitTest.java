package com.mustafabulu.elk.userservice.event;

import com.mustafabulu.elk.eventcontract.UserRegisteredEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRegisteredEventConsumerUnitTest {

    @Mock
    private ProcessedEventStore processedEventStore;

    @InjectMocks
    private UserRegisteredEventConsumer consumer;

    @Test
    void consume_shouldThrowWhenEventIsNull() {
        assertThatThrownBy(() -> consumer.consume(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("event must not be null");
    }

    @Test
    void consume_shouldThrowWhenEventIdIsBlank() {
        UserRegisteredEvent event = buildEvent("  ");

        assertThatThrownBy(() -> consumer.consume(event))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("eventId is required");
    }

    @Test
    void consume_shouldIgnoreDuplicateEvent() {
        UserRegisteredEvent event = buildEvent("evt-1");
        when(processedEventStore.markAsProcessedIfNew("evt-1")).thenReturn(false);

        consumer.consume(event);

        verify(processedEventStore).markAsProcessedIfNew("evt-1");
    }

    @Test
    void consume_shouldProcessNewEvent() {
        UserRegisteredEvent event = buildEvent("evt-2");
        when(processedEventStore.markAsProcessedIfNew("evt-2")).thenReturn(true);

        consumer.consume(event);

        verify(processedEventStore).markAsProcessedIfNew("evt-2");
    }

    private UserRegisteredEvent buildEvent(String eventId) {
        UserRegisteredEvent event = new UserRegisteredEvent();
        event.setEventId(eventId);
        event.setEventType("USER_REGISTERED");
        event.setOccurredAt(Instant.now());
        event.setUserId(11);
        event.setUsername("unit-user");
        event.setEmail("unit-user@test.com");
        return event;
    }
}

