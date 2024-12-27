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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
                List<Income> recentIncomes = incomeRepository.findTop5ByUserOrderByDateDesc(user); // Add filtering by user
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
                model.addAttribute("categorySpending", categorySpending);

                // Add all incomes and expenses for detailed tables
                model.addAttribute("incomes", incomes);
                model.addAttribute("expenses", expenses);
            }
        }

        return "dashboard"; // Render dashboard.html
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
