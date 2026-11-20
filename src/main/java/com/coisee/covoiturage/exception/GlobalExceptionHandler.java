package com.coisee.covoiturage.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {
        return createProblem(
                HttpStatus.NOT_FOUND,
                "Ressource introuvable",
                exception.getMessage(),
                "not-found",
                request
        );
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ProblemDetail> handleBusinessRule(
            BusinessRuleException exception,
            HttpServletRequest request
    ) {
        return createProblem(
                HttpStatus.CONFLICT,
                "Règle métier non respectée",
                exception.getMessage(),
                "business-rule",
                request
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleResponseStatus(
            ResponseStatusException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.valueOf(
                exception.getStatusCode().value()
        );

        return createProblem(
                status,
                status.getReasonPhrase(),
                exception.getReason(),
                "http-error",
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + " : " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return createProblem(
                HttpStatus.BAD_REQUEST,
                "Erreur de validation",
                message,
                "validation",
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpectedError(
            Exception exception,
            HttpServletRequest request
    ) {
        log.error("Erreur interne pendant {} {}",
                request.getMethod(), request.getRequestURI(), exception);

        return createProblem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erreur interne",
                "Une erreur inattendue est survenue.",
                "internal-error",
                request
        );
    }

    private ResponseEntity<ProblemDetail> createProblem(
            HttpStatus status,
            String title,
            String detail,
            String type,
            HttpServletRequest request
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                status,
                detail != null ? detail : title
        );

        problem.setTitle(title);
        problem.setType(URI.create(
                "https://covoiturage.example/problems/" + type
        ));
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("code", status.value());

        return ResponseEntity.status(status).body(problem);
    }
}
