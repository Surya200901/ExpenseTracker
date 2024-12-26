package com.expensetracker.service;

import com.expensetracker.model.Expense;
import com.expensetracker.model.Income;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Map<String, Object> getDashboardOverview() {
        double totalIncome = incomeRepository.getTotalIncome();
        double totalExpenses = expenseRepository.getTotalExpenses();
        double totalSavings = totalIncome - totalExpenses;

        Map<String, Object> overview = new HashMap<>();
        overview.put("totalIncome", totalIncome);
        overview.put("totalExpenses", totalExpenses);
        overview.put("totalSavings", totalSavings);

        return overview;
    }

    public Map<String, Object> getRecentTransactions() {
        List<Income> recentIncome = incomeRepository.findTop5ByOrderByDateDesc();
        List<Expense> recentExpenses = expenseRepository.findTop5ByOrderByDateDesc();

        Map<String, Object> recentTransactions = new HashMap<>();
        recentTransactions.put("recentIncome", recentIncome);
        recentTransactions.put("recentExpenses", recentExpenses);

        return recentTransactions;
    }

    public List<Map<String, Object>> getSpendingAnalysis() {
        List<Object[]> results = expenseRepository.getCategoryWiseSpending();
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
