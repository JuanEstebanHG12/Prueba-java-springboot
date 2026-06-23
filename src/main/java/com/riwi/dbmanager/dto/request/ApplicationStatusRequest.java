package com.riwi.dbmanager.dto.request;

import com.riwi.dbmanager.model.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public record ApplicationStatusRequest(
        @NotNull(message = "Status is required")
        ApplicationStatus status
) {
}
