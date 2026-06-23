package com.riwi.dbmanager.service;

import com.riwi.dbmanager.dto.request.ApplicationRequest;
import com.riwi.dbmanager.dto.request.ApplicationStatusRequest;
import com.riwi.dbmanager.dto.response.ApplicationResponse;
import com.riwi.dbmanager.exception.ApplicationNotFoundException;
import com.riwi.dbmanager.exception.BusinessException;
import com.riwi.dbmanager.exception.UserNotFoundException;
import com.riwi.dbmanager.exception.VacancyNotFoundException;
import com.riwi.dbmanager.mapper.ApplicationMapper;
import com.riwi.dbmanager.model.Application;
import com.riwi.dbmanager.model.User;
import com.riwi.dbmanager.model.Vacancy;
import com.riwi.dbmanager.model.enums.JobStatus;
import com.riwi.dbmanager.model.enums.Role;
import com.riwi.dbmanager.repository.ApplicationRepository;
import com.riwi.dbmanager.repository.UserRepository;
import com.riwi.dbmanager.repository.VacanciesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final VacanciesRepository vacanciesRepository;
    private final ApplicationMapper applicationMapper;

    @Transactional
    public ApplicationResponse saveApplication(ApplicationRequest request) {
        User candidate = userRepository.findById(request.candidateId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (candidate.getRole() != Role.CANDIDATE) {
            throw new BusinessException("Only candidates can apply to vacancies");
        }

        Vacancy vacancy = vacanciesRepository.findById(request.vacancyId())
                .orElseThrow(() -> new VacancyNotFoundException("Vacancy not found"));

        if (vacancy.getStatus() != JobStatus.OPEN) {
            throw new BusinessException("Cannot apply to a vacancy that is not open");
        }

        if (applicationRepository.existsByCandidateIdAndVacancyId(request.candidateId(), request.vacancyId())) {
            throw new BusinessException("Candidate has already applied to this vacancy");
        }

        Application application = applicationMapper.toApplication(request);
        application.setCandidate(candidate);
        application.setVacancy(vacancy);

        return applicationMapper.toApplicationResponse(applicationRepository.save(application));
    }

    public List<ApplicationResponse> getAllApplications() {
        return applicationMapper.toApplicationResponseList(applicationRepository.findAll());
    }

    public List<ApplicationResponse> getApplicationsByCandidateId(Long candidateId) {
        userRepository.findById(candidateId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return applicationMapper.toApplicationResponseList(applicationRepository.findByCandidateId(candidateId));
    }

    public ApplicationResponse getApplicationById(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));
        return applicationMapper.toApplicationResponse(application);
    }

    @Transactional
    public ApplicationResponse changeStatus(Long id, ApplicationStatusRequest request) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));
        application.setStatus(request.status());
        return applicationMapper.toApplicationResponse(applicationRepository.save(application));
    }
}
