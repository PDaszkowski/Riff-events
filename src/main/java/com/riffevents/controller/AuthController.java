package com.riffevents.controller;

import com.riffevents.model.User;
import com.riffevents.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
class AuthController {

    @Autowired
    private UserService userService;


    @GetMapping("/register")
    public String registerForm(Model model){
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String name,
            RedirectAttributes redirectAttributes)
    {
        try {
            userService.registerUser(email, password, name);
            redirectAttributes.addFlashAttribute("success", "Registration Successful. You can log in");
            return "redirect:/auth/login";
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/auth/register";
        }
    }

    @GetMapping("/login")
    public String loginForm(){
        return "auth/login";
    }



}
