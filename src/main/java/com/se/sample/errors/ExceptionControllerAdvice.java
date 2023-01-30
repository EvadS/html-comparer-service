package com.se.sample.errors;


import com.se.sample.errors.exception.AlreadyExistException;
import com.se.sample.errors.exception.ComparisonNotFoundException;
import com.se.sample.errors.exception.DataBaseConstraintException;
import com.se.sample.errors.exception.ResourceNotFoundException;
import com.se.sample.errors.models.ApiValidationError;
import com.se.sample.errors.models.ErrorDetail;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

//
@RestControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {
    public static final String TRACE = "trace";
    public static final String UNKNOWN_ERROR_OCCURRED = "Unknown error occurred";
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    @Value("${reflectoring.trace:false}")
    private final boolean printStackTrace = false;

    /***************************************************************
     * VALIDATION ERRORS block
     ***************************************************************/
    @Override
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {


        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<ApiValidationError> validationErrorList = fieldErrors.stream().map(
                i ->
                        new ApiValidationError(i.getObjectName(),
                                i.getField(),
                                i.getRejectedValue(),
                                i.getDefaultMessage())

        ).collect(Collectors.toList());

        return buildErrorResponse(ex, "Field type mismatch", "Constraint validation",
                HttpStatus.UNPROCESSABLE_ENTITY,
                validationErrorList,
                request);
    }

    /***************************************************************
     *      block NotFound
     ***************************************************************/
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException e, WebRequest request) {

        LOGGER.error("Resource {} ,by: {} value {} cannot be found", e.getResourceName(),
                e.getFieldName(), e.getFieldValue());

        return buildErrorResponse(e, "Resource not found", e.getMessage(), HttpStatus.NOT_FOUND, request);
    }


    @ExceptionHandler(ComparisonNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleResourceNotFoundException(ComparisonNotFoundException e, WebRequest request) {
        return buildErrorResponse(e, "Resource not found", e.getMessage(), HttpStatus.NOT_FOUND, request);
    }




    @ExceptionHandler(DataBaseConstraintException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleResourceNotFoundException(DataBaseConstraintException e, WebRequest request) {

        LOGGER.error("ConstraintViolationException. Reason: {}", e.getMessage()) ;

        return buildErrorResponse(e, "Resource not found", e.getMessage(), HttpStatus.BAD_REQUEST, request);
    }



    /***************************************************************
     *      Http Media Type Not Acceptable
     ***************************************************************/
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        LOGGER.error("incorrect media type: {} ", ex.getMessage());

        return buildErrorResponse(ex, "not valid arguments",
                "Input Request Message cannot be processed", HttpStatus.NOT_ACCEPTABLE, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        LOGGER.error("MissingPathVariable: {} ", ex.getMessage());

        return buildErrorResponse(ex, "incorrect request",
                "Input Request Message cannot be processed", HttpStatus.BAD_REQUEST, request);

    }
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        LOGGER.error("Method not allowed : {} ", ex.getMessage());

        return buildErrorResponse(ex, "incorrect request",
                ex.getMessage(), HttpStatus.METHOD_NOT_ALLOWED, request);
    }

    /****************************************************************
     *      Block 409 Conflict
     ***************************************************************/
    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<Object> handleAlreadyExistException(Exception ex, WebRequest request) {

        LOGGER.error(ex.getMessage());

        return buildErrorResponse(ex, "Resource already exists",
                ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    /****************************************************************
     *      Block 415  HttpMediaTypeNotSupportedException
     ****************************************************************/
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        LOGGER.error(ex.getMessage());

        return buildErrorResponse(ex, "Invalid request",
                "Input Request Message cannot be processed", HttpStatus.CONFLICT, request);
    }

    /***************************************************************
     *      Block 422  HttpMediaTypeNotSupportedException
     ***************************************************************/
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        LOGGER.error("Cannot de-serialize message in request body: {}", ex.getMessage());

        return buildErrorResponse(ex, "Invalid request",
                "Input Request Message cannot be processed", HttpStatus.CONFLICT, request);
    }

    /***************************************************************
     * block 500
     ***************************************************************/
    @ExceptionHandler(value = {IllegalArgumentException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final ResponseEntity<Object> handleIllegalArgument(Exception ex, WebRequest request) {
        LOGGER.error("An unexpected error occurred", ex);

        String title = "This should be application specific";
        String detail = "An unexpected error has occurred";


        return buildErrorResponse(ex, title, detail, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /***************************************************************
     * buildErrorResponse Block
     ***************************************************************/

    private ResponseEntity<Object> buildErrorResponse(Exception exception, String title, String detail,
                                                      HttpStatus httpStatus, WebRequest request) {
        return buildErrorResponse(exception, title, detail, httpStatus, null, request);
    }

    private ResponseEntity<Object> buildErrorResponse(Exception exception, String title, HttpStatus httpStatus,
                                                      WebRequest request) {
        return buildErrorResponse(exception, title, exception.getMessage(), httpStatus, Collections.EMPTY_LIST, request);
    }

    private ResponseEntity<Object> buildErrorResponse(Exception exception, String title,
                                                      String detail, HttpStatus httpStatus,
                                                      List<ApiValidationError> errors,
                                                      WebRequest request) {
        ErrorDetail errorDetail = new ErrorDetail();

        errorDetail.setMessage(title);
        errorDetail.setDetail(detail);
        errorDetail.setStatus(httpStatus.value());
        errorDetail.setErrors(errors);

        if (printStackTrace && request != null && isTraceOn(request)) {
            errorDetail.setStackTrace(ExceptionUtils.getStackTrace(exception));
        }
        return ResponseEntity.status(httpStatus).body(errorDetail);
    }

    private boolean isTraceOn(WebRequest request) {
        String[] value = request.getParameterValues(TRACE);
        return Objects.nonNull(value)
                && value.length > 0
                && value[0].contentEquals("true");
    }
}
