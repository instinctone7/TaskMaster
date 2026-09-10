package com.projects.task_master.handlers;

import com.projects.task_master.exceptions.NonAuthorized;
import com.projects.task_master.exceptions.PasswordIncorrect;
import com.projects.task_master.exceptions.TaskNotFound;
import com.projects.task_master.exceptions.UserNotFound;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<ApiErrorResponse<Void>> handleUserNotFound(UserNotFound ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    @ExceptionHandler(TaskNotFound.class)
    public ResponseEntity<ApiErrorResponse<Void>> handleTaskNotFound(TaskNotFound ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    @ExceptionHandler(PasswordIncorrect.class)
    public ResponseEntity<ApiErrorResponse<Void>> handlePasswordIncorrect(PasswordIncorrect ex, HttpServletRequest request) {
        return buildError(HttpStatus.UNAUTHORIZED, ex.getMessage(), request, null);
    }

    @ExceptionHandler({NonAuthorized.class, AccessDeniedException.class})
    public ResponseEntity<ApiErrorResponse<Void>> handleAuthorizationErrors(Exception ex, HttpServletRequest request) {
        return buildError(HttpStatus.FORBIDDEN, ex.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse<Void>> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toMessage)
                .toList();
        return buildError(HttpStatus.BAD_REQUEST, "Validation failed", request, errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse<Void>> handleUnexpectedError(Exception ex, HttpServletRequest request) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request, List.of(ex.getMessage()));
    }

    private ResponseEntity<ApiErrorResponse<Void>> buildError(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            List<String> errors
    ) {
        ApiErrorResponse<Void> response = ApiErrorResponse.<Void>builder()
                .success(false)
                .message(message)
                .statusCode(status.value())
                .path(request.getRequestURI())
                .timestamp(Instant.now())
                .errors(errors)
                .build();
        return ResponseEntity.status(status).body(response);
    }

    private String toMessage(FieldError error) {
        String defaultMessage = error.getDefaultMessage() == null ? "invalid value" : error.getDefaultMessage();
        return error.getField() + ": " + defaultMessage;
    }
}
