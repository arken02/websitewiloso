package com.wiloso.admin.controller;

import com.wiloso.admin.model.User;
import com.wiloso.admin.model.Vendor;
import com.wiloso.admin.service.VendorService;
import com.wiloso.admin.service.UserService; // Untuk memeriksa peran user
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/vendors")
public class VendorRestController {

    @Autowired
    private VendorService vendorService;

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public ResponseEntity<?> addVendorAjax(
            @RequestBody Vendor vendor,
            BindingResult bindingResult,
            HttpSession session
    ) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        if (!userService.isDirektur(currentUser)) {
            return ResponseEntity.status(403).body("Forbidden: hanya Direktur yang bisa menambah vendor");
        }

        if (vendor.getName() == null || vendor.getName().isBlank()) {
            bindingResult.rejectValue("name", "error.vendor", "Nama vendor wajib diisi");
        }
        if (vendor.getEmail() == null || vendor.getEmail().isBlank()) {
            bindingResult.rejectValue("email", "error.vendor", "Email vendor wajib diisi");
        } else if (vendorService.emailExists(vendor.getEmail())) {
            bindingResult.rejectValue("email", "error.vendor", "Email vendor sudah digunakan");
        }

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        vendorService.saveVendor(vendor);
        return ResponseEntity.ok("Vendor berhasil ditambahkan");
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editVendorAjax(
            @PathVariable Long id,
            @RequestBody Vendor vendor,
            BindingResult bindingResult,
            HttpSession session
    ) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        if (!userService.isDirektur(currentUser)) {
            return ResponseEntity.status(403).body("Forbidden: hanya Direktur yang bisa mengedit vendor");
        }

        Optional<Vendor> existingVendorOptional = vendorService.getVendorById(id);
        if (existingVendorOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Vendor tidak ditemukan");
        }

        Vendor existingVendor = existingVendorOptional.get();

        existingVendor.setName(vendor.getName()); // Perubahan dari setName()
        existingVendor.setContactPerson(vendor.getContactPerson());
        existingVendor.setPhoneNumber(vendor.getPhoneNumber());
        existingVendor.setAddress(vendor.getAddress());
        existingVendor.setNpwp(vendor.getNpwp()); // Atribut baru
        existingVendor.setNik(vendor.getNik()); // Atribut baru

        // Cek email jika diubah
        if (vendor.getEmail() != null && !existingVendor.getEmail().equalsIgnoreCase(vendor.getEmail())) {
            if (vendorService.emailExists(vendor.getEmail())) {
                bindingResult.rejectValue("email", "error.vendor", "Email vendor sudah digunakan");
            } else {
                existingVendor.setEmail(vendor.getEmail());
            }
        }
        // Tambahkan validasi jika email diubah menjadi kosong
        else if (vendor.getEmail() == null || vendor.getEmail().isBlank()) {
            bindingResult.rejectValue("email", "error.vendor", "Email vendor wajib diisi");
        }


        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        vendorService.saveVendor(existingVendor);
        return ResponseEntity.ok("Vendor berhasil diperbarui");
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteVendorAjax(
            @PathVariable Long id,
            HttpSession session
    ) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        if (!userService.isDirektur(currentUser)) {
            return ResponseEntity.status(403).body("Forbidden: hanya Direktur yang bisa menghapus vendor");
        }

        if (vendorService.getVendorById(id).isEmpty()) {
            return ResponseEntity.status(404).body("Vendor tidak ditemukan");
        }

        vendorService.deleteVendor(id);
        return ResponseEntity.ok("Vendor berhasil dihapus");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVendorById(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Optional<Vendor> vendor = vendorService.getVendorById(id);
        if (vendor.isPresent()) {
            return ResponseEntity.ok(vendor.get());
        } else {
            return ResponseEntity.status(404).body("Vendor tidak ditemukan");
        }
    }
}