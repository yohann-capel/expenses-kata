package com.yohann.kata.expenses.domain.port.out;

import com.yohann.kata.expenses.domain.model.Expense;
import com.yohann.kata.expenses.domain.model.ExpenseCategory;
import com.yohann.kata.expenses.domain.model.ExpenseFilter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository {
    Expense create(Expense toCreate);
    List<Expense> getAll();
    List<Expense> getAll(ExpenseFilter filter);
    Optional<Expense> getById(Long id);
    Expense update(Expense toUpdate);
    void delete(Long id);
    BigDecimal totalByCategory(ExpenseCategory category);
}
