package com.riwi.dbmanager.controller;

import com.riwi.dbmanager.dto.request.InterviewRequest;
import com.riwi.dbmanager.dto.request.InterviewStatusRequest;
import com.riwi.dbmanager.dto.response.InterviewResponse;
import com.riwi.dbmanager.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping
    public ResponseEntity<InterviewResponse> saveInterview(@Valid @RequestBody InterviewRequest request) {
        return ResponseEntity.ok(interviewService.saveInterview(request));
    }

    @GetMapping
    public List<InterviewResponse> getAllInterviews() {
        return interviewService.getAllInterviews();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InterviewResponse> getInterviewById(@PathVariable Long id) {
        return ResponseEntity.ok(interviewService.getInterviewById(id));
    }

    @GetMapping("/application/{applicationId}")
    public List<InterviewResponse> getInterviewsByApplicationId(@PathVariable Long applicationId) {
        return interviewService.getInterviewsByApplicationId(applicationId);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<InterviewResponse> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody InterviewStatusRequest request
    ) {
        return ResponseEntity.ok(interviewService.changeStatus(id, request));
    }
}
