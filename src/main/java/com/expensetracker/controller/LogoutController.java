package com.expensetracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutController {

    @GetMapping("/logout")
    public String logout() {
        // Custom logout logic (optional)
        return "redirect:/";  // Redirects to index.html after logout
    }
}