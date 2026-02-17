package com.mustafabulu.elk.authservice.controller;

import com.mustafabulu.elk.authservice.controller.docs.OutboxAdminApiDoc;
import com.mustafabulu.elk.authservice.outbox.OutboxAdminService;
import com.mustafabulu.elk.authservice.outbox.OutboxReprocessResponse;
import com.mustafabulu.elk.authservice.outbox.OutboxStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/outbox")
@RequiredArgsConstructor
public class OutboxAdminController implements OutboxAdminApiDoc {

    private final OutboxAdminService outboxAdminService;

    @Override
    @GetMapping("/stats")
    public ResponseEntity<OutboxStatsResponse> stats() {
        return ResponseEntity.ok(outboxAdminService.stats());
    }

    @Override
    @PostMapping("/reprocess-failed")
    public ResponseEntity<OutboxReprocessResponse> reprocessFailed(@RequestParam(defaultValue = "100") int limit) {
        int movedCount = outboxAdminService.reprocessFailed(limit);
        return ResponseEntity.ok(OutboxReprocessResponse.builder().movedToNew(movedCount).build());
    }
}

