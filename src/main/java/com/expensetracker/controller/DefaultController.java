package com.expensetracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {
	
	@GetMapping("/")
    public String showIndex() {
        return "index"; // Serve the index.html page
    }
	
	@GetMapping("/index")
    public String indexPage() {
        return "index"; // Maps to `src/main/resources/templates/index.html` if using Thymeleaf.
    }
	
	@GetMapping("/default-register")
	public String defaultRegisterRedirect() {
	    return "redirect:/register";
	}
    
}
