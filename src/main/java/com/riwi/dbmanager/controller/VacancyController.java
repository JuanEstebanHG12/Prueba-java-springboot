package com.riwi.dbmanager.controller;

import com.riwi.dbmanager.dto.request.VacanciesRequest;
import com.riwi.dbmanager.dto.request.VacancyStatusRequest;
import com.riwi.dbmanager.dto.response.VacancyResponse;
import com.riwi.dbmanager.service.VacanciesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/vacancies")
@RequiredArgsConstructor
public class VacancyController {
    private final VacanciesService vacanciesService;

    //@PreAuthorize("hasAnyRole('ADMIN')") only admin can create vacancies
    @PostMapping
    public ResponseEntity<VacancyResponse> saveVacancy(@Valid @RequestBody VacanciesRequest vacanciesRequest) {
        return ResponseEntity.ok(vacanciesService.saveVacancies(vacanciesRequest));
    }

    @GetMapping
    public List<VacancyResponse> getAllVacancies() {
        return vacanciesService.getAllVacancies();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VacancyResponse> getVacancyById(@PathVariable Long id) {
        return ResponseEntity.ok(vacanciesService.getVacancyById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VacancyResponse> updateVacancy(
            @PathVariable Long id,
            @RequestBody VacanciesRequest vacanciesRequest
    ) {
        return ResponseEntity.ok(vacanciesService.updateVacancies(vacanciesRequest, id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<VacancyResponse> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody VacancyStatusRequest request
    ) {
        return ResponseEntity.ok(vacanciesService.changeStatus(id, request));
    }
}
