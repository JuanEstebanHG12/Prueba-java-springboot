package com.riwi.dbmanager.repository;

import com.riwi.dbmanager.model.Interview;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, Long> {

    @Override
    @EntityGraph(attributePaths = {"application", "application.candidate",
            "application.vacancy", "application.vacancy.responsible"})
    List<Interview> findAll();

    @EntityGraph(attributePaths = {"application", "application.candidate",
            "application.vacancy", "application.vacancy.responsible"})
    List<Interview> findByApplicationId(Long applicationId);
}
