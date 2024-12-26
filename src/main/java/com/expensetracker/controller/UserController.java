package com.expensetracker.controller;

import com.expensetracker.model.User;
import com.expensetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";  // Login page
    }

    @PostMapping("/login")
    public String loginUser(String username, String password, Model model) {
        Optional<User> userOpt = userRepository.findByUsername(username);  // Return Optional<User>

        if (!userOpt.isPresent() || !userOpt.get().getPassword().equals(password)) {
            model.addAttribute("error", "Invalid username or password.");
            return "login";  // Stay on the login page if authentication fails
        }

        return "redirect:/dashboard";  // Redirect to the dashboard after successful login
    }
}
