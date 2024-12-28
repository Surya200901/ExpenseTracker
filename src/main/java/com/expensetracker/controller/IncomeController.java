package com.expensetracker.controller;

import com.expensetracker.model.Income;
import com.expensetracker.model.User;
import com.expensetracker.repository.IncomeRepository;
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
public class IncomeController {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/incomeform")
    public String addIncomeForm(Model model) {
        // Fetch the logged-in user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((org.springframework.security.core.userdetails.User) principal).getUsername();

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // Fetch all income entries for the logged-in user
            List<Income> incomeList = incomeRepository.findAllByUser(user);
            model.addAttribute("incomes", incomeList); // Updated attribute name to "incomes"
        } else {
            System.err.println("User not found in the database.");
        }
        return "incomeform";  // This will return the income form view
    }

    @PostMapping("/addIncome")
    public String addIncome(@ModelAttribute Income income) {
        // Fetch the logged-in user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((org.springframework.security.core.userdetails.User) principal).getUsername();

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            income.setUser(user);  // Set the User object directly
            income.setDate(LocalDate.now()); // Ensure date is set
            incomeRepository.save(income);  // Save the Income object
        } else {
            System.err.println("User not found in the database.");
        }

        return "redirect:/incomeform";  // Redirect back to the income form page to see the updated list
    }

    @GetMapping("/editIncome/{id}")
    public String editIncomeForm(@PathVariable("id") Long id, Model model) {
        Optional<Income> income = incomeRepository.findById(id);
        if (income.isPresent()) {
            model.addAttribute("income", income.get());
            return "editIncomeForm";  // This will return the edit income form view
        } else {
            return "redirect:/incomeform";
        }
    }

    @PostMapping("/updateIncome")
    public String updateIncome(@ModelAttribute Income income) {
        // Fetch the existing income by ID
        Optional<Income> existingIncome = incomeRepository.findById(income.getId());
        if (existingIncome.isPresent()) {
            Income updatedIncome = existingIncome.get();
            updatedIncome.setSource(income.getSource());
            updatedIncome.setAmount(income.getAmount());
            updatedIncome.setDate(income.getDate());
            incomeRepository.save(updatedIncome); // Save updated details
        } else {
            System.err.println("Income record not found for ID: " + income.getId());
        }
        return "redirect:/incomeform";
    }

    @GetMapping("/deleteIncome/{id}")
    public String deleteIncome(@PathVariable("id") Long id) {
        incomeRepository.deleteById(id);  // Delete the income entry by ID
        return "redirect:/incomeform";
    }
}
