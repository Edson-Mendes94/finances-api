package br.com.emendes.financesapi.config.validation.error_dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class ErrorDto {

  @Schema(example = "Erro")
  private String error;

  @Schema(example = "Informação a mais sobre o erro")
  private String message;

  public ErrorDto(String error, String message) {
    this.error = error;
    this.message = message;
  }

  public String getError() {
    return error;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public String toString() {
    return "[ErrorDto: error="+error+", message="+message+"]";
  }

}
