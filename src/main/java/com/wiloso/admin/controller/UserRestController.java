package com.wiloso.admin.controller;

import com.wiloso.admin.model.User;
import com.wiloso.admin.model.Vendor;
import com.wiloso.admin.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public ResponseEntity<?> addUserAjax(
            @Validated @RequestBody User user,
            BindingResult bindingResult,
            HttpSession session
    ) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        if (!userService.isDirektur(currentUser)) {
            return ResponseEntity.status(403).body("Forbidden: hanya Direktur yang bisa menambah user");
        }

        if (user.getPassword() == null || user.getPassword().length() < 6) {
            bindingResult.rejectValue("password", "error.user", "Password minimal 6 karakter");
        }

        if (userService.emailExists(user.getEmail())) {
            bindingResult.rejectValue("email", "error.user", "Email sudah digunakan");
        }

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        userService.saveUser(user);
        return ResponseEntity.ok("User berhasil ditambahkan");
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editUserAjax(
            @PathVariable Long id,
            @RequestBody User updatedUser,
            BindingResult bindingResult,
            HttpSession session
    ) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        if (!userService.isDirektur(currentUser)) {
            return ResponseEntity.status(403).body("Forbidden: hanya Direktur yang bisa mengedit user");
        }

        Optional<User> existingUserOptional = userService.getUserById(id);
        if (existingUserOptional.isEmpty()) {
            return ResponseEntity.status(404).body("User tidak ditemukan");
        }

        User existingUser = existingUserOptional.get();

        existingUser.setName(updatedUser.getName());
        existingUser.setRole(updatedUser.getRole());
        existingUser.setCreatedTime(LocalDateTime.now());

        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equalsIgnoreCase(existingUser.getEmail())) {
            if (userService.emailExists(updatedUser.getEmail())) {
                bindingResult.rejectValue("email", "error.user", "Email sudah digunakan");
            } else {
                existingUser.setEmail(updatedUser.getEmail());
            }
        } else if (updatedUser.getEmail() == null || updatedUser.getEmail().isBlank()) {
            bindingResult.rejectValue("email", "error.user", "Email wajib diisi");
        }

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        userService.saveUser(existingUser);
        return ResponseEntity.ok("User berhasil diperbarui");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(404).body("Vendor tidak ditemukan");
        }
    }

}
