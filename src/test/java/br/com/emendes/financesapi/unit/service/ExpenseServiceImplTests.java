package br.com.emendes.financesapi.unit.service;

import br.com.emendes.financesapi.dto.response.ExpenseResponse;
import br.com.emendes.financesapi.dto.request.ExpenseRequest;
import br.com.emendes.financesapi.model.entity.Expense;
import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.repository.ExpenseRepository;
import br.com.emendes.financesapi.service.impl.ExpenseServiceImpl;
import br.com.emendes.financesapi.util.creator.ExpenseCreator;
import br.com.emendes.financesapi.util.creator.ExpenseFormCreator;
import br.com.emendes.financesapi.util.creator.UserCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.NoResultException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for ExpenseService")
class ExpenseServiceImplTests {

  @InjectMocks
  private ExpenseServiceImpl expenseServiceImpl;

  @Mock
  private ExpenseRepository expenseRepositoryMock;


  private final Long NON_EXISTING_EXPENSE_ID = 99999L;
  private final Pageable PAGEABLE = PageRequest.of(0, 10, Direction.DESC, "date");
  private final Pageable PAGEABLE_WITH_PAGE_ONE = PageRequest.of(1, 10, Direction.DESC, "date");
  private final Authentication AUTHENTICATION = mock(Authentication.class);
  private final SecurityContext SECURITY_CONTEXT = mock(SecurityContext.class);
  private final ExpenseRequest EXPENSE_FORM = ExpenseFormCreator.validExpenseForm();

  @BeforeEach
  public void setUp() {
    final Long userId = 100L;
    Expense expenseToBeSaved = ExpenseCreator.validExpenseWithUser(new User(userId));
    Expense expenseSaved = ExpenseCreator.expenseWithAllArgs();

    PageImpl<Expense> pageExpense = new PageImpl<>(List.of(expenseSaved));
    PageImpl<Expense> pageOneEmpty = new PageImpl<>(Collections.emptyList(), PAGEABLE_WITH_PAGE_ONE, 4L);

    SecurityContextHolder.setContext(SECURITY_CONTEXT);

    BDDMockito.when(expenseRepositoryMock.existsByDescriptionAndMonthAndYearAndUser(
        EXPENSE_FORM.getDescription(),
        EXPENSE_FORM.parseDateToLocalDate().getMonthValue(),
        EXPENSE_FORM.parseDateToLocalDate().getYear())).thenReturn(false);

    BDDMockito.when(expenseRepositoryMock.save(expenseToBeSaved))
        .thenReturn(expenseSaved);

    BDDMockito.when(expenseRepositoryMock.findAllByUser(PAGEABLE))
        .thenReturn(pageExpense);

    BDDMockito.when(expenseRepositoryMock.findByDescriptionAndUser("solina", PAGEABLE))
        .thenReturn(pageExpense);

    BDDMockito.when(expenseRepositoryMock.findByDescriptionAndUser("lina", PAGEABLE))
        .thenReturn(Page.empty(PAGEABLE));

    BDDMockito.when(expenseRepositoryMock.findAllByUser(PAGEABLE_WITH_PAGE_ONE))
        .thenReturn(pageOneEmpty);

    BDDMockito.when(expenseRepositoryMock.findByDescriptionAndUser("uber", PAGEABLE_WITH_PAGE_ONE))
        .thenReturn(pageOneEmpty);

    BDDMockito.when(expenseRepositoryMock.findByIdAndUser(expenseSaved.getId()))
        .thenReturn(Optional.of(expenseSaved));

    BDDMockito.when(expenseRepositoryMock.findByIdAndUser(NON_EXISTING_EXPENSE_ID))
        .thenReturn(Optional.empty());

    BDDMockito.when(expenseRepositoryMock.existsByDescriptionAndMonthAndYearAndNotIdAndUser(
        EXPENSE_FORM.getDescription(),
        EXPENSE_FORM.parseDateToLocalDate().getMonthValue(),
        EXPENSE_FORM.parseDateToLocalDate().getYear(),
        expenseSaved.getId())).thenReturn(false);

    BDDMockito.when(SECURITY_CONTEXT.getAuthentication()).thenReturn(AUTHENTICATION);
    BDDMockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
        .thenReturn(UserCreator.userWithIdAndRoles());

    BDDMockito.when(expenseRepositoryMock.findByYearAndMonthAndUser(2022, 1, PAGEABLE))
        .thenReturn(pageExpense);

    BDDMockito.when(expenseRepositoryMock.findByYearAndMonthAndUser(2023, 9, PAGEABLE_WITH_PAGE_ONE))
        .thenReturn(pageOneEmpty);

    BDDMockito.when(expenseRepositoryMock.findByYearAndMonthAndUser(2000, 1, PAGEABLE))
        .thenReturn(Page.empty(PAGEABLE));
  }

