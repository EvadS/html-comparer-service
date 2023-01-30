package com.se.sample.errors.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@Getter
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ComparisonNotFoundException extends RuntimeException {
    private Long comparisonId;

    /**
     * Instantiates a new instance of {@link ComparisonNotFoundException}.
     *
     * @param comparisonId comparison  operation unique identifier
     */
    public ComparisonNotFoundException(Long comparisonId) {
        super(String.format("Operation not found by id: '%s'", comparisonId));
        this.comparisonId = comparisonId;
    }
}
