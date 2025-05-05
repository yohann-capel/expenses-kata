package com.yohann.kata.expenses.domain.service;

import com.yohann.kata.expenses.domain.model.Expense;
import com.yohann.kata.expenses.domain.model.ExpenseCategory;
import com.yohann.kata.expenses.domain.model.ExpenseFilter;
import com.yohann.kata.expenses.domain.port.in.ExpenseUseCase;
import com.yohann.kata.expenses.domain.port.out.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseUseCase {
    private final ExpenseRepository repository;

    @Override
    public Expense createExpense(Expense toCreate) {
        verifyData(toCreate);
        return repository.create(toCreate);
    }

    @Override
    public List<Expense> getAll() {
        return repository.getAll();
    }

    @Override
    public List<Expense> getFilteredList(ExpenseFilter filter) {
        return repository.getAll(verifyFilter(filter));
    }

    @Override
    public Expense getById(Long id) {
        return repository.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID not found"));
    }

    @Override
    public Expense updateExpense(Expense toUpdate) {
        verifyData(toUpdate);
        return repository.update(toUpdate);
    }

    @Override
    public void deleteExpense(Long id) {
        repository.delete(id);
    }

    @Override
    public BigDecimal amountByCategory(ExpenseCategory category) {
        return repository.totalByCategory(category);
    }

    private void verifyData(Expense toVerify) {
        if(toVerify.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount under zero");
        }

        if(toVerify.getCategory() == null) {
            throw new IllegalArgumentException("No category");
        }

        if(toVerify.getDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date after today");
        }
    }

    private ExpenseFilter verifyFilter(ExpenseFilter toVerify) {
        if (toVerify == null) {
            toVerify = ExpenseFilter.empty();
        }

        if (toVerify.hasDateRange()) {
            LocalDate now = LocalDate.now();

            if (toVerify.getStartDate() != null && toVerify.getEndDate() != null &&
                    toVerify.getStartDate().isAfter(toVerify.getEndDate())) {
                throw new IllegalArgumentException("Start date can't be after end date");
            }

//            if (toVerify.getEndDate() != null && toVerify.getEndDate().isAfter(now)) {
//                throw new IllegalArgumentException("End date can't be in the future");
//            }
        }

        return toVerify;
    }
}
