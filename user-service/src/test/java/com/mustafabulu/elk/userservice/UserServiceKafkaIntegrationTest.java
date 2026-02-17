package com.mustafabulu.elk.userservice;

import com.mustafabulu.elk.eventcontract.UserRegisteredEvent;
import com.mustafabulu.elk.userservice.event.ProcessedEventStore;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false"
})
class UserServiceKafkaIntegrationTest {

    private static final String USER_REGISTERED_TOPIC = "user.registered.v1";
    private static final String USER_REGISTERED_DLT_TOPIC = "user.registered.v1.dlt";
    private static final long DLT_POLL_TIMEOUT_MS = 15000;
    private static final long PROCESS_WAIT_TIMEOUT_MS = 10000;
    private static final int EXPECTED_SINGLE_PROCESS_COUNT = 1;

    @Container
    static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));

    @Autowired
    private KafkaTemplate<Object, Object> kafkaTemplate;

    @Autowired
    private ProcessedEventStore processedEventStore;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
        registry.add("app.kafka.topics.user-registered", () -> USER_REGISTERED_TOPIC);
        registry.add("app.kafka.topics.user-registered-dlt", () -> USER_REGISTERED_DLT_TOPIC);
    }

    @BeforeEach
    void setUp() {
        processedEventStore.clear();
    }

    @Test
    void shouldProcessSameEventOnlyOnce() throws Exception {
        String eventId = UUID.randomUUID().toString();
        UserRegisteredEvent event = buildEvent(eventId);

        kafkaTemplate.send(USER_REGISTERED_TOPIC, eventId, event).get();
        kafkaTemplate.send(USER_REGISTERED_TOPIC, eventId, event).get();

        waitUntilProcessedOnce();
        assertThat(processedEventStore.count()).isEqualTo(EXPECTED_SINGLE_PROCESS_COUNT);
    }

    @Test
    void shouldMoveInvalidEventToDeadLetterTopic() throws Exception {
        UserRegisteredEvent invalidEvent = buildEvent(null);
        invalidEvent.setUsername("invalid-user");

        kafkaTemplate.send(USER_REGISTERED_TOPIC, UUID.randomUUID().toString(), invalidEvent).get();

        try (KafkaConsumer<String, String> consumer = createDltConsumer()) {
            ConsumerRecord<String, String> dltRecord = pollForRecord(consumer);
            assertThat(dltRecord).isNotNull();
            assertThat(dltRecord.value()).contains("invalid-user");
        }
    }

    private UserRegisteredEvent buildEvent(String eventId) {
        UserRegisteredEvent event = new UserRegisteredEvent();
        event.setEventId(eventId);
        event.setEventType("USER_REGISTERED");
        event.setOccurredAt(Instant.now());
        event.setUserId(101);
        event.setUsername("sample-user");
        event.setEmail("sample-user@test.com");
        return event;
    }

    private KafkaConsumer<String, String> createDltConsumer() {
        Map<String, Object> props = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers(),
                ConsumerConfig.GROUP_ID_CONFIG, "user-it-" + UUID.randomUUID(),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class
        );
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(java.util.List.of(USER_REGISTERED_DLT_TOPIC));
        return consumer;
    }

    private ConsumerRecord<String, String> pollForRecord(KafkaConsumer<String, String> consumer) {
        long deadline = System.currentTimeMillis() + DLT_POLL_TIMEOUT_MS;
        ConsumerRecord<String, String> firstMatch = null;
        while (System.currentTimeMillis() < deadline) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
            for (ConsumerRecord<String, String> consumedRecord : records) {
                firstMatch = consumedRecord;
                break;
            }
            if (firstMatch != null) {
                break;
            }
        }
        return firstMatch;
    }

    private void waitUntilProcessedOnce() {
        await()
                .atMost(Duration.ofMillis(PROCESS_WAIT_TIMEOUT_MS))
                .pollInterval(Duration.ofMillis(250))
                .untilAsserted(() -> assertThat(processedEventStore.count()).isEqualTo(EXPECTED_SINGLE_PROCESS_COUNT));
    }
}

