package com.wiloso.admin.controller;

import com.wiloso.admin.model.User;
import com.wiloso.admin.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/users")
public class UserManagementController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public String listUsers(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            HttpSession session,
            Model model
    ) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Page<User> users = userService.getUsers(keyword, page, size);
        model.addAttribute("users", users.getContent());
        model.addAttribute("currentPage", page);

        int totalPages = users.getTotalPages();
        if (totalPages == 0) {
            totalPages = 1;
        }
        model.addAttribute("totalPages", totalPages);

        model.addAttribute("keyword", keyword);
        model.addAttribute("canEdit", userService.isDirektur(currentUser));

        return "user-management";
    }
}
