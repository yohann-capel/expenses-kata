package com.yohann.kata.expenses.infra.repository;

import com.yohann.kata.expenses.domain.model.Expense;
import com.yohann.kata.expenses.domain.model.ExpenseFilter;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import java.util.*;

@Component
public class DBRepository {
    List<Expense> expenses = new ArrayList<>();

    public List<Expense> getAll() {
        return getAll(null);
    }

    public List<Expense> getAll(ExpenseFilter filter) {
        if(filter == null) {
            return expenses;
        }

        return expenses
                .stream()
                .filter(expense ->
                        filter.getCategory() == null || expense.getCategory().equals(filter.getCategory())
                )
                .filter(expense ->
                        filter.getStartDate() == null
                                || expense.getDate().isAfter(filter.getStartDate())
                                || expense.getDate().isEqual(filter.getStartDate())
                )
                .filter(expense ->
                        filter.getEndDate() == null
                                || expense.getDate().isBefore(filter.getEndDate())
                                || expense.getDate().isEqual(filter.getEndDate())
                )
                .sorted((a, b) -> Long.compare(a.getId(), b.getId()))
                .toList();
    }

    public Optional<Expense> getById(Long id) {
        return expenses.stream().filter(expense -> expense.getId().equals(id)).findFirst();
    }

    public Expense save(Expense expense) {
        if(expense.getId() == null)
            return create(expense);

        return update(expense);
    }

    public void delete(Long id) {
        Expense toIndex = expenses.stream().filter(expense -> expense.getId().equals(id)).findFirst().orElse(null);
        if(toIndex == null) return;

        expenses.remove(toIndex);
    }

    private Expense create(Expense expense) {
        Long id = (expenses.isEmpty()) ? 0L : expenses.getLast().getId() + 1;

        expense.setId(id);
        expenses.add(expense);

        return expense;
    }

    private Expense update(Expense toUpdate) {
        Expense test = expenses.stream().filter(expense -> expense.getId().equals(toUpdate.getId())).findFirst().orElse(null);
        if(test == null)
            return create(toUpdate);

        delete(test.getId());
        expenses.add(toUpdate);

        return toUpdate;
    }
}
