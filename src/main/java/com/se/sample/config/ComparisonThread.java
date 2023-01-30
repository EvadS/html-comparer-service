package com.se.sample.config;

import com.se.sample.model.payload.DifferenceFiles;
import com.se.sample.model.enums.FileProcessingStatus;
import com.se.sample.services.ComparisonService;
import com.se.sample.services.HtmlProcessingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;

@Slf4j
@AllArgsConstructor
@Component
@Scope("prototype")
public class ComparisonThread implements Runnable {
    private final Path filePatchByOldFileName;
    private final Path filePatchByNewFileNameName;

    private final HtmlProcessingService htmlProcessingService;
    private final ComparisonService comparisonService;
    private final Long operationId;

    @Override
    public void run() {

        try {
            final DifferenceFiles differenceFiles = htmlProcessingService.process(filePatchByOldFileName, filePatchByNewFileNameName);
            comparisonService.updateDiffFiles(operationId,differenceFiles);

        } catch (IOException e) {
            log.error("Error html file processing. {}", e.getMessage());
            comparisonService.changeStatus(operationId, FileProcessingStatus.FAIL_FINISHED);
        }
    }
}
