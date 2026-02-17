package com.mustafabulu.elk.authservice.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mustafabulu.elk.eventcontract.UserRegisteredEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxServiceImplUnitTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OutboxServiceImpl outboxService;

    @Test
    void enqueueUserRegisteredEvent_shouldPersistNewOutboxRecord() throws Exception {
        UserRegisteredEvent event = UserRegisteredEvent.create(101, "demo-user", "demo@test.com");
        when(objectMapper.writeValueAsString(event)).thenReturn("{\"eventType\":\"USER_REGISTERED\"}");

        outboxService.enqueueUserRegisteredEvent(event);

        ArgumentCaptor<OutboxEvent> outboxCaptor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository).save(outboxCaptor.capture());
        OutboxEvent saved = outboxCaptor.getValue();
        assertThat(saved.getAggregateType()).isEqualTo("USER");
        assertThat(saved.getAggregateId()).isEqualTo("101");
        assertThat(saved.getEventType()).isEqualTo("USER_REGISTERED");
        assertThat(saved.getStatus()).isEqualTo(OutboxStatus.NEW);
        assertThat(saved.getRetryCount()).isZero();
        assertThat(saved.getPayload()).contains("USER_REGISTERED");
    }

    @Test
    void enqueueUserRegisteredEvent_shouldThrowWhenSerializationFails() throws Exception {
        UserRegisteredEvent event = UserRegisteredEvent.create(101, "demo-user", "demo@test.com");
        when(objectMapper.writeValueAsString(event))
                .thenThrow(new JsonProcessingException("serialization failed") { });

        assertThatThrownBy(() -> outboxService.enqueueUserRegisteredEvent(event))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Failed to serialize user registered event");

        verify(outboxEventRepository, never()).save(any(OutboxEvent.class));
    }
}

