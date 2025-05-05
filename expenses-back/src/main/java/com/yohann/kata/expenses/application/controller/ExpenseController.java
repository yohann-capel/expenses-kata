package com.yohann.kata.expenses.application.controller;

import com.yohann.kata.expenses.domain.model.Expense;
import com.yohann.kata.expenses.domain.model.ExpenseCategory;
import com.yohann.kata.expenses.domain.model.ExpenseFilter;
import com.yohann.kata.expenses.domain.port.in.ExpenseUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.flogger.Flogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
@Slf4j
public class ExpenseController {
    private final ExpenseUseCase service;

    @GetMapping
    public ResponseEntity<List<Expense>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/filtered")
    public ResponseEntity<List<Expense>> getAllFiltered(ExpenseFilter filter) {
        return ResponseEntity.ok(service.getFilteredList(filter));
    }

    @GetMapping("/amount/{pathCategory}")
    public ResponseEntity<BigDecimal> getAmountPerCategory(@PathVariable String pathCategory) {
        ExpenseCategory category = ExpenseCategory.valueOf(pathCategory.toUpperCase());
        return ResponseEntity.ok(service.amountByCategory(category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Expense> getById(@PathVariable Long id) {
        Expense result = service.getById(id);
        if(result == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Expense> create(@RequestBody Expense requestExpense) {
        Expense expense = service.createExpense(requestExpense);
        URI resource = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(expense.getId()).toUri();
        return ResponseEntity.created(resource).body(expense);
    }

    @PutMapping
    public ResponseEntity<Expense> update(@RequestBody Expense expense) {
        return ResponseEntity.ok(service.updateExpense(expense));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
