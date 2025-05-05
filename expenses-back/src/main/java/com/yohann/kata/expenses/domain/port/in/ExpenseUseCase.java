package com.yohann.kata.expenses.domain.port.in;

import com.yohann.kata.expenses.domain.model.Expense;
import com.yohann.kata.expenses.domain.model.ExpenseCategory;
import com.yohann.kata.expenses.domain.model.ExpenseFilter;

import java.math.BigDecimal;
import java.util.List;

public interface ExpenseUseCase {
    Expense createExpense(Expense toCreate);
    List<Expense> getAll();
    List<Expense> getFilteredList(ExpenseFilter filter);
    Expense getById(Long id);
    Expense updateExpense(Expense toUpdate);
    void deleteExpense(Long id);
    BigDecimal amountByCategory(ExpenseCategory filter);
}
