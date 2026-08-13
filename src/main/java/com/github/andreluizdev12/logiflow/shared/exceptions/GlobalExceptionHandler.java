package com.github.andreluizdev12.logiflow.shared.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(
            BusinessException exception,
            HttpServletRequest request
    ) {
        String traceId = createTraceId(request);
        log.warn("Business error. traceId={}, code={}, message={}",
                traceId, exception.getCode(), exception.getMessage());

        return createProblem(
                exception.getStatus(),
                "Regra de negócio não atendida",
                exception.getMessage(),
                exception.getCode().name(),
                traceId,
                request
        );
    }

    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomainException(
            DomainException exception,
            HttpServletRequest request
    ) {
        String traceId = createTraceId(request);
        log.warn("Domain error. traceId={}, code={}, message={}",
                traceId, exception.getCode(), exception.getMessage());

        return createProblem(
                HttpStatus.BAD_REQUEST,
                "Regra de domínio não atendida",
                exception.getMessage(),
                exception.getCode().name(),
                traceId,
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<FieldValidationError> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldValidationError(error.getField(), error.getDefaultMessage()))
                .toList();

        ProblemDetail problem = createBadRequest(
                "Dados inválidos",
                "Um ou mais campos estão inválidos",
                request
        );
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleUnreadableMessage(HttpServletRequest request) {
        return createBadRequest(
                "Requisição inválida",
                "O corpo da requisição está malformado ou contém um valor inválido",
                request
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        ProblemDetail problem = createBadRequest(
                "Parâmetro inválido",
                "O parâmetro informado possui formato inválido",
                request
        );
        problem.setProperty("parameter", exception.getName());
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        String traceId = createTraceId(request);
        log.warn("Invalid domain argument. traceId={}, message={}", traceId, exception.getMessage());

        return createProblem(
                HttpStatus.BAD_REQUEST,
                "Regra de domínio não atendida",
                exception.getMessage(),
                ErrorCode.BUSINESS_RULE_VIOLATION.name(),
                traceId,
                request
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        String traceId = createTraceId(request);
        log.error("Database integrity error. traceId={}", traceId, exception);

        return createProblem(
                HttpStatus.CONFLICT,
                "Conflito de dados",
                "A operação viola uma restrição de integridade",
                "DATA_INTEGRITY_VIOLATION",
                traceId,
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpectedException(
            Exception exception,
            HttpServletRequest request
    ) {
        String traceId = createTraceId(request);
        log.error("Unexpected error. traceId={}", traceId, exception);

        return createProblem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno",
                "Ocorreu um erro interno inesperado",
                ErrorCode.INTERNAL_SERVER_ERROR.name(),
                traceId,
                request
        );
    }

    private ProblemDetail createBadRequest(String title, String detail, HttpServletRequest request) {
        return createProblem(
                HttpStatus.BAD_REQUEST,
                title,
                detail,
                ErrorCode.VALIDATION_ERROR.name(),
                createTraceId(request),
                request
        );
    }

    private ProblemDetail createProblem(
            HttpStatus status,
            String title,
            String detail,
            String code,
            String traceId,
            HttpServletRequest request
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("code", code);
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("traceId", traceId);
        return problem;
    }

    private String createTraceId(HttpServletRequest request) {
        String receivedTraceId = request.getHeader("X-Trace-Id");
        return receivedTraceId != null && !receivedTraceId.isBlank()
                ? receivedTraceId
                : UUID.randomUUID().toString();
    }
}