  @Test
  @DisplayName("Create must returns ExpenseDto when created successfully")
  void create_ReturnsExpenseDto_WhenSuccessful() {
    ExpenseRequest expenseRequest = ExpenseFormCreator.validExpenseForm();
    ExpenseResponse expenseResponse = this.expenseServiceImpl.create(expenseRequest);

    Assertions.assertThat(expenseResponse).isNotNull();
    Assertions.assertThat(expenseResponse.getDescription()).isEqualTo(expenseRequest.getDescription());
    Assertions.assertThat(expenseResponse.getValue()).isEqualTo(expenseRequest.getValue());
  }

  @Test
  @DisplayName("create must throws ResponseStatusException when user already has expense with this description")
  void create_MustThrowsResponseStatusException_WhenUserAlreadyHasExpenseWithThisDescription(){
    BDDMockito.when(expenseRepositoryMock.existsByDescriptionAndMonthAndYearAndUser(
        EXPENSE_FORM.getDescription(),
        EXPENSE_FORM.parseDateToLocalDate().getMonthValue(),
        EXPENSE_FORM.parseDateToLocalDate().getYear())).thenReturn(true);

    ExpenseRequest expenseRequest = ExpenseFormCreator.validExpenseForm();
    Assertions.assertThatExceptionOfType(ResponseStatusException.class)
        .isThrownBy(() -> this.expenseServiceImpl.create(expenseRequest))
        .withMessageContaining("Uma despesa com essa descrição já existe em ");
  }

  @Test
  @DisplayName("readAllByUser must returns page of expenseDto when successful")
  void readAllByUser_ReturnsPageOfExpenseDto_WhenSuccessful() {
    Page<ExpenseResponse> pageExpenseDto = expenseServiceImpl.readAllByUser(PAGEABLE);

    Assertions.assertThat(pageExpenseDto).isNotEmpty();
    Assertions.assertThat(pageExpenseDto.getNumberOfElements()).isEqualTo(1);
  }

  @Test
  @DisplayName("readAllByUser must throws NoResultException when user don't have expenses")
  void readAllByUser_ThrowsNoResultException_WhenUserDontHaveExpenses() {
    BDDMockito.when(expenseRepositoryMock.findAllByUser(PAGEABLE))
        .thenReturn(Page.empty(PAGEABLE));

    Assertions.assertThatExceptionOfType(NoResultException.class)
        .isThrownBy(() -> expenseServiceImpl.readAllByUser(PAGEABLE))
        .withMessage("O usuário não possui despesas");
  }

  @Test
  @DisplayName("readAllByUser must returns empty page when user has expenses but request a page without data")
  void readAllByUser_ReturnsEmptyPage_WhenUserHasExpensesButRequestAPageWithoutData(){
    Page<ExpenseResponse> pageExpenseDto = expenseServiceImpl.readAllByUser(PAGEABLE_WITH_PAGE_ONE);

    Assertions.assertThat(pageExpenseDto).isEmpty();
    Assertions.assertThat(pageExpenseDto.getTotalElements()).isEqualTo(4L);
  }

  @Test
  @DisplayName("readByDescriptionAndUser must returns page of expenseDto when successful")
  void readByDescriptionAndUser_ReturnsPageOfExpenseDto_WhenSuccessful() {
    String description = "solina";

    Page<ExpenseResponse> pageExpenseDto = expenseServiceImpl.readByDescriptionAndUser(description, PAGEABLE);

    Assertions.assertThat(pageExpenseDto).isNotEmpty();
    Assertions.assertThat(pageExpenseDto.getNumberOfElements()).isEqualTo(1);
  }

  @Test
  @DisplayName("readByDescriptionAndUser must throws NoResultException when user don't have expenses")
  void readByDescriptionAndUser_ThrowsNoResultException_WhenUserDontHaveExpenses() {
    String description = "lina";

    Assertions.assertThatExceptionOfType(NoResultException.class)
        .isThrownBy(() -> expenseServiceImpl.readByDescriptionAndUser(description, PAGEABLE))
        .withMessageContaining("O usuário não possui despesas com descrição similar a ");
  }

  @Test
  @DisplayName("readByDescriptionAndUser must returns empty page when user has expenses but request a page without data")
  void readByDescriptionAndUser_ReturnsEmptyPage_WhenUserHasExpensesButRequestAPageWithoutData(){
    String description = "uber";
    Page<ExpenseResponse> pageExpenseDto = expenseServiceImpl.readByDescriptionAndUser(description, PAGEABLE_WITH_PAGE_ONE);

    Assertions.assertThat(pageExpenseDto).isEmpty();
    Assertions.assertThat(pageExpenseDto.getTotalElements()).isEqualTo(4L);
  }

  @Test
  @DisplayName("readByIdAndUser must returns optional expenseDto when successful")
  void readByIdAndUser_ReturnsOptionalExpenseDto_WhenSuccessful() {
    Long id = ExpenseCreator.expenseWithAllArgs().getId();

    ExpenseResponse expenseResponse = expenseServiceImpl.readByIdAndUser(id);

    Assertions.assertThat(expenseResponse).isNotNull();
    Assertions.assertThat(expenseResponse.getId()).isEqualTo(id);
  }

