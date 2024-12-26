package com.expensetracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboardPage")
public class DashboardPageController {

    @GetMapping
    public String dashboardPage() {
        return "dashboard"; // Maps to dashboard.html in templates or static folder.
    }
}

