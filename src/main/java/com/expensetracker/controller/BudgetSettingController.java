package com.expensetracker.controller;

import com.expensetracker.model.Budget;
import com.expensetracker.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BudgetSettingController {

    @Autowired
    private BudgetService budgetService;

    // Show current budget for the user
    @GetMapping("/budgetsetting")
    public String getBudgetSetting(Model model) {
        int userId = 1; // Replace this with the actual user ID from the session or authentication context
        budgetService.getBudgetForCurrentMonth(userId).ifPresent(budget -> {
            model.addAttribute("currentBudget", budget.getAmount());
        });
        return "budgetsetting";
    }

    // Set the budget for the user
    @PostMapping("/budgetsetting")
    public String setBudget(@RequestParam double budget, Model model) {
        int userId = 1; // Replace this with the actual user ID from the session or authentication context
        Budget updatedBudget = budgetService.setBudget(userId, budget);
        model.addAttribute("currentBudget", updatedBudget.getAmount());
        return "budgetsetting";
    }
}
