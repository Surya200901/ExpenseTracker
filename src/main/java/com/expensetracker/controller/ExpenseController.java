package com.expensetracker.controller;

import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/expenseform")
    public String addExpenseForm(Model model) {
        return "expenseform";  // This will return the expense form view
    }

    @PostMapping("/addExpense")
    public String addExpense(@ModelAttribute Expense expense) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof org.springframework.security.core.userdetails.User) {
            String username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
            Optional<User> optionalUser = userRepository.findByUsername(username);

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                expense.setUser(user);

                if (expense.getDate() == null) {
                    expense.setDate(LocalDate.now()); // Default date if none is provided
                }

                expenseRepository.save(expense); // Save expense to the DB
            } else {
                System.err.println("User not found in the database.");
            }
        } else {
            System.err.println("Principal is not a valid User instance.");
        }

        return "redirect:/dashboard"; // Redirect to the dashboard after saving
    }

}
