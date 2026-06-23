package com.riwi.dbmanager.dto.request;

import com.riwi.dbmanager.model.enums.InterviewStatus;
import jakarta.validation.constraints.NotNull;

public record InterviewStatusRequest(
        @NotNull(message = "Status is required")
        InterviewStatus status
) {
}
