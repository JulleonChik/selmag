package org.julleon.feedback.controller.handler;


import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ExceptionHandlingControllerAdvice {

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ProblemDetail>> handleWebExchangeBindException(
            WebExchangeBindException exception
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("errors", exception
                .getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).toList());
        return Mono.just(
                ResponseEntity.badRequest()
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(problemDetail));
    }
}
