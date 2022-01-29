package br.com.emendes.financesapi.config.validation.error_handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import br.com.emendes.financesapi.config.validation.error_dto.ConflictErrorDto;

@RestControllerAdvice
public class ConflictErrorhandler {
  
  @ResponseStatus(code = HttpStatus.CONFLICT)
  @ExceptionHandler(ResponseStatusException.class)
  public ConflictErrorDto handle(ResponseStatusException exception){

    ConflictErrorDto conflictErrorDto = new ConflictErrorDto(
        exception.getRawStatusCode(), exception.getStatus().name(), exception.getReason());

    return conflictErrorDto;
  }
}