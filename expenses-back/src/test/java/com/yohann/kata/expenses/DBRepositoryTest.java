package com.yohann.kata.expenses;

import com.yohann.kata.expenses.domain.model.Expense;
import com.yohann.kata.expenses.domain.model.ExpenseCategory;
import com.yohann.kata.expenses.domain.model.ExpenseFilter;
import com.yohann.kata.expenses.infra.repository.DBRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DBRepositoryTest {

    private DBRepository repository;
    private Expense expense1;
    private Expense expense2;
    private Expense expense3;

    @BeforeEach
    void setUp() {
        repository = new DBRepository();

        expense1 = Expense.builder()
                .id(0L)
                .category(ExpenseCategory.ALIMENTATION)
                .amount(new BigDecimal("50.00"))
                .date(LocalDate.of(2023, 1, 15))
                .description("Courses")
                .build();

        expense2 = Expense.builder()
                .id(1L)
                .category(ExpenseCategory.TRANSPORTS)
                .amount(new BigDecimal("30.00"))
                .date(LocalDate.of(2023, 2, 10))
                .description("Ticket de bus")
                .build();

        expense3 = Expense.builder()
                .id(2L)
                .category(ExpenseCategory.ALIMENTATION)
                .amount(new BigDecimal("25.00"))
                .date(LocalDate.of(2023, 3, 5))
                .description("Restaurant")
                .build();

        repository.save(expense1);
        repository.save(expense2);
        repository.save(expense3);
    }

    @Test
    void testGetAll_ShouldReturnAllExpenses() {
        // When
        List<Expense> result = repository.getAll();
        
        // Then
        assertEquals(3, result.size());
        assertTrue(result.contains(expense1));
        assertTrue(result.contains(expense2));
        assertTrue(result.contains(expense3));
    }

    @Test
    void testGetAll_WithCategoryFilter_ShouldReturnFilteredExpenses() {
        // Given
        ExpenseFilter filter = ExpenseFilter.builder()
                .category(ExpenseCategory.ALIMENTATION)
                .build();
        
        // When
        List<Expense> result = repository.getAll(filter);
        
        // Then
        assertEquals(2, result.size());
        assertTrue(result.contains(expense1));
        assertTrue(result.contains(expense3));
        assertFalse(result.contains(expense2));
    }

    @Test
    void testGetAll_WithDateFilter_ShouldReturnFilteredExpenses() {
        // Given
        ExpenseFilter filter = ExpenseFilter.builder()
                .startDate(LocalDate.of(2023, 1, 20))
                .endDate(LocalDate.of(2023, 2, 20))
                .build();
        
        // When
        List<Expense> result = repository.getAll(filter);
        
        // Then
        assertEquals(1, result.size());
        assertTrue(result.contains(expense2));
    }

    @Test
    void testGetAll_WithCategoryAndDateFilter_ShouldReturnFilteredExpenses() {
        // Given
        ExpenseFilter filter = ExpenseFilter.builder()
                .category(ExpenseCategory.ALIMENTATION)
                .startDate(LocalDate.of(2023, 1, 1))
                .endDate(LocalDate.of(2023, 2, 1))
                .build();
        
        // When
        List<Expense> result = repository.getAll(filter);
        
        // Then
        assertEquals(1, result.size());
        assertTrue(result.contains(expense1));
    }

    @Test
    void testGetById_ExistingId_ShouldReturnExpense() {
        // Given
        Long id = expense2.getId();
        
        // When
        Optional<Expense> result = repository.getById(id);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(expense2, result.get());
    }

    @Test
    void testGetById_NonExistingId_ShouldReturnEmptyOptional() {
        // Given
        Long nonExistingId = 999L;
        
        // When
        Optional<Expense> result = repository.getById(nonExistingId);
        
        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testSave_NewExpense_ShouldCreateAndAssignId() {
        // Given
        Expense newExpense = Expense.builder()
                .category(ExpenseCategory.LOISIRS)
                .amount(new BigDecimal("100.00"))
                .date(LocalDate.now())
                 .build();
        
        // When
        Expense result = repository.save(newExpense);
        
        // Then
        assertNotNull(result.getId());
        assertEquals(3L, result.getId()); // Should be the 4th element (index 3)
        assertEquals(4, repository.getAll().size());
    }

    @Test
    void testSave_ExistingExpense_ShouldUpdate() {
        // Given
        expense1.setAmount(new BigDecimal("75.00"));
        expense1.setDescription("Updated expense");
        
        // When
        Expense result = repository.save(expense1);
        
        // Then
        assertEquals(expense1.getId(), result.getId());
        assertEquals(new BigDecimal("75.00"), result.getAmount());
        assertEquals("Updated expense", result.getDescription());
        
        // Verify the update in the repository
        Optional<Expense> updated = repository.getById(expense1.getId());
        assertTrue(updated.isPresent());
        assertEquals(new BigDecimal("75.00"), updated.get().getAmount());
        assertEquals("Updated expense", updated.get().getDescription());
    }

    @Test
    void testDelete_ExistingId_ShouldRemoveExpense() {
        // Given
        Long id = expense2.getId();
        
        // When
        repository.delete(id);
        
        // Then
        assertEquals(2, repository.getAll().size());
        assertFalse(repository.getAll().contains(expense2));
    }

    @Test
    void testDelete_NonExistingId_ShouldNotFailAndNotModifyRepository() {
        // Given
        Long nonExistingId = 999L;
        int initialSize = repository.getAll().size();
        
        // When
        repository.delete(nonExistingId);
        
        // Then
        assertEquals(initialSize, repository.getAll().size());
    }
}
