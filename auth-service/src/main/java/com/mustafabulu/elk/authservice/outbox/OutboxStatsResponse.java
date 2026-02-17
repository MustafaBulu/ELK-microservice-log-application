package com.mustafabulu.elk.authservice.outbox;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@Schema(description = "Outbox event counters by status")
public class OutboxStatsResponse {
    @Schema(description = "Number of NEW outbox events", example = "3")
    long newCount;
    @Schema(description = "Number of published outbox events", example = "120")
    long publishedCount;
    @Schema(description = "Number of failed outbox events", example = "2")
    long failedCount;
}

