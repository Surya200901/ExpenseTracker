package com.expensetracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class LoginController {
    @GetMapping("/login-page")
    public String loginPage() {
        return "login"; // Corresponds to login.html in templates folder
    }
}
