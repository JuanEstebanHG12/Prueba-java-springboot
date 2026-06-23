package com.riwi.dbmanager.mapper;

import com.riwi.dbmanager.dto.request.VacanciesRequest;
import com.riwi.dbmanager.dto.response.VacancyResponse;
import com.riwi.dbmanager.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface VacanciesMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publishDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "responsible", ignore = true)
    Vacancy toVacancies(VacanciesRequest vacanciesRequest);

    VacancyResponse toVacanciesRequest(Vacancy vacancy);

    List<VacancyResponse> toVacanciesRequest(List<Vacancy> vacancies);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publishDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "responsible", ignore = true)
    void updateVacancyFromRequest(VacanciesRequest vacanciesRequest, @MappingTarget Vacancy vacancy);
}
