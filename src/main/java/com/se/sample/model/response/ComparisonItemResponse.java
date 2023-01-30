package com.se.sample.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ComparisonItemResponse {
    @Schema(description = "unique identifier.")
    private Long operationId;

    @Schema(description = "URL to download  the old file version.")
    private String oldFileLink;

    @Schema(description = "URL to download the new file version.")
    private String newFileLink;

    @Schema(description = "URL to download the old file with changes.")
    private String oldDiffLink;

    @Schema(description = "URL to download the new file with changes.")
    private String newDiffLink;

    @Schema(description = "Actual comparison status.")
    private String comparisonStatus;
}
