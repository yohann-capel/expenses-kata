package com.yohann.kata.expenses;

import com.yohann.kata.expenses.domain.model.Expense;
import com.yohann.kata.expenses.domain.model.ExpenseCategory;
import com.yohann.kata.expenses.infra.repository.DBRepository;
import com.yohann.kata.expenses.infra.repository.ExpenseRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseRepositoryImplTest {

    @Mock
    private DBRepository dbRepository;

    @InjectMocks
    private ExpenseRepositoryImpl expenseRepository;

    private Expense alimentationExpense1;
    private Expense alimentationExpense2;
    private Expense transportExpense;

    @BeforeEach
    void setUp() {
        alimentationExpense1 = Expense.builder()
                .id(1L)
                .category(ExpenseCategory.ALIMENTATION)
                .amount(new BigDecimal("50.00"))
                .date(LocalDate.of(2023, 1, 15))
                .description("Courses")
                .build();

        alimentationExpense2 = Expense.builder()
                .id(2L)
                .category(ExpenseCategory.ALIMENTATION)
                .amount(new BigDecimal("25.00"))
                .date(LocalDate.of(2023, 3, 5))
                .description("Restaurant")
                .build();

        transportExpense = Expense.builder()
                .id(3L)
                .category(ExpenseCategory.TRANSPORTS)
                .amount(new BigDecimal("30.00"))
                .date(LocalDate.of(2023, 2, 10))
                .description("Ticket de bus")
                .build();
    }

    @Test
    void totalByCategory_ShouldReturnSumOfExpensesInCategory() {
        // Given
        List<Expense> alimentationExpenses = Arrays.asList(alimentationExpense1, alimentationExpense2);
        when(dbRepository.getAll(any())).thenReturn(alimentationExpenses);

        // When
        BigDecimal total = expenseRepository.totalByCategory(ExpenseCategory.ALIMENTATION);

        // Then
        assertEquals(new BigDecimal("75.00"), total);
    }

    @Test
    void totalByCategory_WithNoExpensesInCategory_ShouldReturnZero() {
        // Given
        when(dbRepository.getAll(any())).thenReturn(List.of());

        // When
        BigDecimal total = expenseRepository.totalByCategory(ExpenseCategory.LOISIRS);

        // Then
        assertEquals(BigDecimal.ZERO, total);
    }

    @Test
    void totalByCategory_WithMultipleCategories_ShouldProcessOnlyRequestedCategory() {
        // Given
        List<Expense> transportExpenses = List.of(transportExpense);
        when(dbRepository.getAll(any())).thenReturn(transportExpenses);

        // When
        BigDecimal total = expenseRepository.totalByCategory(ExpenseCategory.TRANSPORTS);

        // Then
        assertEquals(new BigDecimal("30.00"), total);
    }
}