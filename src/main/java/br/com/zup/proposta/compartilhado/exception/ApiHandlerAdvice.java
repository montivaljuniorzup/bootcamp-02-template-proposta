package br.com.zup.proposta.compartilhado.exception;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@RestControllerAdvice
public class ApiHandlerAdvice {

    private static Logger logger = LoggerFactory.getLogger(ApiHandlerAdvice.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroPadronizado> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        Collection<String> mensagens = new ArrayList<>();

        e.getBindingResult()
                .getFieldErrors()
                .forEach(fieldError -> {
                    String message = String.format("Campo %s %s", fieldError.getField(), fieldError.getDefaultMessage());
                    mensagens.add(message);
                });
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErroPadronizado(mensagens));
    }

    @ExceptionHandler(ApiErrorException.class)
    public ResponseEntity<ErroPadronizado> handleApiErrorException(ApiErrorException e){
                return ResponseEntity
                        .status(e.getHttpStatus())
                        .body(new ErroPadronizado(Arrays.asList(e.getReason())));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErroPadronizado> handleApiErrorException(ConstraintViolationException e){
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new ErroPadronizado(Arrays.asList(e.getMessage())));
    }

    @ExceptionHandler(FeignException.FeignClientException.class)
    public ResponseEntity<ErroPadronizado> handleFeignClientException(FeignException.FeignClientException e){
                return ResponseEntity
                        .status(e.status())
                        .body(new ErroPadronizado(Arrays.asList(e.getMessage())));
    }
    @ExceptionHandler(FeignException.FeignServerException.class)
    public ResponseEntity<ErroPadronizado> handleFeignServerException(FeignException.FeignServerException e){
                return ResponseEntity
                        .status(e.status())
                        .body(new ErroPadronizado(Arrays.asList(e.getMessage())));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErroPadronizado> handleResponseStatusException(ResponseStatusException e){
                return ResponseEntity
                        .status(e.getStatus())
                        .body(new ErroPadronizado(Arrays.asList(e.getMessage())));
    }




}
