package com.se.sample.errors.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.CONFLICT)
public class AlreadyExistException extends RuntimeException{
    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    /**
     * Instantiates a new instance of {@link AlreadyExistException}.
     *
     * @param resourceName  created resource
     * @param fieldName     type of the resource which cannot be create
     * @param fieldValue    an identifier of the resource which cannot be created
     */
    public AlreadyExistException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with field value %s: '%s'", resourceName, fieldName, fieldValue));

        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
