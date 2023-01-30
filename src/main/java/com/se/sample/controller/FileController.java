package com.se.sample.controller;

import com.se.sample.config.ProjectConstants;
import com.se.sample.controller.api.FileApi;
import com.se.sample.model.response.ComparisonItemResponse;
import com.se.sample.model.response.ComparisonStatusResponse;
import com.se.sample.model.response.UploadResponse;
import com.se.sample.services.ComparisonService;
import com.se.sample.services.FileStorageService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

import static com.se.sample.config.ProjectConstants.*;

@RestController
@RequestMapping(ProjectConstants.COMPARISON_API_PATH)
@RequiredArgsConstructor
public class FileController implements FileApi {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    private final ComparisonService comparisonService;
    private final FileStorageService fileStorageService;


    @PostMapping(UPLOAD_API_PATH)
    public ResponseEntity<UploadResponse> upload(@RequestParam("old") MultipartFile oldVersion,
                                                 @RequestParam("new") MultipartFile newVersion) {
        UploadResponse uploadResponse = comparisonService.createComparison(oldVersion, newVersion);
        return ResponseEntity.ok(uploadResponse);
    }


    @GetMapping(OPERATION_STATUS_API + URL_SEPARATOR + "{" + OPERATION_ID_PARAM + "}")
    public ResponseEntity<ComparisonStatusResponse> getComparisonStatus(@PathVariable(OPERATION_ID_PARAM) Long operationId) {
        ComparisonStatusResponse comparisonStatusResponse = comparisonService.getComparisonStatus(operationId);
        return ResponseEntity.ok(comparisonStatusResponse);
    }

    @GetMapping(HTML_DOWNLOAD_API_PATH + HTML_DOWNLOAD_FILE_BY_NAME_API_PATH)
    public ResponseEntity<Resource> downloadFileByName(
            @Parameter(description = "File name")
            @PathVariable("file-name") String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = comparisonService.getFileByName(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }


    @GetMapping(HTML_DOWNLOAD_API_PATH + "/{operation-id}/new")
    public ResponseEntity<Resource> downloadOldFile(@PathVariable("operation-id")
                                                    @Parameter(description = "Comparison unique identifier")
                                                            Long operationId, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = comparisonService.getNewFileAsResource(operationId);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }


    @GetMapping(HTML_DOWNLOAD_API_PATH + HTML_DOWNLOAD_OLD_API_PATH)
    public ResponseEntity<Resource> downloadFile(@PathVariable("operation-id")
                                                 @Parameter(description = "Comparison unique identifier")
                                                         Long operationId, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = comparisonService.getOldFileAsResource(operationId);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }


    @GetMapping("/all")
    public List<ComparisonItemResponse> getAll() {
        return comparisonService.getAll();
    }
}
