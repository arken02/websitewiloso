package com.wiloso.admin.controller;

import com.wiloso.admin.model.User;
import com.wiloso.admin.repository.UserRepository;
import com.wiloso.admin.service.UserService;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String nama,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(defaultValue = "Staff") String role,
            Model model
    ) {
        if (userService.emailExists(email)) {
            model.addAttribute("message", "Email sudah digunakan.");
            return "register";
        }

        userService.registerUser(nama, email, password, role);
        model.addAttribute("message", "Registrasi berhasil. Silakan login.");
        return "login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {
        if (userService.validateLogin(email, password)) {
            session.setAttribute("user", userService.findByEmail(email).get());
            return "redirect:/dashboard";
        }
        model.addAttribute("message", "Email atau password salah.");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        model.addAttribute("nama", user.getName());
        return "dashboard";
    }
}

