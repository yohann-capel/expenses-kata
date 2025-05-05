package com.yohann.kata.expenses;

import com.yohann.kata.expenses.domain.model.Expense;
import com.yohann.kata.expenses.domain.model.ExpenseCategory;
import com.yohann.kata.expenses.domain.model.ExpenseFilter;
import com.yohann.kata.expenses.domain.port.out.ExpenseRepository;
import com.yohann.kata.expenses.domain.service.ExpenseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepository repository;

    @InjectMocks
    private ExpenseServiceImpl service;

    private Expense validExpense;
    private ExpenseFilter emptyFilter;

    @BeforeEach
    void setUp() {
        validExpense = Expense.builder()
                .id(1L)
                .amount(new BigDecimal("100.00"))
                .category(ExpenseCategory.ALIMENTATION)
                .date(LocalDate.now())
                .description("Grocery shopping")
                .build();

        emptyFilter = ExpenseFilter.empty();
    }

    @Nested
    @DisplayName("Create Expense Tests")
    class CreateExpenseTests {

        @Test
        @DisplayName("Should create expense when data is valid")
        void shouldCreateExpenseWhenDataIsValid() {
            // Given
            when(repository.create(validExpense)).thenReturn(validExpense);

            // When
            Expense result = service.createExpense(validExpense);

            // Then
            assertEquals(validExpense, result);
            verify(repository, times(1)).create(validExpense);
        }

        @Test
        @DisplayName("Should throw exception when amount is negative")
        void shouldThrowExceptionWhenAmountIsNegative() {
            // Given
            Expense expenseWithNegativeAmount = Expense.builder()
                    .id(validExpense.getId())
                    .amount(new BigDecimal("-10.00"))
                    .category(validExpense.getCategory())
                    .date(validExpense.getDate())
                    .description(validExpense.getDescription())
                    .build();

            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.createExpense(expenseWithNegativeAmount)
            );
            assertEquals("Amount under zero", exception.getMessage());
            verify(repository, never()).create(any());
        }

        @Test
        @DisplayName("Should throw exception when category is null")
        void shouldThrowExceptionWhenCategoryIsNull() {
            // Given
            Expense expenseWithoutCategory = Expense.builder()
                    .id(validExpense.getId())
                    .amount(validExpense.getAmount())
                    .category(null)
                    .date(validExpense.getDate())
                    .description(validExpense.getDescription())
                    .build();

            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.createExpense(expenseWithoutCategory)
            );
            assertEquals("No category", exception.getMessage());
            verify(repository, never()).create(any());
        }

        @Test
        @DisplayName("Should throw exception when date is in the future")
        void shouldThrowExceptionWhenDateIsInFuture() {
            // Given
            Expense expenseWithFutureDate = Expense.builder()
                    .id(validExpense.getId())
                    .amount(validExpense.getAmount())
                    .category(validExpense.getCategory())
                    .date(LocalDate.now().plusDays(1))
                    .description(validExpense.getDescription())
                    .build();

            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.createExpense(expenseWithFutureDate)
            );
            assertEquals("Date after today", exception.getMessage());
            verify(repository, never()).create(any());
        }
    }

    @Nested
    @DisplayName("Get All Expenses Tests")
    class GetAllExpensesTests {

        @Test
        @DisplayName("Should return all expenses")
        void shouldReturnAllExpenses() {
            // Given
            Expense secondExpense = Expense.builder()
                    .id(2L)
                    .amount(new BigDecimal("200.00"))
                    .category(ExpenseCategory.LOISIRS)
                    .date(LocalDate.now())
                    .description("Trip expenses")
                    .build();

            List<Expense> expectedExpenses = Arrays.asList(validExpense, secondExpense);
            when(repository.getAll()).thenReturn(expectedExpenses);

            // When
            List<Expense> result = service.getAll();

            // Then
            assertEquals(expectedExpenses, result);
            verify(repository, times(1)).getAll();
        }
    }

    @Nested
    @DisplayName("Get Filtered Expenses Tests")
    class GetFilteredExpensesTests {

        @Test
        @DisplayName("Should return filtered expenses when filter is valid")
        void shouldReturnFilteredExpensesWhenFilterIsValid() {
            // Given
            ExpenseFilter filter = ExpenseFilter.builder()
                    .category(ExpenseCategory.ALIMENTATION)
                    .startDate(LocalDate.now().minusDays(7))
                    .endDate(LocalDate.now())
                    .build();

            List<Expense> expectedExpenses = Collections.singletonList(validExpense);
            when(repository.getAll(filter)).thenReturn(expectedExpenses);

            // When
            List<Expense> result = service.getFilteredList(filter);

            // Then
            assertEquals(expectedExpenses, result);
            verify(repository, times(1)).getAll(filter);
        }

        @Test
        @DisplayName("Should use empty filter when filter is null")
        void shouldUseEmptyFilterWhenFilterIsNull() {
            // Given
            List<Expense> expectedExpenses = Collections.singletonList(validExpense);
            when(repository.getAll(any(ExpenseFilter.class))).thenReturn(expectedExpenses);

            // When
            List<Expense> result = service.getFilteredList(null);

            // Then
            assertEquals(expectedExpenses, result);
            verify(repository, times(1)).getAll(any(ExpenseFilter.class));
        }

        @Test
        @DisplayName("Should throw exception when start date is after end date")
        void shouldThrowExceptionWhenStartDateIsAfterEndDate() {
            // Given
            ExpenseFilter filter = ExpenseFilter.builder()
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().minusDays(7))
                    .build();

            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.getFilteredList(filter)
            );
            assertEquals("Start date can't be after end date", exception.getMessage());
            verify(repository, never()).getAll(any(ExpenseFilter.class));
        }

