package br.com.emendes.financesapi.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import br.com.emendes.financesapi.config.validation.error_dto.ErrorDto;
import br.com.emendes.financesapi.controller.dto.ExpenseDto;
import br.com.emendes.financesapi.model.enumerator.Category;
import br.com.emendes.financesapi.util.CustomMockMvc;
import br.com.emendes.financesapi.util.DtoFromMvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")
public class ExpenseControllerTests {

  @Autowired
  private CustomMockMvc mock;

  private String tokenLorem;

  private String tokenIpsum;

  @BeforeAll
  public void addUsuarioLorem() throws Exception {
    String name = "Lorem Amet";
    String email = "lorem.a@email.com";
    String password = "111111111";
    String confirm = "111111111";

    Map<String, Object> paramsSignup = Map.of("name", name, "email", email, "password", password, "confirm", confirm);
    Map<String, Object> paramsSignin = Map.of("email", email, "password", password);

    mock.post("/auth/signup", paramsSignup, "", 201);
    MvcResult result = mock.post("/auth/signin", paramsSignin, "", 200);

    tokenLorem = DtoFromMvcResult.tokenDto(result).getTypeWithToken();
  }

  @BeforeAll
  public void addUsuarioIpsum() throws Exception {
    String name = "Ipsum Amet";
    String email = "ipsum.a@email.com";
    String password = "22222222";
    String confirm = "22222222";

    Map<String, Object> paramsSignup = Map.of("name", name, "email", email, "password", password, "confirm", confirm);
    Map<String, Object> paramsSignin = Map.of("email", email, "password", password);

    mock.post("/auth/signup", paramsSignup, "", 201);
    MvcResult result = mock.post("/auth/signin", paramsSignin, "", 200);

    tokenIpsum = DtoFromMvcResult.tokenDto(result).getTypeWithToken();
  }

  @Test
  @Order(1)
  public void deveriaDevolverStatus201AoCriarDespesa() throws Exception {

    String description = "Gasolina";
    BigDecimal value = BigDecimal.valueOf(341.87);
    String date = "2022-01-28";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date);

