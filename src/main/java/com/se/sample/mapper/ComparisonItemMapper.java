package com.se.sample.mapper;

import com.se.sample.entity.FileComparison;
import com.se.sample.model.response.ComparisonItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface ComparisonItemMapper {

    ComparisonItemMapper INSTANCE = Mappers.getMapper(ComparisonItemMapper.class);

    @Mappings({
            @Mapping(target = "operationId", source = "id"),
            @Mapping(target = "oldFileLink", source = "odlFileLocation"),
            @Mapping(target = "newFileLink", source = "oldFileDownloadUrl"),
            @Mapping(target = "oldDiffLink", source = "oldFileWithDiffPath"),
            @Mapping(target = "newDiffLink", source = "newFileWithDiffPath"),
            @Mapping(target = "comparisonStatus", source = "processingStatus", defaultValue = "default-value")
    })
    ComparisonItemResponse toComparisonItemResponse(FileComparison comparison);
}
