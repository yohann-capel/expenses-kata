package com.yohann.kata.expenses.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ExpenseFilter {
    private ExpenseCategory category;
    private LocalDate startDate;
    private LocalDate endDate;

    public boolean hasCategory() {
        return category != null;
    }

    public boolean hasDateRange() {
        return startDate != null && endDate != null;
    }

    public static ExpenseFilter empty() {
        return ExpenseFilter.builder().build();
    }
}
