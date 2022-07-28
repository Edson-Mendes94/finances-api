package br.com.emendes.financesapi.validation.handler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.emendes.financesapi.controller.dto.error.ErrorDto;

@RestControllerAdvice
public class InvalidCategoryHandler {

  @ExceptionHandler(InvalidFormatException.class)
  public ResponseEntity<ErrorDto> handle(InvalidFormatException exception) {
    ErrorDto errorDto = new ErrorDto("Categoria inválida",
        "Categorias válidas: ALIMENTACAO, SAUDE, MORADIA, TRANSPORTE, EDUCACAO, LAZER, IMPREVISTOS, OUTRAS");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(errorDto);
  }

}