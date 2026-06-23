package com.riwi.dbmanager.dto.request;

import com.riwi.dbmanager.model.enums.InterviewType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record InterviewRequest(
        @NotNull(message = "Application is required")
        Long applicationId,

        @NotNull(message = "Scheduled date is required")
        LocalDateTime scheduledDate,

        @NotNull(message = "Interview type is required")
        InterviewType type,

        @Size(max = 1000, message = "Notes must not exceed 1000 characters")
        String notes
) {
}
