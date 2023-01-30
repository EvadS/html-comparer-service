package com.se.sample.entity;

import com.se.sample.dao.models.audit.DateAudit;
import com.se.sample.model.enums.FileProcessingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comparation")
@Builder(toBuilder = true)
public class FileComparison  extends DateAudit {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String odlFileLocation;
    private String oldFileDownloadUrl;
    private String oldFileWithDiffPath;

    private String newFileLocation;
    private String newFileWithDiffPath;

    private FileProcessingStatus processingStatus;

    @Column(name = "created_date", nullable = false, updatable = false)
    @CreatedDate
    private long createdDate;

    @Column(name = "modified_date")
    @LastModifiedDate
    private long modifiedDate;
}
