package com.mustafabulu.elk.authservice.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxAdminServiceImpl implements OutboxAdminService {

    private final OutboxEventRepository outboxEventRepository;

    @Override
    @Transactional(readOnly = true)
    public OutboxStatsResponse stats() {
        return OutboxStatsResponse.builder()
                .newCount(outboxEventRepository.countByStatus(OutboxStatus.NEW))
                .publishedCount(outboxEventRepository.countByStatus(OutboxStatus.PUBLISHED))
                .failedCount(outboxEventRepository.countByStatus(OutboxStatus.FAILED))
                .build();
    }

    @Override
    @Transactional
    public int reprocessFailed(int limit) {
        if (limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("limit must be between 1 and 100");
        }

        List<OutboxEvent> failedEvents = outboxEventRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.FAILED);
        int processCount = Math.min(limit, failedEvents.size());

        for (int i = 0; i < processCount; i++) {
            OutboxEvent outboxEvent = failedEvents.get(i);
            outboxEvent.setStatus(OutboxStatus.NEW);
            outboxEvent.setRetryCount(0);
            outboxEvent.setPublishedAt(null);
        }

        outboxEventRepository.saveAll(failedEvents.subList(0, processCount));
        return processCount;
    }
}

