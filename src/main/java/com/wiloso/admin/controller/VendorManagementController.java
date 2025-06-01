package com.wiloso.admin.controller;

import com.wiloso.admin.model.User;
import com.wiloso.admin.model.Vendor;
import com.wiloso.admin.service.VendorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/vendors")
public class VendorManagementController {

    @Autowired
    private VendorService vendorService;

    @GetMapping("")
    public String listVendors(
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

        Page<Vendor> vendors = vendorService.getVendors(keyword, page, size);
        model.addAttribute("vendors", vendors.getContent());
        model.addAttribute("currentPage", page);

        int totalPages = vendors.getTotalPages();
        if (totalPages == 0) {
            totalPages = 1;
        }
        model.addAttribute("totalPages", totalPages);

        model.addAttribute("keyword", keyword);
        model.addAttribute("canEdit", vendorService.canManageVendors(currentUser));

        return "vendor-management";
    }
}