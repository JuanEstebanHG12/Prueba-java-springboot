package com.riwi.dbmanager.dto.response;

import com.riwi.dbmanager.model.enums.InterviewStatus;
import com.riwi.dbmanager.model.enums.InterviewType;

import java.time.LocalDateTime;

public record InterviewResponse(
        Long id,
        LocalDateTime scheduledDate,
        InterviewType type,
        InterviewStatus status,
        String notes,
        ApplicationResponse application
) {
}
