package com.example.smart_university_devices_and_materials_maintanance_system.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            model.addAttribute("statusCode", statusCode);
            
            if (statusCode == 404) {
                model.addAttribute("errorMessage", "Page not found");
            } else if (statusCode == 500) {
                model.addAttribute("errorMessage", "Internal server error - please try again");
            } else {
                model.addAttribute("errorMessage", "An error occurred");
            }
        }
        
        return "error";
    }
}
