package com.se.sample.services;

import com.se.sample.config.ComparisonThread;
import com.se.sample.dao.models.repository.ComparisonRepository;
import com.se.sample.entity.FileComparison;
import com.se.sample.errors.exception.ComparisonNotFoundException;
import com.se.sample.errors.exception.ResourceNotFoundException;
import com.se.sample.mapper.ComparisonItemMapper;
import com.se.sample.mapper.ComparisonMapper;
import com.se.sample.model.enums.FileProcessingStatus;
import com.se.sample.model.payload.DifferenceFiles;
import com.se.sample.model.response.ComparisonItemResponse;
import com.se.sample.model.response.ComparisonStatusResponse;
import com.se.sample.model.response.UploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

import static com.se.sample.config.ProjectConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComparisonService {
    private final TaskExecutor taskExecutor;
    private final ApplicationContext applicationContext;
    private final ComparisonRepository comparisonRepository;
    private final FileStorageService fileStorageService;
    private final HtmlProcessingService htmlProcessingService;

    public ComparisonStatusResponse getComparisonStatus(Long operationId) {
        final FileComparison fileComparison = comparisonRepository.findById(operationId)
                .orElseThrow(() -> new ResourceNotFoundException("Comparison", "id", operationId));

        return ComparisonMapper.INSTANCE.toComparisonStatusResponse(fileComparison);
    }

    public Resource getNewFileAsResource(Long operationId) {
        log.debug("get file with new changes for: {}", operationId);
        FileComparison fileComparison = comparisonRepository.findById(operationId)
                .orElseThrow(() -> new ComparisonNotFoundException(operationId));

        final String newFileWithDiffPath = fileComparison.getNewFileWithDiffPath();
        return fileStorageService.loadFileAsResource(newFileWithDiffPath);
    }

    public Resource getOldFileAsResource(Long operationId) {
        log.debug("get file with old changes for: {}", operationId);
        FileComparison fileComparison = comparisonRepository.findById(operationId)
                .orElseThrow(() -> new ComparisonNotFoundException(operationId));

        final String filePath = fileComparison.getOldFileWithDiffPath();
        return fileStorageService.loadFileAsResource(filePath);
    }

    public UploadResponse createComparison(MultipartFile oldVersion, MultipartFile newVersion) {
        // Store file
        String fileNameNew = fileStorageService.storeFile(oldVersion);

        String oldFileDownloadLink = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(PATH_PREFIX + COMPARISON_API_PATH + HTML_DOWNLOAD_API_PATH + URL_SEPARATOR)
                .path(fileNameNew)
                .toUriString();

        String fileNameOld = fileStorageService.storeFile(newVersion);
        String newFileDownloadLink = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(PATH_PREFIX + COMPARISON_API_PATH + HTML_DOWNLOAD_API_PATH + URL_SEPARATOR)
                .path(fileNameOld)
                .toUriString();

        log.info("File uploaded. Stored path: {}", oldFileDownloadLink);

        FileComparison fileComparison = FileComparison.builder()
                .odlFileLocation(fileNameNew)
                .processingStatus(FileProcessingStatus.CREATED)
                .oldFileDownloadUrl(oldFileDownloadLink)
                .newFileLocation(newFileDownloadLink)
                .build();

        comparisonRepository.save(fileComparison);

        ComparisonThread myThread = new ComparisonThread(
                fileStorageService.getFilePatchByName(fileNameOld),
                fileStorageService.getFilePatchByName(fileNameNew),
                htmlProcessingService,
                this,
                fileComparison.getId());

        taskExecutor.execute(myThread);


        return new UploadResponse(fileComparison.getId(), oldFileDownloadLink, newFileDownloadLink);
    }

    public void changeStatus(Long operationId, FileProcessingStatus processingStatus) {
        log.debug("change status for id:{}, to: {}", operationId, processingStatus);
        final FileComparison fileComparison = comparisonRepository.findById(operationId)
                .orElseThrow(() -> new ResourceNotFoundException("Comparison", "id", operationId));

        fileComparison.setProcessingStatus(processingStatus);
    }


    public void updateDiffFiles(Long operationId, DifferenceFiles differenceFiles) {

        log.debug("update files with differences");
        final FileComparison fileComparison = comparisonRepository.findById(operationId)
                .orElseThrow(() -> new ResourceNotFoundException("Comparison", "id", operationId));

        fileComparison.setOldFileWithDiffPath(differenceFiles.getDiffLeft());
        fileComparison.setNewFileWithDiffPath(differenceFiles.getDiffRight());

        fileComparison.setProcessingStatus(FileProcessingStatus.SUCCESS_FINISHED);

        comparisonRepository.save(fileComparison);
    }

    public Resource getFileByName(String fileName) {
        return fileStorageService.loadFileAsResource(fileName);
    }

    public List<ComparisonItemResponse> getAll() {
        return comparisonRepository.findAll().stream()
                .map(i -> ComparisonItemMapper.INSTANCE.toComparisonItemResponse(i))
                .collect(Collectors.toList());
    }
}