  @Test
  @DisplayName("readByIdAndUser must throws NoResultException when expenseId don't exists")
  void readByIdAndUser_ThrowsNoResultException_WhenExpenseIdDontExists() {
    Assertions.assertThatExceptionOfType(NoResultException.class)
        .isThrownBy(() -> expenseServiceImpl.readByIdAndUser(NON_EXISTING_EXPENSE_ID))
        .withMessage(String.format("Nenhuma despesa com id = %d para esse usuário", NON_EXISTING_EXPENSE_ID));
  }

  @Test
  @DisplayName("readByYearAndMonthAndUser must returns Page<ExpenseDto> when finded successful")
  void readByYearAndMonthAndUser_ReturnsPageExpenseDto_WhenFindedSuccessful(){
    int month = 1;
    int year = 2022;

    Page<ExpenseResponse> pageExpenseDto = expenseServiceImpl.readByYearAndMonthAndUser(year, month, PAGEABLE);

    Assertions.assertThat(pageExpenseDto).isNotEmpty();
    Assertions.assertThat(pageExpenseDto.getNumberOfElements()).isEqualTo(1);
  }

  @Test
  @DisplayName("readByYearAndMonthAndUser must throws NoResultException when don't has expenses")
  void readByYearAndMonthAndUser_ThrowsNoResultException_WhenDontHasExpenses(){
    int month = 1;
    int year = 2000;

    Assertions.assertThatExceptionOfType(NoResultException.class)
        .isThrownBy(() -> expenseServiceImpl.readByYearAndMonthAndUser(year, month, PAGEABLE))
        .withMessage(String.format("Não há despesas para o ano %d e mês %d", year, month));
  }

  @Test
  @DisplayName("readByYearAndMonthAndUser must returns empty page when user has expenses but request a page without data")
  void readByYearAndMonthAndUser_ReturnsEmptyPage_WhenUserHasExpensesButRequestAPageWithoutData(){
    int year = 2023;
    int month = 9;
    Page<ExpenseResponse> pageExpenseDto = expenseServiceImpl.readByYearAndMonthAndUser(year, month, PAGEABLE_WITH_PAGE_ONE);

    Assertions.assertThat(pageExpenseDto).isEmpty();
    Assertions.assertThat(pageExpenseDto.getTotalElements()).isEqualTo(4L);
  }

  @Test
  @DisplayName("update must returns ExpenseDto updated when successful")
  void update_ReturnsExpenseDtoUpdated_WhenSuccessful() {
    Long id = ExpenseCreator.expenseWithAllArgs().getId();
    ExpenseRequest expenseRequest = ExpenseFormCreator.validExpenseForm();

    ExpenseResponse updateExpense = expenseServiceImpl.update(id, expenseRequest);

    Assertions.assertThat(updateExpense).isNotNull();
    Assertions.assertThat(updateExpense.getId()).isEqualTo(id);
    Assertions.assertThat(updateExpense.getDescription()).isEqualTo(expenseRequest.getDescription());
  }

  @Test
  @DisplayName("update must throws ResponseStatusException when user already has another expense with same description")
  void update_MustThrowsResponseStatusException_WhenUserHasAnotherExpenseWithSameDescription() {
    BDDMockito.when(expenseRepositoryMock.existsByDescriptionAndMonthAndYearAndNotIdAndUser(
        EXPENSE_FORM.getDescription(),
        EXPENSE_FORM.parseDateToLocalDate().getMonthValue(),
        EXPENSE_FORM.parseDateToLocalDate().getYear(),
        1000L)).thenReturn(true);

    Long id = ExpenseCreator.expenseWithAllArgs().getId();
    ExpenseRequest expenseRequest = ExpenseFormCreator.validExpenseForm();

    Assertions.assertThatExceptionOfType(ResponseStatusException.class)
        .isThrownBy(() -> expenseServiceImpl.update(id, expenseRequest))
        .withMessageContaining("Outra despesa com essa descrição já existe em ");
  }

  @Test
  @DisplayName("update must throws NoResultException when expense don't exists")
  void update_ThrowsNoResultException_WhenExpenseDontExists() {
    ExpenseRequest expenseRequest = ExpenseFormCreator.validExpenseForm();

    Assertions.assertThatExceptionOfType(NoResultException.class)
        .isThrownBy(() -> expenseServiceImpl.update(NON_EXISTING_EXPENSE_ID, expenseRequest))
        .withMessage(String.format("Nenhuma despesa com id = %d para esse usuário", NON_EXISTING_EXPENSE_ID));
  }


  @Test
  @DisplayName("readByYearAndMonthAndUser must throws NoResultException when don't has expenses")
  void deleteById_ThrowsNoResultException_WhenExpenseDontExists(){
    Assertions.assertThatExceptionOfType(NoResultException.class)
        .isThrownBy(() -> expenseServiceImpl.deleteById(NON_EXISTING_EXPENSE_ID))
        .withMessage(String.format("Nenhuma despesa com id = %d para esse usuário", NON_EXISTING_EXPENSE_ID));
  }

}
