package ru.javawebinar.topjava.web;

import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import ru.javawebinar.topjava.util.ValidationUtil;
import ru.javawebinar.topjava.util.exception.ErrorInfo;
import ru.javawebinar.topjava.util.exception.ErrorType;
import ru.javawebinar.topjava.util.exception.IllegalRequestDataException;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import java.util.Map;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.util.ValidationUtil.parseErrors;
import static ru.javawebinar.topjava.util.exception.ErrorType.*;

@RestControllerAdvice(annotations = RestController.class)
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
public class ExceptionInfoHandler {
    private static final Logger log = LoggerFactory.getLogger(ExceptionInfoHandler.class);
    private static final Map<String,String> CONSTRAINTS =Map.of("meal_unique_user_datetime_idx","У вас уже есть еда с такой дата/время",
            "users_unique_email_idx","Пользователь с таким Email уже существует");

    //  http://stackoverflow.com/a/22358422/548473
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(NotFoundException.class)
    public ErrorInfo notFoundError(HttpServletRequest req, NotFoundException e) {
        return logAndGetErrorInfo(req, e, false, DATA_NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.CONFLICT)  // 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public static ErrorInfo conflict(HttpServletRequest req, DataIntegrityViolationException e) {
        String errorMessage = ValidationUtil.getRootCause(e).getMessage();
        if(errorMessage!=null){
            errorMessage=errorMessage.toLowerCase();
            for(Map.Entry<String,String> entry:CONSTRAINTS.entrySet()){
                if(errorMessage.contains(entry.getKey())){
                    return logAndGetErrorInfo(req,e,true,VALIDATION_ERROR, entry.getValue());
                }
            }
        }
        return logAndGetErrorInfo(req, e, true, DATA_ERROR);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)  // 422
    @ExceptionHandler({IllegalRequestDataException.class, MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class, BindException.class})
    public ErrorInfo validationError(HttpServletRequest req, Exception e) {
        return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR);
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorInfo internalError(HttpServletRequest req, Exception e) throws Exception {
        Throwable rootCause = ValidationUtil.getRootCause(e);
        if(!rootCause.equals(e)){
            throw (Exception) rootCause;
        }
        return logAndGetErrorInfo(req, (Exception) rootCause, true, APP_ERROR);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException error ) {
        return ResponseEntity.badRequest().body(ValidationUtil.parseErrors(error.getBindingResult()));
    }
    //    https://stackoverflow.com/questions/538870/should-private-helper-methods-be-static-if-they-can-be-static
    private static ErrorInfo logAndGetErrorInfo(HttpServletRequest req, Exception e, boolean logException, ErrorType errorType) {
        Throwable rootCause = ValidationUtil.getRootCause(e);
        String details;
        if(e instanceof BindException){
            BindingResult result =((BindException) e).getBindingResult();
            details= result.getFieldErrors().stream()
                    .map(fe -> String.format("[%s] %s", fe.getField(), fe.getDefaultMessage()))
                    .collect(Collectors.joining("<br>"));
        }
        else{
            details=rootCause.toString();
        }
        if (logException) {
            log.error(errorType + " at request " + req.getRequestURL(), rootCause);
        } else {
            log.warn("{} at request  {}: {}", errorType, req.getRequestURL(), rootCause.toString());
        }
        return new ErrorInfo(req.getRequestURL(), errorType, details);
    }
    private static ErrorInfo logAndGetErrorInfo(HttpServletRequest req, Exception e, boolean logException, ErrorType errorType,String message) {
        Throwable rootCause = ValidationUtil.getRootCause(e);
        if (logException) {
            log.error(errorType + " at request " + req.getRequestURL(), rootCause);
        } else {
            log.warn("{} at request  {}: {}", errorType, req.getRequestURL(), rootCause.toString());
        }
        return new ErrorInfo(req.getRequestURL(), errorType, message);
    }
}