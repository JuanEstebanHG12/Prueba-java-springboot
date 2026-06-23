package com.riwi.dbmanager.controller;

import com.riwi.dbmanager.dto.response.VacancyResponse;
import com.riwi.dbmanager.service.VacanciesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Separate controller so we can expose /api/vacancies without the /api/admin/ prefix.
// Spring Security's .anyRequest().permitAll() covers this route for all authenticated users.
@RestController
@RequestMapping("/api/vacancies")
@RequiredArgsConstructor
public class PublicVacancyController {

    private final VacanciesService vacanciesService;

    @GetMapping
    public List<VacancyResponse> getPublicVacancies() {
        return vacanciesService.getAllVacancies();
    }
}
