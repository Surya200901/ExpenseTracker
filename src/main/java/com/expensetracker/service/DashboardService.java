package com.expensetracker.service;

import com.expensetracker.model.Expense;
import com.expensetracker.model.Income;
import com.expensetracker.model.User;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.IncomeRepository;
import com.expensetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private UserRepository userRepository;

    private User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return userRepository.findByUsername(username).orElse(null);
        }
        return null;
    }

    public Map<String, Object> getDashboardOverview() {
        User user = getAuthenticatedUser();
        if (user == null) {
            throw new RuntimeException("User not authenticated");
        }

        double totalIncome = incomeRepository.getTotalIncome(user);
        double totalExpenses = expenseRepository.getTotalExpenses(user);
        double totalSavings = totalIncome - totalExpenses;

        Map<String, Object> overview = new HashMap<>();
        overview.put("totalIncome", totalIncome);
        overview.put("totalExpenses", totalExpenses);
        overview.put("totalSavings", totalSavings);

        return overview;
    }

    public Map<String, Object> getRecentTransactions() {
        User user = getAuthenticatedUser();
        if (user == null) {
            throw new RuntimeException("User not authenticated");
        }

        List<Income> recentIncome = incomeRepository.findTop5ByUserOrderByDateDesc(user);
        List<Expense> recentExpenses = expenseRepository.findTop5ByUserOrderByDateDesc(user);

        Map<String, Object> recentTransactions = new HashMap<>();
        recentTransactions.put("recentIncome", recentIncome);
        recentTransactions.put("recentExpenses", recentExpenses);

        return recentTransactions;
    }

    public List<Map<String, Object>> getSpendingAnalysis() {
        User user = getAuthenticatedUser();
        if (user == null) {
            throw new RuntimeException("User not authenticated");
        }

        List<Object[]> results = expenseRepository.getCategoryWiseSpendingByUser(user);
        List<Map<String, Object>> spendingAnalysis = new ArrayList<>();

        for (Object[] result : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("category", result[0]);
            map.put("amount", result[1]);
            spendingAnalysis.add(map);
        }

        return spendingAnalysis;
    }
}
