package com.mustafabulu.elk.eventcontract;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent {

    private String eventId;
    private String eventType;
    private Instant occurredAt;
    private int userId;
    private String username;
    private String email;

    public static UserRegisteredEvent create(int userId, String username, String email) {
        return new UserRegisteredEvent(
                UUID.randomUUID().toString(),
                "USER_REGISTERED",
                Instant.now(),
                userId,
                username,
                email
        );
    }
}

