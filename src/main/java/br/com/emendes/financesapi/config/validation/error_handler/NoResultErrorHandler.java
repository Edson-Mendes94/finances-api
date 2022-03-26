package br.com.emendes.financesapi.config.validation.error_handler;

import javax.persistence.NoResultException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.emendes.financesapi.config.validation.error_dto.ErrorDto;

@RestControllerAdvice
public class NoResultErrorHandler {
  
  @ExceptionHandler(NoResultException.class)
  public ResponseEntity<ErrorDto> handle(NoResultException exception){
    ErrorDto errorDto = new ErrorDto("Not Found", exception.getMessage());
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(errorDto);
  }

}
