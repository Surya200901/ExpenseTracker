package com.expensetracker.controller;

import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class ExpenseController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    // Show the expense form along with the user's existing expenses
    @RequestMapping("/expenseform")
    public String addExpenseForm(Model model) {
        // Fetch the logged-in user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((org.springframework.security.core.userdetails.User) principal).getUsername();

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // Fetch all expense entries for the logged-in user
            List<Expense> expenseList = expenseRepository.findAllByUser(user);
            model.addAttribute("expenses", expenseList); // Added the list of expenses
        } else {
            System.err.println("User not found in the database.");
        }

        return "expenseform";  // This will return the expense form view
    }

    // Add an expense
    @PostMapping("/addExpense")
    public String addExpense(@ModelAttribute Expense expense) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof org.springframework.security.core.userdetails.User) {
            String username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
            Optional<User> optionalUser = userRepository.findByUsername(username);

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                expense.setUser(user);  // Set the logged-in user

                if (expense.getDate() == null) {
                    expense.setDate(LocalDate.now()); // Set the date if it's null
                }

                expenseRepository.save(expense); // Save the expense to the database
            } else {
                System.err.println("User not found in the database.");
            }
        } else {
            System.err.println("Principal is not a valid User instance.");
        }

        return "redirect:/expenseform";  // Redirect back to the expense form page to see the updated list
    }

    // Edit an existing expense
    @GetMapping("/editExpense/{id}")
    public String editExpenseForm(@PathVariable("id") Long id, Model model) {
        Optional<Expense> expense = expenseRepository.findById(id);
        if (expense.isPresent()) {
            model.addAttribute("expense", expense.get());
            return "editExpenseForm";  // This will return the edit expense form view
        } else {
            return "redirect:/expenseform";
        }
    }

    // Update an expense
    @PostMapping("/updateExpense")
    public String updateExpense(@ModelAttribute Expense expense) {
        Optional<Expense> existingExpense = expenseRepository.findById(expense.getId());
        if (existingExpense.isPresent()) {
            Expense updatedExpense = existingExpense.get();
            updatedExpense.setCategory(expense.getCategory());  // Set category
            updatedExpense.setAmount(expense.getAmount());      // Set amount
            updatedExpense.setDate(expense.getDate());          // Set date
            expenseRepository.save(updatedExpense); // Save the updated expense
        } else {
            System.err.println("Expense record not found for ID: " + expense.getId());
        }
        return "redirect:/expenseform";
    }

    // Delete an expense
    @GetMapping("/deleteExpense/{id}")
    public String deleteExpense(@PathVariable("id") Long id) {
        expenseRepository.deleteById(id);  // Delete the expense entry by ID
        return "redirect:/expenseform";
    }
}
