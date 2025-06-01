package com.wiloso.admin.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "vendors")
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    private String npwp;

    private String nik;

    @Column(unique = true)
    private String email;

    private String contactPerson;

    private String phoneNumber;

    private LocalDateTime createdTime;

    @OneToMany(mappedBy = "vendor")
    private Set<Order> orders = new HashSet<>();
}
