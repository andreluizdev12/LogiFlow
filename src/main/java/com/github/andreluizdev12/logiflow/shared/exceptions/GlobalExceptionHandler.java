package com.github.andreluizdev12.logiflow.shared.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(
            BusinessException exception,
            HttpServletRequest request
    ) {
        String traceId = createTraceId(request);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                exception.getStatus(),
                exception.getMessage()
        );

        problem.setTitle("Regra de negócio não atendida");
        problem.setInstance(URI.create(request.getRequestURI()));

        problem.setProperty("code", exception.getCode().name());
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("traceId", traceId);

        log.warn(
                "Business error. traceId={}, code={}, message={}",
                traceId,
                exception.getCode(),
                exception.getMessage()
        );

        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        String traceId = createTraceId(request);

        List<FieldValidationError> errors = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldValidationError(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .toList();

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Um ou mais campos estão inválidos"
        );

        problem.setTitle("Dados inválidos");
        problem.setInstance(URI.create(request.getRequestURI()));

        problem.setProperty(
                "code",
                ErrorCode.VALIDATION_ERROR.name()
        );
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("traceId", traceId);
        problem.setProperty("errors", errors);

        return problem;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        String traceId = createTraceId(request);

        log.error(
                "Database integrity error. traceId={}",
                traceId,
                exception
        );

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "A operação viola uma restrição de integridade"
        );

        problem.setTitle("Conflito de dados");
        problem.setInstance(URI.create(request.getRequestURI()));

        problem.setProperty("code", "DATA_INTEGRITY_VIOLATION");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("traceId", traceId);

        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpectedException(
            Exception exception,
            HttpServletRequest request
    ) {
        String traceId = createTraceId(request);

        log.error(
                "Unexpected error. traceId={}",
                traceId,
                exception
        );

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno inesperado"
        );

        problem.setTitle("Erro interno");
        problem.setInstance(URI.create(request.getRequestURI()));

        problem.setProperty(
                "code",
                ErrorCode.INTERNAL_SERVER_ERROR.name()
        );
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("traceId", traceId);

        return problem;
    }

    private String createTraceId(HttpServletRequest request) {
        String receivedTraceId = request.getHeader("X-Trace-Id");

        if (receivedTraceId != null && !receivedTraceId.isBlank()) {
            return receivedTraceId;
        }

        return UUID.randomUUID().toString();
    }
}