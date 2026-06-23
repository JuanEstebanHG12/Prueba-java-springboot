package com.riwi.dbmanager.repository;

import com.riwi.dbmanager.model.Application;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    @Override
    @EntityGraph(attributePaths = {"candidate", "vacancy", "vacancy.responsible"})
    List<Application> findAll();

    @EntityGraph(attributePaths = {"candidate", "vacancy", "vacancy.responsible"})
    List<Application> findByCandidateId(Long candidateId);

    boolean existsByCandidateIdAndVacancyId(Long candidateId, Long vacancyId);
}
