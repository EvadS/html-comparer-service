package com.se.sample.model.enums;

import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum FileProcessingStatus {
    CREATED(1),
    IN_PROGRESS(2),
    SUCCESS_FINISHED(3),
    FAIL_FINISHED(4);

    private final int id;
    FileProcessingStatus(int id) {
        this.id = id;
    }


    public static FileProcessingStatus of(int id) {
        return Stream.of(FileProcessingStatus.values())
                .filter(p -> p.getId()==id)
                .findFirst().orElseThrow(() -> new IllegalStateException(String.format("Unsupported type %s.", id)));
    }
}
