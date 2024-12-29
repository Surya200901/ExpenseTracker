package com.expensetracker.service;

import com.expensetracker.model.Budget;
import com.expensetracker.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.time.YearMonth;
import java.util.Optional;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    // Get the current month's budget for a user
    public Optional<Budget> getBudgetForCurrentMonth(int userId) {
        String currentMonth = YearMonth.now().toString(); // 'YYYY-MM' format
        return budgetRepository.findByUserIdAndMonth(userId, currentMonth);
    }

    // Set the budget for the current month
    public Budget setBudget(int userId, double amount) {
        String currentMonth = YearMonth.now().toString(); // 'YYYY-MM' format
        Optional<Budget> existingBudget = budgetRepository.findByUserIdAndMonth(userId, currentMonth);

        Budget budget;
        if (existingBudget.isPresent()) {
            budget = existingBudget.get();
            budget.setAmount(amount);
        } else {
            budget = new Budget();
            budget.setUserId(userId);
            budget.setMonth(currentMonth);
            budget.setAmount(amount);
        }

        return budgetRepository.save(budget);
    }
}
