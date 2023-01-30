package com.se.sample.errors.models;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Error Detail response")
@NoArgsConstructor
public class ErrorDetail {

    @Schema(description = "Error Detail Title", required = true, example = "Resource not found")
    private String message;

    @Schema(description = "Error Detail Description", required = true, example = "Requested resource cannot be found")
    private String detail;

    @Schema(description = "HTTP Status Code", example = "404")
    private Integer status;

    @Schema(description = "List of additional errors")
    private List<ApiValidationError> errors;

    private String stackTrace;

    public ErrorDetail(final String title, final String detail) {
        this.message = title;
        this.detail = detail;
    }
}
