package com.yohann.kata.expenses.infra.repository;

import com.yohann.kata.expenses.domain.model.Expense;
import com.yohann.kata.expenses.domain.model.ExpenseCategory;
import com.yohann.kata.expenses.domain.model.ExpenseFilter;
import com.yohann.kata.expenses.domain.port.out.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ExpenseRepositoryImpl implements ExpenseRepository {
    private final DBRepository repository;

    @Override
    public Expense create(Expense toCreate) {
        return repository.save(toCreate);
    }

    @Override
    public List<Expense> getAll() {
        return repository.getAll();
    }

    @Override
    public List<Expense> getAll(ExpenseFilter filter) {
        return repository.getAll(filter);
    }

    @Override
    public Optional<Expense> getById(Long id) {
        return repository.getById(id);
    }

    @Override
    public Expense update(Expense toUpdate) {
        return repository.save(toUpdate);
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }

    @Override
    public BigDecimal totalByCategory(ExpenseCategory category) {
        ExpenseFilter filter = ExpenseFilter.builder().category(category).build();
        return repository
                .getAll(filter)
                .stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
