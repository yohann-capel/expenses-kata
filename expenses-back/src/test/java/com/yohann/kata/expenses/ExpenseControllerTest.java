package com.yohann.kata.expenses;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yohann.kata.expenses.application.controller.ExpenseController;
import com.yohann.kata.expenses.domain.model.Expense;
import com.yohann.kata.expenses.domain.model.ExpenseCategory;
import com.yohann.kata.expenses.domain.model.ExpenseFilter;
import com.yohann.kata.expenses.domain.port.in.ExpenseUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private ExpenseUseCase expenseUseCase;


    private Expense testExpense;
    private List<Expense> expenseList;

    private String buildUrl(String path) {
        return "/expenses" + path;
    }

    @BeforeEach
    void setUp() {
        testExpense = new Expense(1L, BigDecimal.valueOf(100), ExpenseCategory.ALIMENTATION, LocalDate.now(), "Test Expense");
        expenseList = Arrays.asList(
                testExpense,
                new Expense(2L, BigDecimal.valueOf(50), ExpenseCategory.TRANSPORTS, LocalDate.now(), "Another Expense")
        );
    }

    @Test
    void getAll_ShouldReturnAllExpenses() throws Exception {
        when(expenseUseCase.getAll()).thenReturn(expenseList);

        mockMvc.perform(get(buildUrl("")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Test Expense")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].description", is("Another Expense")));

        verify(expenseUseCase, times(1)).getAll();
    }

    @Test
    void getAllFiltered_ShouldReturnFilteredExpenses() throws Exception {

        when(expenseUseCase.getFilteredList(any(ExpenseFilter.class))).thenReturn(List.of(testExpense));

        mockMvc.perform(get(buildUrl("/filtered?category=ALIMENTATION")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Test Expense")))
                .andExpect(jsonPath("$[0].category", is("ALIMENTATION")));

        verify(expenseUseCase, times(1)).getFilteredList(any(ExpenseFilter.class));
    }

    @Test
    void getAmountPerCategory_ShouldReturnAmount() throws Exception {
        BigDecimal amount = BigDecimal.valueOf(150);
        when(expenseUseCase.amountByCategory(ExpenseCategory.ALIMENTATION)).thenReturn(amount);

        mockMvc.perform(get(buildUrl("/amount/ALIMENTATION")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(150)));

        verify(expenseUseCase, times(1)).amountByCategory(ExpenseCategory.ALIMENTATION);
    }

    @Test
    void getAmountPerCategory_WithLowercaseCategory_ShouldReturnAmount() throws Exception {
        BigDecimal amount = BigDecimal.valueOf(150);
        when(expenseUseCase.amountByCategory(ExpenseCategory.ALIMENTATION)).thenReturn(amount);

        mockMvc.perform(get(buildUrl("/amount/ALIMENTATION")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(150)));

        verify(expenseUseCase, times(1)).amountByCategory(ExpenseCategory.ALIMENTATION);
    }

    @Test
    void getAmountPerCategory_WithInvalidCategory_ShouldThrowException() throws Exception {
        mockMvc.perform(get(buildUrl("/amount/INVALID_CATEGORY")))
                .andExpect(status().isBadRequest());  // Assuming your RestControllerAdvice returns 400 for IllegalArgumentException

        verify(expenseUseCase, never()).amountByCategory(any());
    }

    @Test
    void getById_WhenExpenseExists_ShouldReturnExpense() throws Exception {
        when(expenseUseCase.getById(1L)).thenReturn(testExpense);

        mockMvc.perform(get(buildUrl("/1")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Test Expense")))
                .andExpect(jsonPath("$.amount", is(100)))
                .andExpect(jsonPath("$.category", is("ALIMENTATION")));

        verify(expenseUseCase, times(1)).getById(1L);
    }

    @Test
    void getById_WhenExpenseDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(expenseUseCase.getById(999L)).thenReturn(null);

        mockMvc.perform(get(buildUrl("/999")))
                .andExpect(status().isNotFound());

        verify(expenseUseCase, times(1)).getById(999L);
    }

    @Test
    void create_ShouldReturnCreatedExpense() throws Exception {
        Expense newExpense = new Expense(null, BigDecimal.valueOf(75), ExpenseCategory.LOISIRS, LocalDate.now(), "New Expense");
        Expense savedExpense = new Expense(3L, BigDecimal.valueOf(75), ExpenseCategory.LOISIRS, LocalDate.now(), "New Expense");

        when(expenseUseCase.createExpense(any(Expense.class))).thenReturn(savedExpense);

        mockMvc.perform(post(buildUrl(""))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newExpense)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/3")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.description", is("New Expense")))
                .andExpect(jsonPath("$.amount", is(75)))
                .andExpect(jsonPath("$.category", is("LOISIRS")));

        verify(expenseUseCase, times(1)).createExpense(any(Expense.class));
    }

    @Test
    void update_ShouldReturnUpdatedExpense() throws Exception {
        Expense updatedExpense = new Expense(1L, BigDecimal.valueOf(120), ExpenseCategory.ALIMENTATION, LocalDate.now(), "Updated Expense");

        when(expenseUseCase.updateExpense(any(Expense.class))).thenReturn(updatedExpense);

        mockMvc.perform(put(buildUrl(""))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedExpense)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Updated Expense")))
                .andExpect(jsonPath("$.amount", is(120)))
                .andExpect(jsonPath("$.category", is("ALIMENTATION")));

        verify(expenseUseCase, times(1)).updateExpense(any(Expense.class));
    }

    @Test
    void delete_ShouldReturnNoContent() throws Exception {
        doNothing().when(expenseUseCase).deleteExpense(1L);

        mockMvc.perform(delete(buildUrl("/1")))
                .andExpect(status().isNoContent());

        verify(expenseUseCase, times(1)).deleteExpense(1L);
    }
}