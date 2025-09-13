package com.mohamed.lawyer.logs;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/logs")
@Tag(name = "Logs", description = "Logs API")
public class LogsController {

    private final LogsService logsService;

    @GetMapping
    public ResponseEntity<List<LogsResponse>> getAllLogs() {
        return ResponseEntity.ok(logsService.getAllLogs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LogsResponse> getLogById(@PathVariable Long id) {
        return ResponseEntity.ok(logsService.getLogById(id));
    }

    @GetMapping("/lawsuit/{lawsuitId}")
    public ResponseEntity<List<LogsResponse>> getLogsByLawsuitId(@PathVariable Long lawsuitId) {
        return ResponseEntity.ok(logsService.getLogsByLawsuitId(lawsuitId));
    }

    @PostMapping("/lawsuit/{lawsuitId}")
    public ResponseEntity<Long> createLog(@Valid @RequestBody LogsRequest request, 
                                         @PathVariable Long lawsuitId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(logsService.createLog(request, lawsuitId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLog(@PathVariable Long id, 
                                         @Valid @RequestBody LogsRequest request) {
        logsService.updateLog(id, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLog(@PathVariable Long id) {
        logsService.deleteLog(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
