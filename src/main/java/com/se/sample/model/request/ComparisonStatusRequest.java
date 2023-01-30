package com.se.sample.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Provide model to check comparison status ")
public class ComparisonStatusRequest {

    @Schema(description = "Comparison operation unique identifier",
            example = "1", required = true)
    private Long operationId;
}