//        @Test
//        @DisplayName("Should throw exception when end date is in the future")
//        void shouldThrowExceptionWhenEndDateIsInFuture() {
//            // Given
//            ExpenseFilter filter = ExpenseFilter.builder()
//                    .startDate(LocalDate.now().minusDays(7))
//                    .endDate(LocalDate.now().plusDays(1))
//                    .build();
//
//            // When & Then
//            IllegalArgumentException exception = assertThrows(
//                    IllegalArgumentException.class,
//                    () -> service.getFilteredList(filter)
//            );
//            assertEquals("End date can't be in the future", exception.getMessage());
//            verify(repository, never()).getAll(any(ExpenseFilter.class));
//        }
    }

    @Nested
    @DisplayName("Get Expense By Id Tests")
    class GetExpenseByIdTests {

        @Test
        @DisplayName("Should return expense when ID exists")
        void shouldReturnExpenseWhenIdExists() {
            // Given
            Long id = 1L;
            when(repository.getById(id)).thenReturn(Optional.of(validExpense));

            // When
            Expense result = service.getById(id);

            // Then
            assertEquals(validExpense, result);
            verify(repository, times(1)).getById(id);
        }

        @Test
        @DisplayName("Should throw exception when ID does not exist")
        void shouldThrowExceptionWhenIdDoesNotExist() {
            // Given
            Long id = 999L;
            when(repository.getById(id)).thenReturn(Optional.empty());

            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.getById(id)
            );
            assertEquals("ID not found", exception.getMessage());
            verify(repository, times(1)).getById(id);
        }
    }

    @Nested
    @DisplayName("Update Expense Tests")
    class UpdateExpenseTests {

        @Test
        @DisplayName("Should update expense when data is valid")
        void shouldUpdateExpenseWhenDataIsValid() {
            // Given
            Expense updatedExpense = Expense.builder()
                    .id(validExpense.getId())
                    .amount(new BigDecimal("150.00"))
                    .category(ExpenseCategory.ALIMENTATION)
                    .date(validExpense.getDate())
                    .description("Updated description")
                    .build();

            when(repository.update(updatedExpense)).thenReturn(updatedExpense);

            // When
            Expense result = service.updateExpense(updatedExpense);

            // Then
            assertEquals(updatedExpense, result);
            verify(repository, times(1)).update(updatedExpense);
        }

        @Test
        @DisplayName("Should throw exception when updating with invalid data")
        void shouldThrowExceptionWhenUpdatingWithInvalidData() {
            // Given
            Expense invalidExpense = Expense.builder()
                    .id(validExpense.getId())
                    .amount(new BigDecimal("-50.00"))
                    .category(validExpense.getCategory())
                    .date(validExpense.getDate())
                    .description(validExpense.getDescription())
                    .build();

            // When & Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.updateExpense(invalidExpense)
            );
            assertEquals("Amount under zero", exception.getMessage());
            verify(repository, never()).update(any());
        }
    }

    @Nested
    @DisplayName("Delete Expense Tests")
    class DeleteExpenseTests {

        @Test
        @DisplayName("Should delete expense by ID")
        void shouldDeleteExpenseById() {
            // Given
            Long id = 1L;
            doNothing().when(repository).delete(id);

            // When
            service.deleteExpense(id);

            // Then
            verify(repository, times(1)).delete(id);
        }
    }

    @Nested
    @DisplayName("Amount By Category Tests")
    class AmountByCategoryTests {

        @Test
        @DisplayName("Should return total amount for category")
        void shouldReturnTotalAmountForCategory() {
            // Given
            ExpenseCategory category = ExpenseCategory.ALIMENTATION;
            ExpenseFilter filter = ExpenseFilter.builder()
                    .category(category)
                    .build();

            BigDecimal expectedAmount = new BigDecimal("250.00");
            when(repository.totalByCategory(category)).thenReturn(expectedAmount);

            // When
            BigDecimal result = service.amountByCategory(filter.getCategory());

            // Then
            assertEquals(expectedAmount, result);
            verify(repository, times(1)).totalByCategory(category);
        }

//        @Test
//        @DisplayName("Should throw exception when category is not specified")
//        void shouldThrowExceptionWhenCategoryIsNotSpecified() {
//            // Given
//            ExpenseFilter filterWithoutCategory = ExpenseFilter.builder()
//                    .startDate(LocalDate.now().minusDays(7))
//                    .endDate(LocalDate.now())
//                    .build();
//
//            // When & Then
//            IllegalArgumentException exception = assertThrows(
//                    IllegalArgumentException.class,
//                    () -> service.amountByCategory(null)
//            );
//            assertEquals("Category is mandatory to calculate the total amount PER CATEGORY", exception.getMessage());
//            verify(repository, never()).totalByCategory(any());
//        }
    }
}