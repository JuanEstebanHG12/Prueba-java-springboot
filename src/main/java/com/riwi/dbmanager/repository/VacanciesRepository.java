package com.riwi.dbmanager.repository;

import com.riwi.dbmanager.model.Vacancy;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VacanciesRepository extends JpaRepository<Vacancy, Long> {

    // Solves the N+1 problem when fetching entities with a related entity lazily loaded.
    // This method indicates to the persistence provider to load the related entity eagerly.
    @Override
    @EntityGraph(attributePaths = "responsible")
    List<Vacancy> findAll();
}
