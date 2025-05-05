package com.yohann.kata.expenses;

import com.yohann.kata.expenses.domain.model.ExpenseCategory;
import com.yohann.kata.expenses.domain.model.ExpenseFilter;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseFilterTest {

    @Test
    void shouldReturnTrueIfFilterHasCategory() {
        ExpenseFilter filter = ExpenseFilter.builder()
                .category(ExpenseCategory.ALIMENTATION)
                .build();

        assertTrue(filter.hasCategory());
    }

    @Test
    void shouldReturnFalseIfFilterHasCategory() {
        ExpenseFilter filter = ExpenseFilter.builder().build();

        assertFalse(filter.hasCategory());
    }

    @Test
    void shouldReturnFalseIfFilterHasDateRange() {
        ExpenseFilter filter = ExpenseFilter.builder()
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .build();

        assertTrue(filter.hasDateRange());
    }

    @Test
    void shouldReturnFalseIfFilterHasOnlyStartDate() {
        ExpenseFilter filter = ExpenseFilter.builder()
                .startDate(LocalDate.of(2024, 1, 1))
                .build();

        assertFalse(filter.hasDateRange());
    }

    @Test
    void shouldReturnFalseIfFilterHasOnlyEndDate() {
        ExpenseFilter filter = ExpenseFilter.builder()
                .endDate(LocalDate.of(2024, 12, 31))
                .build();

        assertFalse(filter.hasDateRange());
    }

    @Test
    void shouldReturnFalseWhenBothDateAreNull() {
        ExpenseFilter filter = ExpenseFilter.builder().build();

        assertFalse(filter.hasDateRange());
    }

    @Test
    void shouldReturnFilterEmpty() {
        ExpenseFilter emptyFilter = ExpenseFilter.empty();

        assertFalse(emptyFilter.hasCategory());
        assertFalse(emptyFilter.hasDateRange());
        assertNull(emptyFilter.getCategory());
        assertNull(emptyFilter.getStartDate());
        assertNull(emptyFilter.getEndDate());
    }
}