    mock.post("/despesas", params, tokenLorem, 201);
  }

  @Test
  @Order(2)
  public void deveriaDevolverCategoryOutrasQuandoNaoInseridoCategory() throws Exception {

    String description = "Mercado";
    BigDecimal value = BigDecimal.valueOf(719.40);
    String date = "2022-01-31";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date);

    MvcResult result = mock.post("/despesas", params, tokenLorem, 201);
    ExpenseDto expenseDto = DtoFromMvcResult.expenseDto(result);

    Assertions.assertEquals(expenseDto.getCategory(), Category.OUTRAS);

  }

  @Test
  @Order(3)
  public void deveriaDevolver409AoCadastrarDescricaoDuplicadaEmMesmoMesEAno() throws Exception {

    String description = "Aluguel";
    BigDecimal value = BigDecimal.valueOf(1500.00);
    String date = "2022-01-08";
    String category = "MORADIA";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date, "category", category);

    mock.post("/despesas", params, tokenLorem, 201);
    mock.post("/despesas", params, tokenLorem, 409);

  }

  @Test
  @Order(4)
  public void deveriaDevolver201AoCadastrarDescricaoEmMesDiferentes() throws Exception {

    String description = "Netflix";
    BigDecimal value = BigDecimal.valueOf(39.90);
    String date = "2022-01-18";
    String category = "LAZER";

    Map<String, Object> params1 = Map.of("description", description, "value", value, "date", date, "category",
        category);

    String newDate = "2022-02-18";
    Map<String, Object> params2 = Map.of("description", description, "value", value, "date", newDate, "category",
        category);

    mock.post("/despesas", params1, tokenLorem, 201);
    mock.post("/despesas", params2, tokenLorem, 201);

  }

  @Test
  @Order(5)
  public void deveriaDevolver400AoNaoEnviarAlgumParametroObrigatorio() throws Exception {

    String description = "Farmácia";
    BigDecimal value = BigDecimal.valueOf(85.00);
    String date = "2022-01-22";
    String category = "SAUDE";

    mock.post("/despesas", Map.of("value", value, "date", date, "category", category), tokenLorem, 400);
    mock.post("/despesas", Map.of("description", description, "date", date, "category", category), tokenLorem, 400);
    mock.post("/despesas", Map.of("description", description, "value", value, "category", category), tokenLorem, 400);
    mock.post("/despesas", Map.of("value", value, "category", category), tokenLorem, 400);
    mock.post("/despesas", Map.of("description", description, "category", category), tokenLorem, 400);
    mock.post("/despesas", Map.of("date", date, "category", category), tokenLorem, 400);
    mock.post("/despesas", Map.of(), tokenLorem, 400);
  }

  @Test
  @Order(6)
  public void deveriaDevolver200AoBuscarTodasAsDespesas() throws Exception {
    mock.get("/despesas", tokenLorem, 200);
  }

  @Test
  @Order(7)
  public void deveriaDevolver200AoBuscarPorIdExistente() throws Exception {
    MvcResult result = mock.get("/despesas", tokenLorem, 200);
    List<ExpenseDto> listExpenseDto = DtoFromMvcResult.listExpenseDto(result);

    Long id = listExpenseDto.get(0).getId();

    mock.get("/despesas/" + id, tokenLorem, 200);
  }

  @Test
  @Order(8)
  public void deveriaDevolver200AoBuscarPorAnoEMesExistentes() throws Exception {
    mock.get("/despesas/2022/01", tokenLorem, 200);
  }

  @Test
  @Order(9)
  public void deveriaDevolver404AoBuscarPorAnoEMesInexistentes() throws Exception {
    mock.get("/despesas/2022/03", tokenLorem, 404);
  }

  @Test
  @Order(10)
  public void deveriaDevolver404AoBuscarPorIdInexistentes() throws Exception {
    mock.get("/despesas/999", tokenLorem, 404);
  }

  @Test
  @Order(11)
  public void deveriaDevolver200AoBuscarPorDescricaoExistente() throws Exception {
    mock.get("/despesas?description=net", tokenLorem, 200);
  }

  @Test
  @Order(12)
  public void deveriaDevolver404AoBuscarPorDescricaoInexistente() throws Exception {
    mock.get("/despesas?description=nettttt", tokenLorem, 404);
  }

  @Test
  @Order(13)
  public void deveriaDevolver200AoAtualizarDespesaCorretamente() throws Exception {
    MvcResult result = mock.get("/despesas", tokenLorem, 200);
    List<ExpenseDto> listExpenseDto = DtoFromMvcResult.listExpenseDto(result);

    Long id = listExpenseDto.get(0).getId();

    String description = "Combustivel";
    BigDecimal value = BigDecimal.valueOf(341.87);
    String date = "2022-01-28";
    String category = "TRANSPORTE";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date, "category", category);

    mock.put("/despesas/" + id, params, tokenLorem, 200);
  }

  @Test
  @Order(14)
  public void deveriaDevolver404AoAtualizarDespesaComIdInexistente() throws Exception {
    int id = 1000;
    String description = "Combustivel";
    BigDecimal value = BigDecimal.valueOf(341.87);
    String date = "2022-01-28";
    String category = "TRANSPORTE";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date, "category", category);

    mock.put("/despesas/" + id, params, tokenLorem, 404);
  }

  @Test
  @Order(15)
  public void deveriaDevolver400AoAtualizarDespesaSemAlgumParametroObrigatorio() throws Exception {
    String description = "Combustivel";
    BigDecimal value = BigDecimal.valueOf(341.87);
    String date = "2022-01-28";
    String category = "TRANSPORTE";

    mock.put("/despesas/1", Map.of("value", value, "date", date, "category", category), tokenLorem, 400);
    mock.put("/despesas/1", Map.of("description", description, "date", date, "category", category), tokenLorem, 400);
    mock.put("/despesas/1", Map.of("description", description, "value", value, "category", category), tokenLorem, 400);
    mock.put("/despesas/1", Map.of("value", value, "category", category), tokenLorem, 400);
    mock.put("/despesas/1", Map.of("description", description, "category", category), tokenLorem, 400);
    mock.put("/despesas/1", Map.of("date", date, "category", category), tokenLorem, 400);
    mock.put("/despesas/1", Map.of(), tokenLorem, 400);
  }

  @Test
  @Order(16)
  public void deveriaDevolver200AoDeletarUmaDespesaComIdExistente() throws Exception {
    MvcResult result = mock.get("/despesas", tokenLorem, 200);
    List<ExpenseDto> listExpenseDto = DtoFromMvcResult.listExpenseDto(result);

    Long id = listExpenseDto.get(0).getId();

    mock.delete("/despesas/" + id, tokenLorem, 200);
  }

  @Test
  @Order(17)
  public void deveriaDevolver404AoDeletarUmaDespesaComIdInexistente() throws Exception {
    int id = 1000;
    mock.delete("/despesas/" + id, tokenLorem, 404);
  }

  @Test
  @Order(18)
  public void deveriaDevolverSomenteAsDespesasDeIpsum() throws Exception {
    String description1 = "Aluguel";
    BigDecimal value1 = new BigDecimal("1000.0");
    String date1 = "2022-01-05";
    String category = "MORADIA";

    String description2 = "Condomínio";
    BigDecimal value2 = new BigDecimal("200.0");
    String date2 = "2022-01-05";

    Map<String, Object> params1 = Map.of("description", description1, "value", value1, "date", date1, "category",
        category);
    Map<String, Object> params2 = Map.of("description", description2, "value", value2, "date", date2, "category",
        category);

    mock.post("/despesas", params1, tokenIpsum, 201);
    mock.post("/despesas", params2, tokenIpsum, 201);

    MvcResult result = mock.get("/despesas", tokenIpsum, 200);
    List<ExpenseDto> listExpenseDto = DtoFromMvcResult.listExpenseDto(result);
    List<ExpenseDto> listExpected = new ArrayList<>();

    ExpenseDto expenseDto1 = new ExpenseDto(6l, description1, LocalDate.parse(date1), value1,
        Category.valueOf(category));
    ExpenseDto expenseDto2 = new ExpenseDto(7l, description2, LocalDate.parse(date2), value2,
        Category.valueOf(category));

    listExpected.add(expenseDto1);
    listExpected.add(expenseDto2);

    Assertions.assertEquals(listExpected.size(), listExpenseDto.size());
    Assertions.assertEquals(listExpected, listExpenseDto);
  }

  @Test
  @Order(19)
  public void deveriaDevolver404AoTentarAtualizarDespesaDeOutroUsuario() throws Exception {
    int id = 1;
    String description = "Spotify";
    BigDecimal value = BigDecimal.valueOf(20.00);
    String date = "2022-01-08";
    String category = "LAZER";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date, "category", category);

    MvcResult result = mock.put("/despesas/" + id, params, tokenIpsum, 404);

    ErrorDto errorDto = DtoFromMvcResult.errorDto(result);

    Assertions.assertEquals("Not Found", errorDto.getError());
    Assertions.assertEquals("Nenhuma despesa com esse id para esse usuário", errorDto.getMessage());
  }

  @Test
  @Order(20)
  public void deveriaDevolver404AoTentarDeletarDespesaDeOutroUsuario() throws Exception {
    Long id = 1l;

    MvcResult result = mock.delete("/despesas/" + id, tokenIpsum, 404);

    ErrorDto errorDto = DtoFromMvcResult.errorDto(result);

    Assertions.assertEquals("Not Found", errorDto.getError());
    Assertions.assertEquals("Nenhuma despesa com esse id para esse usuário", errorDto.getMessage());
  }

}
