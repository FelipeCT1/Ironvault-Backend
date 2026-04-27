package dev.fatec.ecommerce.config;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleNotFound(EntityNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Recurso não encontrado");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(SecurityException.class)
    public ProblemDetail handleUnauthorized(SecurityException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        pd.setTitle("Não autorizado");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleRuntime(RuntimeException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Erro de negócio");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        pd.setTitle("Erro de validação");
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        pd.setDetail(errors);
        return pd;
    }
}
