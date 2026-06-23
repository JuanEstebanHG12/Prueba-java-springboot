package com.riwi.dbmanager.service;

import com.riwi.dbmanager.dto.request.InterviewRequest;
import com.riwi.dbmanager.dto.request.InterviewStatusRequest;
import com.riwi.dbmanager.dto.response.InterviewResponse;
import com.riwi.dbmanager.exception.ApplicationNotFoundException;
import com.riwi.dbmanager.exception.BusinessException;
import com.riwi.dbmanager.exception.InterviewNotFoundException;
import com.riwi.dbmanager.mapper.InterviewMapper;
import com.riwi.dbmanager.model.Application;
import com.riwi.dbmanager.model.Interview;
import com.riwi.dbmanager.model.enums.ApplicationStatus;
import com.riwi.dbmanager.model.enums.InterviewStatus;
import com.riwi.dbmanager.repository.ApplicationRepository;
import com.riwi.dbmanager.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterviewService {

    private static final Set<ApplicationStatus> TERMINAL_STATUSES =
            Set.of(ApplicationStatus.ACCEPTED, ApplicationStatus.REJECTED, ApplicationStatus.WITHDRAWN);

    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewMapper interviewMapper;

    @Transactional
    public InterviewResponse saveInterview(InterviewRequest request) {
        Application application = applicationRepository.findById(request.applicationId())
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));

        if (request.scheduledDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Interview must be scheduled in the future");
        }

        if (TERMINAL_STATUSES.contains(application.getStatus())) {
            throw new BusinessException("Cannot schedule an interview for an application in status: " + application.getStatus());
        }

        Interview interview = interviewMapper.toInterview(request);
        interview.setApplication(application);

        application.setStatus(ApplicationStatus.INTERVIEW_SCHEDULED);
        applicationRepository.save(application);

        return interviewMapper.toInterviewResponse(interviewRepository.save(interview));
    }

    public List<InterviewResponse> getAllInterviews() {
        return interviewMapper.toInterviewResponseList(interviewRepository.findAll());
    }

    public InterviewResponse getInterviewById(Long id) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new InterviewNotFoundException("Interview not found"));
        return interviewMapper.toInterviewResponse(interview);
    }

    public List<InterviewResponse> getInterviewsByApplicationId(Long applicationId) {
        applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found"));
        return interviewMapper.toInterviewResponseList(interviewRepository.findByApplicationId(applicationId));
    }

    @Transactional
    public InterviewResponse changeStatus(Long id, InterviewStatusRequest request) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new InterviewNotFoundException("Interview not found"));

        if ((request.status() == InterviewStatus.CANCELLED || request.status() == InterviewStatus.RESCHEDULED)
                && interview.getStatus() != InterviewStatus.SCHEDULED) {
            throw new BusinessException("Can only cancel or reschedule a SCHEDULED interview");
        }

        if (request.status() == InterviewStatus.CANCELLED) {
            interview.getApplication().setStatus(ApplicationStatus.UNDER_REVIEW);
            applicationRepository.save(interview.getApplication());
        }

        interview.setStatus(request.status());
        return interviewMapper.toInterviewResponse(interviewRepository.save(interview));
    }
}
