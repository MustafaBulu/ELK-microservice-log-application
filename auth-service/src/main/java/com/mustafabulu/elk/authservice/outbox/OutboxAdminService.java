package com.mustafabulu.elk.authservice.outbox;

public interface OutboxAdminService {

    OutboxStatsResponse stats();

    int reprocessFailed(int limit);
}

