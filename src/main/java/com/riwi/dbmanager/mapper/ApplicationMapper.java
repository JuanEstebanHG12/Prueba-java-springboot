package com.riwi.dbmanager.mapper;

import com.riwi.dbmanager.dto.request.ApplicationRequest;
import com.riwi.dbmanager.dto.response.ApplicationResponse;
import com.riwi.dbmanager.model.Application;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {VacanciesMapper.class, UserMapper.class}
)
public interface ApplicationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "applicationDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "candidate", ignore = true)
    @Mapping(target = "vacancy", ignore = true)
    Application toApplication(ApplicationRequest request);

    ApplicationResponse toApplicationResponse(Application application);

    List<ApplicationResponse> toApplicationResponseList(List<Application> applications);
}
