package com.wiloso.admin.service;

import com.wiloso.admin.model.Vendor;
import com.wiloso.admin.model.User;
import com.wiloso.admin.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class VendorService {

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private UserService userService;

    public Optional<Vendor> findByEmail(String email) {
        return vendorRepository.findByEmail(email);
    }

    public boolean emailExists(String email) {
        return vendorRepository.findByEmail(email).isPresent();
    }

    public void saveVendor(Vendor vendor) {
        if (vendor.getId() == null) {
            vendor.setCreatedTime(LocalDateTime.now());
        }
        vendorRepository.save(vendor);
    }

    public Page<Vendor> getVendors(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (keyword == null || keyword.isBlank()) {
            return vendorRepository.findAll(pageable);
        }

        return vendorRepository.findByNameContainingIgnoreCaseOrContactPersonContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, keyword, pageable);
    }

    public Optional<Vendor> getVendorById(Long id) {
        return vendorRepository.findById(id);
    }

    public void deleteVendor(Long id) {
        vendorRepository.deleteById(id);
    }

    public boolean canManageVendors(User user) {
        return userService.isDirektur(user);
    }
}