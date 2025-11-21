package com.example.tracker.service;

import com.example.tracker.model.Budget;
import com.example.tracker.repository.BudgetRepository;
import org.springframework.stereotype.Service;

import java.time.YearMonth;

@Service
public class BudgetService {

    private final BudgetRepository repo;

    public BudgetService(BudgetRepository repo) {
        this.repo = repo;
    }

    private String currentMonthKey() {
        return YearMonth.now().toString(); // "2025-11"
    }

    public double getBudget() {
        return repo.findByMonthYear(currentMonthKey())
                .map(Budget::getAmount)
                .orElse(0.0);
    }

    public void updateBudget(double amt) {
        String key = currentMonthKey();
        Budget b = repo.findByMonthYear(key).orElse(new Budget(key, amt));
        b.setAmount(amt);
        repo.save(b);
    }
}
