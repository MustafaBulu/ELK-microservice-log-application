package com.mustafabulu.elk.userservice.event;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProcessedEventStore {

    private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();

    public boolean markAsProcessedIfNew(String eventId) {
        if (eventId == null || eventId.isBlank()) {
            return false;
        }
        return processedEventIds.add(eventId);
    }

    public int count() {
        return processedEventIds.size();
    }

    public void clear() {
        processedEventIds.clear();
    }
}

