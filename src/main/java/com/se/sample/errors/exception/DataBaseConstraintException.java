package com.se.sample.errors.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DataBaseConstraintException extends RuntimeException {
    private String resourceName;
    private String identifier;
    private Object relatedResourcesInfo;

    /**
     * Instantiates a new instance of {@link ResourceNotFoundException}.
     *
     * @param resourceName  searched resource
     * @param identifier            unique identifier
     * @param relatedResourcesInfo    an identifier of the resource which cannot be found
     */
    public DataBaseConstraintException(String resourceName, String identifier, String relatedResourcesInfo) {
        super(String.format("Resource %s by id %s used in '%s'", resourceName, identifier, relatedResourcesInfo));
        this.resourceName = resourceName;
        this.identifier = identifier;
        this.relatedResourcesInfo = relatedResourcesInfo;
    }
}
