package com.se.sample.mapper;

import com.se.sample.model.response.UploadResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UploadResponseMapper {

    UploadResponseMapper INSTANCE = Mappers.getMapper(UploadResponseMapper.class);

    @Mappings({
            @Mapping( target = "operationId", source = "operationId"),
            @Mapping(target = "newFileLink", source = "newLink"),
            @Mapping(target = "oldFileLink", source = "oldLink")
    })
    UploadResponse toUploadResponse(Long operationId, String oldLink, String newLink);
}
