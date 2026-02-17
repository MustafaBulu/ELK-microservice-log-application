package com.mustafabulu.elk.authservice.outbox;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@Schema(description = "Outbox failed-event reprocess result")
public class OutboxReprocessResponse {

    @Schema(description = "Number of FAILED events moved back to NEW", example = "5")
    int movedToNew;
}

