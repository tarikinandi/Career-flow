package com.careerflow.backend.mapper;

import com.careerflow.backend.dto.request.ApplicationRequest;
import com.careerflow.backend.dto.response.ApplicationResponse;
import com.careerflow.backend.model.Application;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    @Mapping(target = "interviews", ignore = true)
    Application toEntity(ApplicationRequest applicationRequest);

    ApplicationResponse toResponse(Application entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(ApplicationRequest applicationRequest,@MappingTarget Application entity);
}
