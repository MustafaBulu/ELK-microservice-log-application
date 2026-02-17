package com.mustafabulu.elk.authservice.controller.docs;

import com.mustafabulu.elk.authservice.outbox.OutboxReprocessResponse;
import com.mustafabulu.elk.authservice.outbox.OutboxStatsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Outbox Admin API", description = "Outbox monitoring and recovery operations")
@SuppressWarnings("unused")
public interface OutboxAdminApiDoc {

    @Operation(summary = "Get outbox counters", description = "Returns counts of NEW, PUBLISHED and FAILED outbox rows.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Outbox statistics", content = @Content(schema = @Schema(implementation = OutboxStatsResponse.class)))
    })
    ResponseEntity<OutboxStatsResponse> stats();

    @Operation(summary = "Move failed events back to NEW", description = "Moves up to `limit` FAILED outbox records back to NEW state for republishing.")
    @Parameter(name = "limit", description = "Maximum failed rows to reprocess", example = "100")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reprocess result", content = @Content(schema = @Schema(implementation = OutboxReprocessResponse.class)))
    })
    ResponseEntity<OutboxReprocessResponse> reprocessFailed(int limit);
}

