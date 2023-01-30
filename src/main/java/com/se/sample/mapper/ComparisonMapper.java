package com.se.sample.mapper;

import com.se.sample.entity.FileComparison;
import com.se.sample.model.response.ComparisonStatusResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
    public interface ComparisonMapper {

     ComparisonMapper INSTANCE = Mappers.getMapper(ComparisonMapper.class);


    @Mappings({
            @Mapping( target = "status", source = "processingStatus"),
            @Mapping(target = "comparisonStatusRequest.operationId", source = "id")
    })
    ComparisonStatusResponse toComparisonStatusResponse(FileComparison fileComparison);
}
