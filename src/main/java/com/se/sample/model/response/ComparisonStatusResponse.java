package com.se.sample.model.response;

import com.se.sample.model.enums.FileProcessingStatus;
import com.se.sample.model.request.ComparisonStatusRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ComparisonStatusResponse {
    @Schema(description = "Request data to create comparison.")
    private ComparisonStatusRequest comparisonStatusRequest;

    @Schema(description = "unique identifier.", required = true)
    private FileProcessingStatus status;
}
