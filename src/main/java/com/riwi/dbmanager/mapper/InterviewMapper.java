package com.riwi.dbmanager.mapper;

import com.riwi.dbmanager.dto.request.InterviewRequest;
import com.riwi.dbmanager.dto.response.InterviewResponse;
import com.riwi.dbmanager.model.Interview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {ApplicationMapper.class}
)
public interface InterviewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "application", ignore = true)
    Interview toInterview(InterviewRequest request);

    InterviewResponse toInterviewResponse(Interview interview);

    List<InterviewResponse> toInterviewResponseList(List<Interview> interviews);
}
