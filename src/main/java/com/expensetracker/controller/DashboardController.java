package com.expensetracker.controller;

import com.expensetracker.model.Expense;
import com.expensetracker.model.Income;
import com.expensetracker.model.User;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.IncomeRepository;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @GetMapping
    public String dashboard(Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof org.springframework.security.core.userdetails.User) {
            String username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
            Optional<User> optionalUser = userRepository.findByUsername(username);

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();

                // Total Income
                List<Income> incomes = incomeRepository.findAllByUser(user);
                double totalIncome = incomes.stream().mapToDouble(Income::getAmount).sum();
                model.addAttribute("totalIncome", totalIncome);

                // Total Expenses
                List<Expense> expenses = expenseRepository.findAllByUser(user);
                double totalExpenses = expenses.stream().mapToDouble(Expense::getAmount).sum();
                model.addAttribute("totalExpenses", totalExpenses);

                // Total Savings
                double totalSavings = totalIncome - totalExpenses;
                model.addAttribute("totalSavings", totalSavings);

                // Recent Transactions (last 5 transactions)
                List<Expense> recentExpenses = expenseRepository.findTop5ByUserOrderByDateDesc(user);
                List<Income> recentIncomes = incomeRepository.findTop5ByUserOrderByDateDesc(user);
                List<Object> recentTransactions = new ArrayList<>();
                recentTransactions.addAll(recentExpenses);
                recentTransactions.addAll(recentIncomes);
                recentTransactions.sort(Comparator.comparing(transaction -> {
                    if (transaction instanceof Income) {
                        return ((Income) transaction).getDate();
                    } else {
                        return ((Expense) transaction).getDate();
                    }
                }).reversed());
                model.addAttribute("recentTransactions", recentTransactions);

                // Category-wise Spending
                List<Object[]> categorySpending = expenseRepository.getCategoryWiseSpendingByUser(user);
                List<Map<String, Object>> categorySpendingData = categorySpending.stream()
                        .map(cs -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("category", cs[0]);
                            map.put("amount", cs[1]);
                            return map;
                        })
                        .collect(Collectors.toList());
                model.addAttribute("categorySpendingJson", new Gson().toJson(categorySpendingData));

                // Income and Expense Trends (last 6 months)
                List<Map<String, Object>> incomeTrends = calculateMonthlyTrends(incomes);
                List<Map<String, Object>> expenseTrends = calculateMonthlyTrends(expenses);
                model.addAttribute("incomeTrendsJson", new Gson().toJson(incomeTrends));
                model.addAttribute("expenseTrendsJson", new Gson().toJson(expenseTrends));

                // Add all incomes and expenses for detailed tables
                model.addAttribute("incomes", incomes);
                model.addAttribute("expenses", expenses);
            }
        }

        return "dashboard"; // Render dashboard.html
    }

    private List<Map<String, Object>> calculateMonthlyTrends(List<?> transactions) {
        Map<String, Double> monthlyData = new TreeMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");

        transactions.forEach(transaction -> {
            LocalDate date;
            double amount;
            if (transaction instanceof Income) {
                date = ((Income) transaction).getDate(); // Ensure `Income` uses `LocalDate`
                amount = ((Income) transaction).getAmount();
            } else {
                date = ((Expense) transaction).getDate(); // Ensure `Expense` uses `LocalDate`
                amount = ((Expense) transaction).getAmount();
            }
            String month = date.format(formatter);
            monthlyData.put(month, monthlyData.getOrDefault(month, 0.0) + amount);
        });

        return monthlyData.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("month", entry.getKey());
                    map.put("amount", entry.getValue());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/overview")
    public Map<String, Object> getDashboardOverview() {
        return dashboardService.getDashboardOverview();
    }

    @GetMapping("/recent-transactions")
    public Map<String, Object> getRecentTransactions() {
        return dashboardService.getRecentTransactions();
    }

    @GetMapping("/spending-analysis")
    public List<Map<String, Object>> getSpendingAnalysis() {
        return dashboardService.getSpendingAnalysis();
    }
}
