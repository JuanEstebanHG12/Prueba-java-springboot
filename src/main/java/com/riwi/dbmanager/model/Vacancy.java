package com.riwi.dbmanager.model;

import com.riwi.dbmanager.model.enums.JobCategory;
import com.riwi.dbmanager.model.enums.JobStatus;
import com.riwi.dbmanager.model.enums.WorkMode;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "vacancies")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Vacancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "description", nullable = false)
    String description;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    JobCategory category;

    @Column(name = "mode", nullable = false)
    @Enumerated(EnumType.STRING)
    WorkMode mode;

    @Column(name = "salary")
    BigDecimal salary;

    @Column(name = "publish_date", nullable = false)
    @Builder.Default
    LocalDate publishDate = LocalDate.now();

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    JobStatus status = JobStatus.OPEN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_id", nullable = false)
    User responsible;
}
