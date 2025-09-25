package com.mohamed.lawyer.lawsuit;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/lawsuits")
@Tag(name = "Lawsuits", description = "Lawsuits API")
public class LawsuitController {

    private final LawsuitService service;

    @GetMapping
    public ResponseEntity<List<LawsuitResponse>> getAllLawsuits(){
        return ResponseEntity.ok(service.getAllLawsuits());
    }

    @PostMapping
    public ResponseEntity<Long> addLawsuit(@Valid @RequestBody LawsuitRequest request,
                                              Authentication connectedUser){
        return ResponseEntity.ok(service.addLawsuit(request, connectedUser));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<LawsuitResponse>> getLawsuitsByStatus(@PathVariable Status status){
        return ResponseEntity.ok(service.getLawsuitByStatus(status));
    }

    @GetMapping("/client-name/{clientName}")
    public ResponseEntity<List<LawsuitResponse>> getLAwsuitByClientName(@PathVariable String clientName){
        return ResponseEntity.ok(service.getLawsuitByClientName(clientName));
    }

    @GetMapping("/case-name/{name}")
    public ResponseEntity<List<LawsuitResponse>> getLawsuitByName(@PathVariable String name){
        return ResponseEntity.ok(service.getLawsuitByName(name));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> markAsDeleted(@PathVariable Long id){
        service.markAsDeleted(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @Valid @RequestParam Status status){
        service.updateStatus(id,status);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}/notes")
    public ResponseEntity<Void> updateNotes(@PathVariable Long id, @Valid @RequestParam String notes){
        service.updateNotes(id,notes);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}/description")
    public ResponseEntity<Void> updateDescription(@PathVariable Long id, @Valid @RequestParam String description){
        service.updateDescription(id,description);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}/client-name")
    public ResponseEntity<Void> updateClientName(@PathVariable Long id, @Valid @RequestParam String clientName){
        service.updateClientName(id,clientName);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}/client-phone")
    public ResponseEntity<Void> updateClientPhone(@PathVariable Long id, @Valid @RequestParam String clientPhone){
        service.updateClientPhone(id,clientPhone);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<LawsuitResponse>> searchLawsuits(@RequestParam String search, Authentication connectedUser){
        return ResponseEntity.ok(service.getFulltextFuzzy(search, connectedUser));
    }

    @PutMapping("/update/{lawsuitId}")
    public ResponseEntity<Void> updateLawsuit(@PathVariable Long lawsuitId, @Valid @RequestBody LawsuitRequest lawsuitRequest){
        service.updateLawsuit(lawsuitId, lawsuitRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
