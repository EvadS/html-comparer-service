package com.se.sample.controller.api;

import com.se.sample.config.GeneralConstants;
import com.se.sample.errors.models.ErrorDetail;
import com.se.sample.model.response.ComparisonItemResponse;
import com.se.sample.model.response.ComparisonStatusResponse;
import com.se.sample.model.response.UploadResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.se.sample.config.ProjectConstants.*;

public interface FileApi {

    @Operation(
            summary = "Upload files ",
            description = "Upload 2 file to check differences between",
            method = "POST",
            responses = {

                    @ApiResponse(
                            responseCode = "200",
                            description = GeneralConstants.HTTP_OK_SUCCESS),
                    @ApiResponse(
                            responseCode = "400",
                            description = GeneralConstants.HTTP_BAD_REQUEST,
                            content = @Content(schema = @Schema(implementation = ErrorDetail.class))),
                    @ApiResponse(
                            responseCode = "500",
                            description = GeneralConstants.HTTP_INTERNAL_ERROR,
                            content = @Content(schema = @Schema(implementation = ErrorDetail.class)))

        })
    @PostMapping(UPLOAD_API_PATH)
    ResponseEntity<UploadResponse> upload(@RequestParam("old") MultipartFile oldVersion,
                                          @RequestParam("new") MultipartFile newVersion);

    @Operation(
            summary = "Get status by id",
            description = "Allow to get comparison status Item by Id",
            method = "GET",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved comparison item",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ComparisonStatusResponse.class))),
                    @ApiResponse(responseCode = "404",
                            description = GeneralConstants.NOT_FOUND,
                            content = @Content(schema = @Schema(implementation = ErrorDetail.class))),
                    @ApiResponse(responseCode = "500",
                            description = GeneralConstants.HTTP_INTERNAL_ERROR,
                            content = @Content(schema = @Schema(implementation = ErrorDetail.class))),
            })
    @GetMapping(OPERATION_STATUS_API + URL_SEPARATOR + "{" + OPERATION_ID_PARAM + "}")
    ResponseEntity<ComparisonStatusResponse> getComparisonStatus(@PathVariable(OPERATION_ID_PARAM) Long operationId);

    @Operation(
            summary = "Download file by name",
            description = "Allow to download  file with by name",
            method = "GET",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archive with old value html file",
                            content = @Content(
                                    mediaType = "application/octet-stream")),

                    @ApiResponse(responseCode = "500",
                            description = GeneralConstants.HTTP_INTERNAL_ERROR,
                            content = @Content(schema = @Schema(implementation = ErrorDetail.class))),
            })
    @GetMapping(HTML_DOWNLOAD_API_PATH + HTML_DOWNLOAD_FILE_BY_NAME_API_PATH)
    ResponseEntity<Resource> downloadFileByName(
            @Parameter(description = "File name")
            @PathVariable("file-name") String fileName, HttpServletRequest request);

    @Operation(
            summary = "Download new file by comparison id ",
            description = "Allow to get file with new value ",
            method = "GET",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archive with old value html file",
                            content = @Content(
                                    mediaType = "application/octet-stream")),

                    @ApiResponse(responseCode = "500",
                            description = GeneralConstants.HTTP_INTERNAL_ERROR,
                            content = @Content(schema = @Schema(implementation = ErrorDetail.class))),
            })
    @GetMapping(HTML_DOWNLOAD_API_PATH + "/{operation-id}/new")
    ResponseEntity<Resource> downloadOldFile(@PathVariable("operation-id")
                                             @Parameter(description = "Comparison unique identifier")
                                                     Long operationId, HttpServletRequest request);

    @Operation(
            summary = "Download old file by comparison id ",
            description = "Allow to get file with old value ",
            method = "GET",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archive with old value html file",
                            content = @Content(
                                    mediaType = "application/octet-stream")),

                    @ApiResponse(responseCode = "500",
                            description = GeneralConstants.HTTP_INTERNAL_ERROR,
                            content = @Content(schema = @Schema(implementation = ErrorDetail.class))),
            })
    @GetMapping(HTML_DOWNLOAD_API_PATH + HTML_DOWNLOAD_OLD_API_PATH)
    ResponseEntity<Resource> downloadFile(@PathVariable("operation-id")
                                          @Parameter(description = "Comparison unique identifier")
                                                  Long operationId, HttpServletRequest request);

    @Operation(
            summary = "Get all comparison Id",
            description = "Allow to get all comparison",
            method = "GET",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved comparison  data",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ComparisonItemResponse.class))),

                    @ApiResponse(responseCode = "500",
                            description = GeneralConstants.HTTP_INTERNAL_ERROR,
                            content = @Content(schema = @Schema(implementation = ErrorDetail.class))),
            })
    @GetMapping("/all")
    List<ComparisonItemResponse> getAll();
}
