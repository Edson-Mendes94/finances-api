package br.com.emendes.financesapi.unit.form;

import java.math.BigDecimal;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.emendes.financesapi.dto.request.IncomeRequest;
import br.com.emendes.financesapi.util.creator.IncomeFormCreator;

@DisplayName("Tests for IncomeForm")
public class IncomeRequestTests {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  @DisplayName("Must return empty violations when IncomeForm is valid")
  void mustReturnEmptyViolations_WhenIncomeFormIsValid() {
    IncomeRequest validIncomeRequest = IncomeFormCreator.validIncomeForm();

    Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(validIncomeRequest);

    Assertions.assertThat(violations).isEmpty();
  }

  @Test
  @DisplayName("Must return empty violations when category is null")
  void mustReturnEmptyViolations_WhenCategoryIsNull() {
    String description = "Sálario";
    String date = "23/01/2022";
    BigDecimal value = new BigDecimal("2500.00");

    IncomeRequest invalidIncomeRequest = new IncomeRequest(description, date, value);

    Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(invalidIncomeRequest);

    Assertions.assertThat(violations).isEmpty();
  }

  @Test
  @DisplayName("Must return violations when date is invalid")
  void mustReturnViolations_WhenDateIsInvalid() {
    String description = "Sálario";
    String invalidDate = "23-01-2022";
    BigDecimal value = new BigDecimal("2500.00");

    IncomeRequest invalidIncomeRequest = new IncomeRequest(description, invalidDate, value);

    Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(invalidIncomeRequest);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(1);
    Assertions.assertThat(violations.stream().findAny().get().getMessage())
        .isEqualTo("A data informada é inválida!");
  }

  @Test
  @DisplayName("Must return violations when value is invalid")
  void mustReturnViolations_WhenValueIsInvalid() {
    String description = "Sálario";
    String date = "23/01/2022";
    BigDecimal invalidValue = new BigDecimal("2500000.00");

    IncomeRequest invalidIncomeRequest = new IncomeRequest(description, date, invalidValue);

    Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(invalidIncomeRequest);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(1);
  }

  @Test
  @DisplayName("Must return violations when value is negative")
  void mustReturnViolations_WhenValueIsNegative() {
    String description = "Sálario";
    String date = "23/01/2022";
    BigDecimal invalidValue = new BigDecimal("-2500.00");

    IncomeRequest invalidIncomeRequest = new IncomeRequest(description, date, invalidValue);

    Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(invalidIncomeRequest);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(1);
  }

  @Test
  @DisplayName("Must return violations when description is null")
  void mustReturnViolations_WhenDescriptionIsNull() {
    String description = null;
    String date = "23/01/2022";
    BigDecimal value = new BigDecimal("2500.00");

    IncomeRequest invalidIncomeRequest = new IncomeRequest(description, date, value);

    Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(invalidIncomeRequest);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(1);
  }

  @Test
  @DisplayName("Must return violations when date is null")
  void mustReturnViolations_WhenDateIsNull() {
    String description = "Sálario";
    String date = null;
    BigDecimal value = new BigDecimal("2500.00");

    IncomeRequest invalidIncomeRequest = new IncomeRequest(description, date, value);

    Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(invalidIncomeRequest);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(2);
  }

  @Test
  @DisplayName("Must return violations when value is null")
  void mustReturnViolations_WhenValueIsNull() {
    String description = "Sálario";
    String date = "23/01/2022";
    BigDecimal value = null;

    IncomeRequest invalidIncomeRequest = new IncomeRequest(description, date, value);

    Set<ConstraintViolation<IncomeRequest>> violations = validator.validate(invalidIncomeRequest);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(1);
  }

}