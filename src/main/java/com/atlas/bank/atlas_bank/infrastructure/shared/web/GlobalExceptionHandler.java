package com.atlas.bank.atlas_bank.infrastructure.shared.web;

import com.atlas.bank.atlas_bank.domain.account.exception.AccountBalanceNotZeroException;
import com.atlas.bank.atlas_bank.domain.account.exception.AccountNotActiveException;
import com.atlas.bank.atlas_bank.domain.account.exception.AccountNotFoundException;
import com.atlas.bank.atlas_bank.domain.account.exception.InsufficientFoundException;
import com.atlas.bank.atlas_bank.domain.transaction.exception.FraudCheckException;
import com.atlas.bank.atlas_bank.domain.transaction.exception.InvalidTransferException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Extends {@link ResponseEntityExceptionHandler} so standard Spring MVC errors (malformed JSON,
 * type mismatches, unknown routes, unsupported methods...) keep their 4xx status instead of
 * falling into the catch-all 500 handler.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ProblemDetail handleAccountNotFoundException(AccountNotFoundException e) {
        return problem(HttpStatus.NOT_FOUND, "Account Not Found", e.getMessage());
    }

    @ExceptionHandler(InsufficientFoundException.class)
    public ProblemDetail handleInsufficientFoundException(InsufficientFoundException e) {
        return problem(HttpStatus.UNPROCESSABLE_CONTENT, "Insufficient Balance", e.getMessage());
    }

    @ExceptionHandler(AccountNotActiveException.class)
    public ProblemDetail handleAccountNotActiveException(AccountNotActiveException e) {
        return problem(HttpStatus.UNPROCESSABLE_CONTENT, "Account Not Active", e.getMessage());
    }

    @ExceptionHandler(AccountBalanceNotZeroException.class)
    public ProblemDetail handleAccountBalanceNotZeroException(AccountBalanceNotZeroException e) {
        return problem(HttpStatus.UNPROCESSABLE_CONTENT, "Account Has Balance", e.getMessage());
    }

    @ExceptionHandler(FraudCheckException.class)
    public ProblemDetail handleFraudCheckException(FraudCheckException e) {
        return problem(HttpStatus.UNPROCESSABLE_CONTENT, "Operation blocked by fraud check", e.getMessage());
    }

    @ExceptionHandler(InvalidTransferException.class)
    public ProblemDetail handleInvalidTransferException(InvalidTransferException e) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid Transfer", e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException e) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid Request", e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException() {
        return problem(HttpStatus.FORBIDDEN, "Forbidden", "You are not allowed to perform this operation");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.warn("Data integrity violation: {}", e.getMostSpecificCause().getMessage());
        return problem(HttpStatus.CONFLICT, "Conflict", "The resource conflicts with existing data (e.g. duplicated account number)");
    }

    @ExceptionHandler(ConcurrencyFailureException.class)
    public ProblemDetail handleConcurrencyFailureException(ConcurrencyFailureException e) {
        log.warn("Concurrent modification: {}", e.getMessage());
        return problem(HttpStatus.CONFLICT, "Concurrent Modification", "The resource was modified concurrently, please retry");
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception e) {
        log.error(e.getMessage(), e);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Internal server Error");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation error");

        List<String> errors = new ArrayList<>();

        e.getBindingResult()
                .getFieldErrors()
                .forEach(fieldError -> errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage()));
        e.getBindingResult()
                .getGlobalErrors()
                .forEach(objectError -> errors.add(objectError.getDefaultMessage()));

        problemDetail.setProperty("errors", errors);

        return ResponseEntity.badRequest().headers(headers).body(problemDetail);
    }

    private static ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        return problemDetail;
    }
}
