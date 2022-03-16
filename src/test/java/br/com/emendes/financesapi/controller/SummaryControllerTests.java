package br.com.emendes.financesapi.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import br.com.emendes.financesapi.controller.dto.SummaryDto;
import br.com.emendes.financesapi.controller.dto.TokenDto;
import br.com.emendes.financesapi.controller.dto.ValueByCategory;
import br.com.emendes.financesapi.model.enumerator.Category;
import br.com.emendes.financesapi.util.CustomMockMvc;
import br.com.emendes.financesapi.util.DtoFromMvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class SummaryControllerTests {

  @Autowired
  private CustomMockMvc mock;

  private String tokenLorem;

  private String tokenIpsum;

  private void addRoles() throws Exception {
    mock.post("/role", Map.of("name", "ROLE_USER"), "", 201);
  }

  private void addUsuarioLorem() throws Exception {
    String name = "Lorem";
    String email = "lorem@email.com";
    String password = "111111";
    String confirm = "111111";

    Map<String, Object> paramsSignup = Map.of("name", name, "email", email, "password", password, "confirm", confirm);
    Map<String, Object> paramsSignin = Map.of("email", email, "password", password);

    mock.post("/auth/signup", paramsSignup, "", 201);
    MvcResult result = mock.post("/auth/signin", paramsSignin, "", 200);

    tokenLorem = tokenFromTokenDto(DtoFromMvcResult.tokenDto(result));
  }

  private void addUsuarioIpsum() throws Exception {
    String name = "Ipsum";
    String email = "ipsum@email.com";
    String password = "222222";
    String confirm = "222222";

    Map<String, Object> paramsSignup = Map.of("name", name, "email", email, "password", password, "confirm", confirm);
    Map<String, Object> paramsSignin = Map.of("email", email, "password", password);

    mock.post("/auth/signup", paramsSignup, "", 201);
    MvcResult result = mock.post("/auth/signin", paramsSignin, "", 200);

    tokenIpsum = tokenFromTokenDto(DtoFromMvcResult.tokenDto(result));
  }

  // @Test
  // @Order(1)
  private void populateLorem() throws Exception {
    mock.post("/receitas", Map.of("description", "Salário1", "value", new BigDecimal(2500.00), "date", "2022-02-08"),
        tokenLorem,
        201);
    mock.post("/receitas", Map.of("description", "Salário2", "value", new BigDecimal(1500.00), "date", "2022-02-10"),
        tokenLorem,
        201);

    mock.post("/despesas",
        Map.of("description", "Aluguel", "value", new BigDecimal(1000.00), "date", "2022-02-05", "category", "MORADIA"),
        tokenLorem,
        201);
    mock.post("/despesas",
        Map.of("description", "Alimentação", "value", new BigDecimal(800.00), "date", "2022-02-28", "category",
            "ALIMENTACAO"),
        tokenLorem,
        201);
    mock.post("/despesas",
        Map.of("description", "Farmácia", "value", new BigDecimal(150.00), "date", "2022-02-28", "category", "SAUDE"),
        tokenLorem,
        201);
  }

  // @Test
  // @Order(2)
  // TODO: Entender por que os testes executaram antes do @BeforeAll terminar de executar.
  // Os métodos dentro do @BeforeAll eram addRoles(), addUsuarioLorem(), addUsuarioIpsum(),
  // populateLorem() e populateIpsum().
  private void populateIpsum() throws Exception {
    mock.post("/receitas", Map.of("description", "Salário1", "value", new BigDecimal(1800.00), "date", "2022-02-08"),
        tokenIpsum,
        201);
    mock.post("/receitas", Map.of("description", "Salário2", "value", new BigDecimal(1200.00), "date", "2022-02-10"),
        tokenIpsum,
        201);
    mock.post("/despesas",
        Map.of("description", "Aluguel", "value", new BigDecimal(800.00), "date", "2022-02-05", "category", "MORADIA"),
        tokenIpsum,
        201);
    mock.post("/despesas",
        Map.of("description", "Alimentação", "value", new BigDecimal(700.00), "date", "2022-02-28", "category",
            "ALIMENTACAO"),
        tokenIpsum,
        201);
    mock.post("/despesas",
        Map.of("description", "Farmácia", "value", new BigDecimal(250.00), "date", "2022-02-28", "category", "SAUDE"),
        tokenIpsum,
        201);
  }

  @BeforeAll
  public void populateDB() throws Exception {
    addRoles();
    addUsuarioLorem();
    addUsuarioIpsum();
    populateLorem();
    populateIpsum();
  }

  @Test
  @Order(1)
  public void deveriaDevolver200AoBuscarPorResumoMensal() throws Exception {
    int year = 2022;
    int month = 2;
    mock.get("/resumo/" + year + "/" + month, tokenLorem, 200);
  }

  @Test
  @Order(2)
  public void deveriaDevolver404AoBuscarPorResumoInexistente() throws Exception {
    int year = 2022;
    int month = 5;
    mock.get("/resumo/" + year + "/" + month, tokenLorem, 404);
  }

  @Test
  @Order(3)
  public void summaryDtoRecebidoDeveriaSerIgualAoSummaryDtoDeLorem() throws Exception {
    int year = 2022;
    int month = 2;
    MvcResult result = mock.get("/resumo/" + year + "/" + month, tokenLorem, 200);
    SummaryDto summaryResponse = DtoFromMvcResult.summaryDtoFromJsonNode(result);

    List<ValueByCategory> valuesByCategory = new ArrayList<>();
    // FIXME: Refatorar as adicões abaixo.
    valuesByCategory.add(new ValueByCategory(Category.ALIMENTACAO, new BigDecimal("700.0")));
    valuesByCategory.add(new ValueByCategory(Category.SAUDE, new BigDecimal("150.0")));
    valuesByCategory.add(new ValueByCategory(Category.MORADIA, new BigDecimal("1000.0")));
    valuesByCategory.add(new ValueByCategory(Category.TRANSPORTE, new BigDecimal("0")));
    valuesByCategory.add(new ValueByCategory(Category.EDUCACAO, new BigDecimal("0")));
    valuesByCategory.add(new ValueByCategory(Category.LAZER, new BigDecimal("0")));
    valuesByCategory.add(new ValueByCategory(Category.IMPREVISTOS, new BigDecimal("0")));
    valuesByCategory.add(new ValueByCategory(Category.OUTRAS, new BigDecimal("0")));

    SummaryDto summaryExpected = new SummaryDto(new BigDecimal("4000.0"), new BigDecimal("1950.0"), valuesByCategory);

    Assertions.assertEquals(summaryExpected, summaryResponse);
  }

  @Test
  @Order(4)
  public void summaryDtoRecebidoDeveriaSerIgualAoSummaryDtoDeIpsum() throws Exception {
    int year = 2022;
    int month = 2;
    MvcResult result = mock.get("/resumo/" + year + "/" + month, tokenIpsum, 200);
    SummaryDto summaryResponse = DtoFromMvcResult.summaryDtoFromJsonNode(result);

    List<ValueByCategory> valuesByCategory = new ArrayList<>();
    // FIXME: Refatorar as adicões abaixo.
    valuesByCategory.add(new ValueByCategory(Category.ALIMENTACAO, new BigDecimal("800.0")));
    valuesByCategory.add(new ValueByCategory(Category.SAUDE, new BigDecimal("250.0")));
    valuesByCategory.add(new ValueByCategory(Category.MORADIA, new BigDecimal("800.0")));
    valuesByCategory.add(new ValueByCategory(Category.TRANSPORTE, new BigDecimal("0")));
    valuesByCategory.add(new ValueByCategory(Category.EDUCACAO, new BigDecimal("0")));
    valuesByCategory.add(new ValueByCategory(Category.LAZER, new BigDecimal("0")));
    valuesByCategory.add(new ValueByCategory(Category.IMPREVISTOS, new BigDecimal("0")));
    valuesByCategory.add(new ValueByCategory(Category.OUTRAS, new BigDecimal("0")));

    SummaryDto summaryExpected = new SummaryDto(new BigDecimal("3000.0"), new BigDecimal("1750.0"), valuesByCategory);

    Assertions.assertEquals(summaryExpected, summaryResponse);
  }

  @Test
  @Order(5)
  public void deveriaDevolver400AoBuscarComAnoNaoNumerico() throws Exception {
    mock.get("/resumo/2O22/12", tokenLorem, 400);
  }

  private String tokenFromTokenDto(TokenDto tokenDto) {
    return tokenDto.getType() + " " + tokenDto.getToken();
  }

}
