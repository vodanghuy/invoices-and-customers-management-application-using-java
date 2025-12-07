package io.huyvo.securecapita.exception;

import io.huyvo.securecapita.model.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice // Tự động quét và xử lý lỗi ở tất cả controller.
@Slf4j
public class HandleException extends ResponseEntityExceptionHandler implements ErrorController {

    // Xử lý các lỗi nội bộ do Spring phát sinh (ví dụ lỗi HTTP message conversion).
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .reason(exception.getMessage())
                        .developerMessage(exception.getMessage())
                        .status(HttpStatus.resolve(statusCode.value()))
                        .statusCode(statusCode.value())
                        .timeStamp(LocalDateTime.now().toString())
                        .build(), statusCode);
    }

    // Bắt lỗi validation @Valid
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        log.error(exception.getMessage());
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        String fieldMessage = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .reason(fieldMessage)
                        .developerMessage(exception.getMessage())
                        .status(HttpStatus.resolve(statusCode.value()))
                        .statusCode(statusCode.value())
                        .timeStamp(LocalDateTime.now().toString())
                        .build(), statusCode);
    }

    // Bắt lỗi vi phạm ràng buộc DB
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    protected ResponseEntity<Object> sQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .reason(exception.getMessage().contains("Duplicate entry") ? "Information already exists" : exception.getMessage())
                        .developerMessage(exception.getMessage())
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .timeStamp(LocalDateTime.now().toString())
                        .build(), BAD_REQUEST);
    }

    // Bắt lỗi đăng nhập sai
    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<Object> badCredentialsException(BadCredentialsException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .reason(exception.getMessage() + ", Incorrect email or password.")
                        .developerMessage(exception.getMessage())
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .timeStamp(LocalDateTime.now().toString())
                        .build(), BAD_REQUEST);
    }

    // Bắt các lỗi custom mà bạn tự ném ra
    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<Object> apiException(ApiException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .reason(exception.getMessage())
                        .developerMessage(exception.getMessage())
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .timeStamp(LocalDateTime.now().toString())
                        .build(), BAD_REQUEST);
    }

    // Bắt lỗi phân quyền
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<Object> accessDeniedException(AccessDeniedException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .reason("Access denied. You don\'t have access.")
                        .developerMessage(exception.getMessage())
                        .status(FORBIDDEN)
                        .statusCode(FORBIDDEN.value())
                        .timeStamp(LocalDateTime.now().toString())
                        .build(), FORBIDDEN);
    }

    // Bắt mọi exception còn lại không thuộc các loại trên.
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> exception(Exception exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .reason(exception.getMessage() != null ?
                                (exception.getMessage().contains("expected 1, actual 0") ? "Record not found" : exception.getMessage())
                                : "Some errors occurred")
                        .developerMessage(exception.getMessage())
                        .status(INTERNAL_SERVER_ERROR)
                        .statusCode(INTERNAL_SERVER_ERROR.value())
                        .timeStamp(LocalDateTime.now().toString())
                        .build(), INTERNAL_SERVER_ERROR);
    }

    // Bắt lỗi Spring trả ra khi query DB không có kết quả
    @ExceptionHandler(EmptyResultDataAccessException.class)
    protected ResponseEntity<Object> emptyResultDataAccessException(EmptyResultDataAccessException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .reason(exception.getMessage().contains("expected 1, actual 0") ? "Record not found" : exception.getMessage())
                        .developerMessage(exception.getMessage())
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .timeStamp(LocalDateTime.now().toString())
                        .build(), BAD_REQUEST);
    }

    // Bắt lỗi tài khoản bị disabled trong Spring Security.
    @ExceptionHandler(DisabledException.class)
    protected ResponseEntity<Object> disabledException(DisabledException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .reason("User account is currently disabled.")
                        .developerMessage(exception.getMessage())
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .timeStamp(LocalDateTime.now().toString())
                        .build(), BAD_REQUEST);
    }

    // Bắt lỗi tài khoản bị khóa.
    @ExceptionHandler(LockedException.class)
    protected ResponseEntity<Object> lockedException(LockedException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .reason("User account is currently locked.")
                        .developerMessage(exception.getMessage())
                        .status(BAD_REQUEST)
                        .statusCode(BAD_REQUEST.value())
                        .timeStamp(LocalDateTime.now().toString())
                        .build(), BAD_REQUEST);
    }
}
