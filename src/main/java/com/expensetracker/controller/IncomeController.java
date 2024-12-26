package com.expensetracker.controller;

import com.expensetracker.model.Income;
import com.expensetracker.model.User;
import com.expensetracker.repository.IncomeRepository;
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
public class IncomeController {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/incomeform")
    public String addIncomeForm(Model model) {
        return "incomeform";  // This will return the income form view
    }

    @PostMapping("/addIncome")
    public String addIncome(@ModelAttribute Income income) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof org.springframework.security.core.userdetails.User) {
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
        } else {
            System.err.println("Principal is not a valid User instance.");
        }

        return "redirect:/dashboard";
    }

}
