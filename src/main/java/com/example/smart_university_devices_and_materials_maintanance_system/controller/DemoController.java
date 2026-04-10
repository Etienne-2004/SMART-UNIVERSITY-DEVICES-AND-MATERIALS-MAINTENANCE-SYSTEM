package com.example.smart_university_devices_and_materials_maintanance_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Demo Controller - Provides demo page for testing all user roles
 */
@Controller
public class DemoController {

    /**
     * Demo page showing all available test users and their roles
     */
    @GetMapping("/demo")
    public String demo(Model model) {
        return "demo";
    }

    @GetMapping("/test")
    public String test(Model model) {
        return "test";
    }
}
