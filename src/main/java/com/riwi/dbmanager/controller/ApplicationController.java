package com.riwi.dbmanager.controller;

import com.riwi.dbmanager.dto.request.ApplicationRequest;
import com.riwi.dbmanager.dto.request.ApplicationStatusRequest;
import com.riwi.dbmanager.dto.response.ApplicationResponse;
import com.riwi.dbmanager.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/api/applications")
    public ResponseEntity<ApplicationResponse> saveApplication(@Valid @RequestBody ApplicationRequest request) {
        return ResponseEntity.ok(applicationService.saveApplication(request));
    }

    @GetMapping("/api/admin/applications")
    public List<ApplicationResponse> getAllApplications() {
        return applicationService.getAllApplications();
    }

    @GetMapping("/api/applications/{id}")
    public ResponseEntity<ApplicationResponse> getApplicationById(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getApplicationById(id));
    }

    @GetMapping("/api/applications/candidate/{candidateId}")
    public List<ApplicationResponse> getApplicationsByCandidateId(@PathVariable Long candidateId) {
        return applicationService.getApplicationsByCandidateId(candidateId);
    }

    @PatchMapping("/api/admin/applications/{id}/status")
    public ResponseEntity<ApplicationResponse> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationStatusRequest request
    ) {
        return ResponseEntity.ok(applicationService.changeStatus(id, request));
    }
}
