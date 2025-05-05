package com.yohann.kata.expenses.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class Expense {
    private Long id;
    private BigDecimal amount;
    private ExpenseCategory category;
    private LocalDate date;
    private String description = null;
}
