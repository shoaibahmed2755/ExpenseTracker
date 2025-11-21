package com.example.tracker.service;

import com.example.tracker.model.Expense;
import com.example.tracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository repo;

    public ExpenseService(ExpenseRepository repo) {
        this.repo = repo;
    }

    public List<Expense> getAll() {
        return repo.findAll();
    }

    public void save(Expense expense) {
        repo.save(expense);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public double totalIncome() {
        return repo.findAll().stream()
                .filter(e -> "INCOME".equalsIgnoreCase(e.getType()))
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    public double totalExpense() {
        return repo.findAll().stream()
                .filter(e -> "EXPENSE".equalsIgnoreCase(e.getType()))
                .mapToDouble(Expense::getAmount)
                .sum();
    }
}
