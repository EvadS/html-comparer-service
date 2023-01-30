package com.se.sample.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadResponse {
    @Schema(description = "Comparison unique identifier.")
    private Long operationId;

    @Schema(description = "URL to download  the old file version.")
    private String oldFileLink;

    @Schema(description = "URL to download  the new file version.")
    private String newFileLink;
}