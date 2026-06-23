package com.riwi.dbmanager.dto.response;

import com.riwi.dbmanager.model.User;
import com.riwi.dbmanager.model.enums.JobCategory;
import com.riwi.dbmanager.model.enums.JobStatus;
import com.riwi.dbmanager.model.enums.WorkMode;
import jakarta.persistence.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VacancyResponse(
        Long id,

        String title,

        String description,

        JobCategory category,

        WorkMode mode,

        BigDecimal salary,

        LocalDate publishDate,

        JobStatus status,

        ResponseUserDTO responsible
){
        }
