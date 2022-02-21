package br.com.emendes.financesapi.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.emendes.financesapi.controller.dto.SummaryDto;
import br.com.emendes.financesapi.controller.dto.ValueByCategory;
import br.com.emendes.financesapi.model.enumerator.Category;

@Service
public class SummaryService {

  @Autowired
  private ExpenseService expenseService;

  @Autowired
  private IncomeService incomeService;

  public ResponseEntity<SummaryDto> monthSummary(Integer year, Integer month) {

    try {
      BigDecimal incomeTotalValue = incomeService.getTotalValueByMonthAndYear(year, month);
      BigDecimal expenseTotalValue = expenseService.getTotalValueByMonthAndYear(year, month);
      SummaryDto summaryDto = new SummaryDto(incomeTotalValue, expenseTotalValue, getTotalByCategory(year, month));

      return ResponseEntity.ok(summaryDto);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

  }

  private List<ValueByCategory> getTotalByCategory(Integer year, Integer month) {
    List<ValueByCategory> totalByCategory = new ArrayList<>();
    for (Category category : Category.values()) {
      BigDecimal total = expenseService.getTotalByCategoryInYearAndMonth(category, year, month);
      totalByCategory.add(new ValueByCategory(category, total));
    }
    return totalByCategory;
  }
}